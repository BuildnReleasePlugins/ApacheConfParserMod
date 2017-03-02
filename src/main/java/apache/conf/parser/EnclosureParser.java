package apache.conf.parser;

import apache.conf.global.Const;
import apache.conf.global.Utils;
import apache.conf.modules.SharedModule;
import apache.conf.modules.StaticModule;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;

/**
 * 
 * This class is used to parse the Apache configuration and obtain enclosures.
 *
 */
public class EnclosureParser extends Parser {

    /**
     *  rootConfFile
     *            the Apache root configuration file.
     *  serverRoot
     *            the Apache server root
     *  staticModules
     *  sharedModules
     * @throws Exception
     *             if the rootConfFile or serverRoot do not exist
     */
    public EnclosureParser(String rootConfFile, String serverRoot, StaticModule staticModules[], SharedModule sharedModules[]) throws Exception {
        super(rootConfFile, serverRoot, staticModules, sharedModules);
    }

    /**
     * <p>
     * Takes in an enclosure and puts it into parts
     * 
     * 
     * Example: "<VirtualHost *:80>" will be split into "VirtualHost" "80"
     * 
     * </p>
     * 
     *  line
     *            the line with parts to extract
     *  an array with the directive parts
     */
    public static String[] extractEnclosureToParts(String line) {
        String strLine = line.replaceAll(">|<", "");
        strLine = strLine.replaceAll(Const.replaceCommaSpacesRegex, ",");
        strLine = strLine.replaceAll(Const.replaceSpacesInValuesRegex, "@@");
        
        return strLine.split("@@");
    }
    
    /**
     * <p>
     * Parses all active configuration files for the enclosure specified by enclosureType.
     * </p>
     * <p>
     * For example if you search for "VirtualHost" this function will return all VirtualHosts in the configuration.
     * </p>
     * 
     *  enclosureType
     *            The enclosure name. This is not case sensitive.
     *  includeVHosts
     *            flag to indicate whether to include enclosures in VirtualHosts
     *  An array with all matching enclosures.
     * @throws Exception
     */
    public Enclosure[] getEnclosure(String enclosureType, boolean includeVHosts) throws Exception {

        ArrayList<Enclosure> enclosures = new ArrayList<Enclosure>();

        ParsableLine lines[] = getConfigurationParsableLines(includeVHosts);

        String strLine;
        ArrayList<ParsableLine> parsableLines = new ArrayList<ParsableLine>();

        Stack enclosureStack = new Stack();
        for (ParsableLine line : lines) {
            if (line.isInclude()) {
                strLine = line.getConfigurationLine().getProcessedLine();

                if (!isCommentMatch(strLine) && isEnclosureTypeMatch(strLine, enclosureType)) {
                    enclosureStack.push(strLine);
                }
                if (!enclosureStack.isEmpty()) {
                    if (!isCommentMatch(strLine) && !strLine.equals("")) {
                        parsableLines.add(line);
                    }

                    if (!isCommentMatch(strLine) && isCloseEnclosureTypeMatch(strLine, enclosureType)) {
                        enclosureStack.pop();

                        if (enclosureStack.isEmpty()) {
                            enclosures.add(parseEnclosure(parsableLines.toArray(new ParsableLine[parsableLines.size()]), includeVHosts));
                            parsableLines.clear();
                        }
                    }
                }
            }
        }

        return enclosures.toArray(new Enclosure[enclosures.size()]);
    }

    public Enclosure parseEnclosure(ParsableLine[] parsableLines, boolean includeVHosts) throws Exception {

        String strLine;
        Enclosure enclosure = new Enclosure();

        Stack enclosureStack = new Stack();
        ArrayList<ParsableLine> subParsableLines = new ArrayList<ParsableLine>();

        int iter = 0;
        for (ParsableLine parsableLine : parsableLines) {
            iter++;

            strLine = parsableLine.getConfigurationLine().getProcessedLine();

            if(enclosureStack.isEmpty()) {
                enclosure.addConfigurationLine(parsableLine.getConfigurationLine());
            }
            
            if (iter == 1) {
                
                String enclosureValues[] = extractEnclosureToParts(strLine);
                enclosure.setType(enclosureValues[0]);
                StringBuffer enclosureValue = new StringBuffer();
                for (int j = 1; j < enclosureValues.length; j++) {
                    enclosureValue.append(enclosureValues[j] + " ");
                }
                enclosure.setValue(enclosureValue.toString().trim());
                
            } else {
                if (!isCommentMatch(strLine) && isEnclosureMatch(strLine)) {
                    enclosureStack.push(strLine);
                }
                if (!enclosureStack.isEmpty()) {
                    if (!isCommentMatch(strLine)) {
                        subParsableLines.add(parsableLine);
                    }

                    if (!isCommentMatch(strLine) && isCloseEnclosureMatch(strLine)) {
                        enclosureStack.pop();

                        if (enclosureStack.isEmpty()) {
                            enclosure.addEnclosure(parseEnclosure(subParsableLines.toArray(new ParsableLine[subParsableLines.size()]), includeVHosts));
                            subParsableLines.clear();
                        }
                    }
                } else if (!isCommentMatch(strLine) && !isCloseEnclosureMatch(strLine)) {
                    
                    strLine = strLine.replaceAll(Const.replaceCommaSpacesRegex, ",");
                    strLine = strLine.replaceAll(Const.replaceSpacesInValuesRegex, "@@");
                    
                    String directiveValues[] = strLine.split("@@");
                    
                    Directive directive = new Directive(directiveValues[0]);
                    for (int j = 1; j < directiveValues.length; j++) {
                        directive.addValue(directiveValues[j]);
                    }
                    enclosure.addDirective(directive);
                    
                } 
            }
        }

        return enclosure;
    }

    /**
     * Removes all Enclosures from the active configuration that match the enclosure type and enclosure value pattern.
     * 
     *  enclosureType
     *            The enclosure name. This is not case sensitive.
     *  matchesValuePattern
     *            The pattern to match the enclosure value against
     *  commentOut
     *            true to comment out the matching enclosure, false to remove the enclosure
     *  includeVHosts
     *            boolean indicating whether to search for enclosures inside virtual hosts
     * @throws Exception
     */
    public void deleteEnclosure(String enclosureType, Pattern matchesValuePattern, boolean commentOut, boolean includeVHosts) throws Exception {

        String includedFiles[] = getActiveConfFileList();

        boolean changed = false;
        StringBuffer fileText = new StringBuffer();

        ParsableLine lines[];

        for (String file : includedFiles) {

            fileText.delete(0, fileText.length());

            changed = false;

            lines = getFileParsableLines(file, includeVHosts);

            Stack enclosureStack = new Stack();
            String strLine = "", cmpLine = "";
            for (ParsableLine line : lines) {
                strLine = line.getConfigurationLine().getLine();
                cmpLine = line.getConfigurationLine().getProcessedLine();

                if (!isCommentMatch(cmpLine) && isEnclosureTypeMatch(cmpLine, enclosureType) && line.isInclude()) {

                    if (matchesValuePattern.matcher(cmpLine).find()) {

                        enclosureStack.push(cmpLine);
                        changed = true;
                    }
                }

                if (!enclosureStack.isEmpty()) {
                    if (!isCommentMatch(cmpLine) && isCloseEnclosureTypeMatch(cmpLine, enclosureType)) {
                        enclosureStack.pop();
                    }

                    if (!cmpLine.startsWith("#") && commentOut) {
                        fileText.append("#" + strLine + Const.newLine);
                    }
                } else {
                    fileText.append(strLine + Const.newLine);
                }
            }

            if (changed) {
                Utils.writeStringBufferToFile(new File(file), fileText, Charset.forName("UTF-8"));
            }
        }
    }

    //Added by boixmunl
    public void setUniqueEnclosureHeader(Parser parser, String file, String enclosureType, String enclosureValue) throws Exception{
        Pattern pattern = Pattern.compile("");
        Enclosure enclosures[] = getEnclosure(enclosureType, true);
        if(enclosures.length==1) {
            setEnclosureInFile(enclosureType, file, enclosureValue, pattern, false, true);
        }else if (enclosures.length==0){
            throw new Exception("No "+enclosureType+" has been found");
        }else{
            throw new Exception("More than one Enclosure called "+enclosureType+" has been found");
        }
    }
    
    public void setEnclosureHeader(Parser parser, String file, String enclosureType, String enclosureNewValue, String enclosureOldValue) throws Exception{
        Pattern pattern = Pattern.compile(enclosureOldValue);
        setEnclosureInFile(enclosureType, file, enclosureNewValue, pattern, false,true);
    }
    
    public void setEnclosureInFile(String enclosureType, String file, String insertValue, Pattern matchesPattern, boolean add, boolean includeVHosts) throws Exception {

        StringBuffer fileText = new StringBuffer();

        boolean changed = false;

        ParsableLine lines[] = getFileParsableLines(file, includeVHosts);

        String strLine = "", cmpLine = "";
        for (ParsableLine line : lines) {
            strLine = line.getConfigurationLine().getLine();
            cmpLine = line.getConfigurationLine().getProcessedLine();
            if (line.getConfigurationLine().getLine().contains("<"+enclosureType) && cmpLine.contains(matchesPattern.toString())) {
                if (matchesPattern.matcher(cmpLine).find()) {
                    changed = true;
                    fileText.append("<"+enclosureType + " " + insertValue+">");
                    fileText.append(Const.newLine);
                } else {
                    fileText.append(strLine + Const.newLine);
                }
            } else {
                fileText.append(strLine + Const.newLine);
            }
        }

        if (!changed && add) {

            changed = true;

            fileText.append(Const.newLine);
            fileText.append(enclosureType + " " + insertValue);
            fileText.append(Const.newLine);
        }

        if (changed) {
            Utils.writeStringBufferToFile(new File(file), fileText, Charset.forName("UTF-8"));
        }
    }
}
