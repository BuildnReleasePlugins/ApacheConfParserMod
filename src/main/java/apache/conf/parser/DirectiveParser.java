package apache.conf.parser;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.regex.Pattern;

import apache.conf.global.Const;
import apache.conf.global.Utils;
import apache.conf.modules.SharedModule;
import apache.conf.modules.StaticModule;

/**
 * 
 * This class is used to parse the Apache configuration and obtain directives.
 *
 */
public class DirectiveParser extends Parser {

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
    public DirectiveParser(String rootConfFile, String serverRoot, StaticModule staticModules[], SharedModule sharedModules[]) throws Exception {
        super(rootConfFile, serverRoot, staticModules, sharedModules);
    }

    /**
     * <p>
     * Takes in a directive and puts it into parts
     * 
     * Example: "Listen 80 http" will be split into "Listen" "80" "http"
     * 
     * </p>
     * 
     *  line
     *            the line with parts to extract
     *  an array with the directive parts
     */
    public static String[] extractDirectiveToParts(String line) {
        String strLine = line.replaceAll(Const.replaceCommaSpacesRegex, ",");
        strLine = strLine.replaceAll(Const.replaceSpacesInValuesRegex, "@@");
    
        return strLine.split("@@");
    }
    
    /**
     * <p>
     * Parses all active configuration files for the directive specified by directiveType.
     * </p>
     * 
     *  directiveType
     *            The directive name. This is not case sensitive.
     *  includeVHosts
     *            flag to indicate whether to include directives inside VirtualHosts
     *  an array with all instances of the directive.
     * @throws Exception
     */
    public Directive[] getDirective(String directiveType, boolean includeVHosts) throws Exception {

        ArrayList<Directive> directives = new ArrayList<Directive>();

        boolean loadDefines = true;
        if (directiveType.equals(Const.defineDirective)) {
            loadDefines = false;
        }

        ParsableLine lines[] = getConfigurationParsableLines(loadDefines, includeVHosts);
        String strLine = "";
        for (ParsableLine line : lines) {
            if (line.isInclude()) {

                strLine = line.getConfigurationLine().getProcessedLine();
                
                String directiveValueList[];
                Directive addDirective;

                if (!isCommentMatch(strLine) && isDirectiveMatch(strLine, directiveType)) {
                    
                    addDirective = new Directive(directiveType);

                    directiveValueList = extractDirectiveToParts(strLine);
                    for (int i = 1; i < directiveValueList.length; i++) {
                        addDirective.addValue(directiveValueList[i]);
                    }
                    
                    addDirective.setConfigurationLine(line.getConfigurationLine());

                    directives.add(addDirective);
                }
            }
        }

        return directives.toArray(new Directive[directives.size()]);
    }

    /**
     * <p>
     * Parses all active configuration files for the directive values specified by directiveType.
     * </p>
     * <p>
     * For example if you search for the directive "Listen" and the apache configuration contains the lines:<br/>
     * Listen 80<br/>
     * Listen 443 https<br/>
     * The function will return an array with values "80" and "443 https".
     * </p>
     * 
     *  directiveType
     *            The directive name. This is not case sensitive.
     *  includeVHosts
     *            flag to indicate whether to include directives inside VirtualHosts
     *  gets all of the values of a directive in an array. If one instance of a directive has multiple values then they will be separated by spaces.
     * @throws Exception
     */
    public String[] getDirectiveValue(String directiveType, boolean includeVHosts) throws Exception {
        ArrayList<String> directiveValues = new ArrayList<String>();
        Directive directives[] = getDirective(directiveType, includeVHosts);

        String directiveValueList[];
        String values;

        for (Directive directive : directives) {

            directiveValueList = directive.getValues();

            values = "";
            for (String directiveValue : directiveValueList) {
                values += directiveValue + " ";
            }

            directiveValues.add(values.trim());
        }

        return directiveValues.toArray(new String[directiveValues.size()]);
    }

    /**
     * <p>
     * Inserts a directive string before or after the first found matching directive type.
     * </p>
     * <p>
     * For Example: <br/>
     * If you specify a directive type of "Listen" and a directive String of "Listen 127.0.0.1:80" then this directive String would be inserted after the first "Listen" directive in the configuration.
     * </p>
     * 
     *  directiveType
     *            The directive name. This is not case sensitive.
     *  directiveString
     *            The directive string to insert.
     *  before
     *            a boolean indicating whether the directiveString should be inserted before the first found directive. true for before, false for after.
     *  includeVHosts
     *            flag to indicate whether to search for directives inside VirtualHosts
     *  a boolean indicating if the directive was found.
     * @throws Exception
     */
    public boolean insertDirectiveBeforeOrAfterFirstFound(String directiveType, String directiveString, boolean before, boolean includeVHosts) throws Exception {
        return insertDirectiveBeforeOrAfterFirstFound(directiveType, directiveString, Pattern.compile(".*"), before, includeVHosts);
    }

    /**
     * <p>
     * Inserts a directive string before or after the first found matching directive type and has a value that matches the matchesPattern.
     * </p>
     * <p>
     * For Example: <br/>
     * If you specify a directive type of "Listen", matches "[^0-9]70([^0-9]|)" and a directive String of "Listen 127.0.0.1:80" then this directive String would be inserted after the first "Listen"
     * directive with a value containing the number "70" in the configuration eg. "Listen 70"
     * </p>
     * 
     *  directiveType
     *            The directive name. This is not case sensitive.
     *  directiveString
     *            The directive string to insert.
     *  matchesPattern
     *            A filter that is used to check whether or not the directive matches a certain pattern.
     *  before
     *            a boolean indicating whether the directiveString should be inserted before or after the first found directive. true for before, false for after.
     *  includeVHosts
     *            flag to indicate whether to search for directives inside VirtualHosts
     *  a boolean indicating if the directive was found.
     *
     * @throws Exception
     */
    public boolean insertDirectiveBeforeOrAfterFirstFound(String directiveType, String directiveString, Pattern matchesPattern, boolean before, boolean includeVHosts) throws Exception {

        boolean directiveFound = false;

        String file = getDirectiveFile(directiveType, matchesPattern, includeVHosts);

        if (file != null) {

            directiveFound = true;

            StringBuffer fileText = new StringBuffer();

            ParsableLine lines[] = getFileParsableLines(file, includeVHosts);

            String strLine = "", cmpLine = "";

            boolean found = false;
            for (ParsableLine line : lines) {
                strLine = line.getConfigurationLine().getLine();
                cmpLine = line.getConfigurationLine().getProcessedLine();

                if (found) {
                    fileText.append(strLine + Const.newLine);
                    continue;
                }

                if (!before) {
                    fileText.append(strLine + Const.newLine);
                }

                if (line.isInclude()) {
                    if (!isCommentMatch(cmpLine) && isDirectiveMatch(cmpLine, directiveType)) {
                        if (matchesPattern.matcher(cmpLine).find()) {
                            fileText.append(directiveString + Const.newLine);
                            found = true;
                        }
                    }
                }

                if (before) {
                    fileText.append(strLine + Const.newLine);
                }
            }

            if (found) {
                Utils.writeStringBufferToFile(new File(file), fileText, Charset.forName("UTF-8"));
            }
        }

        return directiveFound;
    }

    /**
     * Parses the Apache active file list looking for the first file with the directive and pattern combination.
     * 
     *  directiveType
     *            The directive name. This is not case sensitive.
     *  matchesPattern
     *            The pattern to match against the directive value.
     *  includeVHosts
     *            flag to indicate whether to include directives in VirtualHosts
     *  the first file that matches the directive type and pattern combination or null if no file is found.
     * @throws Exception
     */
    public String getDirectiveFile(String directiveType, Pattern matchesPattern, boolean includeVHosts) throws Exception {

        ParsableLine lines[] = getConfigurationParsableLines(includeVHosts);

        String strLine = "";
        for (ParsableLine line : lines) {
            if (line.isInclude()) {
                strLine = line.getConfigurationLine().getProcessedLine();

                if (!isCommentMatch(strLine) && isDirectiveMatch(strLine, directiveType)) {
                    if (matchesPattern.matcher(strLine).find()) {
                        return line.getConfigurationLine().getFile();
                    }
                }
            }
        }

        return null;
    }

    /**
     * Goes through the target file and removes any lines that match the directive type and pattern.
     * 
     *  directiveType
     *            The directive name. This is not case sensitive.
     *  file
     *            The target file.
     *  matchesPattern
     *            The pattern to match against the directive value.
     *  commentOut
     *            a boolean indicating if the directive should be commented out rather than completely removed from the file. true to comment out, false to remove.
     *  includeVHosts
     *            flag to indicate whether to search for directives inside VirtualHosts
     *  a boolean indicating if the directive was found.
     *
     * @throws Exception
     */
    public boolean removeDirectiveFromFile(String directiveType, String file, Pattern matchesPattern, boolean commentOut, boolean includeVHosts) throws Exception {

        StringBuffer fileText = new StringBuffer();

        boolean changed = false;

        ParsableLine lines[] = getFileParsableLines(file, includeVHosts);

        String strLine = "", cmpLine = "";
        for (ParsableLine line : lines) {
            strLine = line.getConfigurationLine().getLine();
            cmpLine = line.getConfigurationLine().getProcessedLine();

            if (!isCommentMatch(cmpLine) && isDirectiveMatch(cmpLine, directiveType) && line.isInclude()) {

                if (matchesPattern.matcher(cmpLine).find()) {

                    changed = true;

                    if (commentOut) {
                        fileText.append("#" + strLine + Const.newLine);
                    }
                } else {
                    fileText.append(strLine + Const.newLine);
                }
            } else {
                fileText.append(strLine + Const.newLine);
            }
        }

        if (changed) {
            Utils.writeStringBufferToFile(new File(file), fileText, Charset.forName("UTF-8"));
        }

        return changed;
    }

    /**
     * Goes through the target file and replaces the value of any directives that match the directiveType and matchesPattern with the passed in insertValue.
     * 
     *  directiveType
     *            The directive type. This is not case sensitive.
     *  file
     *            The target file.
     *  insertValue
     *            The value to insert.
     *  matchesPattern
     *            The pattern to match against the directive value.
     *  add
     *            Specifies whether we should add the directive to the file if it doesn't exist. true to add, false otherwise.
     *  includeVHosts
     *            flag to indicate whether to search for directives inside VirtualHosts            
     * @throws Exception
     */
    public void setDirectiveInFile(String directiveType, String file, String insertValue, Pattern matchesPattern, boolean add, boolean includeVHosts) throws Exception {

        StringBuffer fileText = new StringBuffer();

        boolean changed = false;

        ParsableLine lines[] = getFileParsableLines(file, includeVHosts);

        String strLine = "", cmpLine = "";
        for (ParsableLine line : lines) {
            strLine = line.getConfigurationLine().getLine();
            cmpLine = line.getConfigurationLine().getProcessedLine();

            if (!isCommentMatch(cmpLine) && isDirectiveMatch(cmpLine, directiveType) && line.isInclude()) {

                if (matchesPattern.matcher(cmpLine).find()) {

                    changed = true;

                    fileText.append(directiveType + " " + insertValue);
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
            fileText.append(directiveType + " " + insertValue);
            fileText.append(Const.newLine);
        }

        if (changed) {
            Utils.writeStringBufferToFile(new File(file), fileText, Charset.forName("UTF-8"));
        }

    }
    
    //Added by boixmunl
    public void setUniqueDirective(Parser parser, String file, String directiveType, String directiveValue) throws Exception{
        Pattern pattern = Pattern.compile("");
        discommentDirective(parser, file, directiveType);
        Directive directives[] = getDirective(directiveType, true);
        if(directives.length==1) {
            setDirectiveInFile(directiveType, file, directiveValue, pattern, false, true);
        }else if (directives.length==0){
            throw new Exception("No "+directiveType+" has been found");
        }else{
            throw new Exception("More than one Directive called "+directiveType+" has been found");
        }
    }
    
    public void enableDirective(Parser parser, String file, String directiveType, String directiveValue) throws Exception{
        //This method discomments a directive if it is comment or creates it if it doesnt exist
        boolean exist=false;
        Pattern pattern = Pattern.compile(directiveValue);
        ParsableLine[] parsableLines=parser.getConfigurationParsableLines(true);
        for(int i=0;i<parsableLines.length;i++){
            if(parsableLines[i].getConfigurationLine().getLine().contains(directiveValue) && parsableLines[i].getConfigurationLine().getLine().contains("#"+directiveType) && parsableLines[i].getConfigurationLine().isComment()){
                //the directive is located commented
                discommentDirective(parser, file, directiveType, directiveValue);
                exist=true;
            }else if(parsableLines[i].getConfigurationLine().getLine().contains(directiveValue) && parsableLines[i].getConfigurationLine().getLine().contains(directiveType) && !parsableLines[i].getConfigurationLine().isComment()){
                exist=true;
            }
        }
        if(!exist){
            setDirectiveInFile(directiveType, file, directiveValue, pattern, true, true);
        }
    }
    
    public void commentDirective(DirectiveParser dParser,Parser parser, String file, String directiveType, String directiveValue) throws Exception{
        Pattern pattern = Pattern.compile(directiveValue);
        ParsableLine[] parsableLines=parser.getConfigurationParsableLines(true);
        for(int i=0;i<parsableLines.length;i++){
            if(parsableLines[i].getConfigurationLine().getLine().contains(directiveValue) && parsableLines[i].getConfigurationLine().getLine().contains(directiveType) && !parsableLines[i].getConfigurationLine().isComment()){
                dParser.removeDirectiveFromFile(directiveType, file, pattern, true, true);
            }
        }
    }
    
    public boolean getDirectiveStatus(Parser parser, DirectiveParser dParser, String file, String directiveType, String directiveValue) throws Exception{
        boolean status=false;
        ParsableLine[] parsableLines=parser.getFileParsableLines(file, true);
        for(int i=0;i<parsableLines.length;i++){
            if(parsableLines[i].getConfigurationLine().getLine().contains(directiveValue) && parsableLines[i].getConfigurationLine().getLine().contains("#"+directiveType) && parsableLines[i].getConfigurationLine().isComment()){
                //the directive is located commented
                discommentDirective(parser, file, directiveType, directiveValue);
                status=false;
            }else if(parsableLines[i].getConfigurationLine().getLine().contains(directiveValue) && parsableLines[i].getConfigurationLine().getLine().contains(directiveType) && !parsableLines[i].getConfigurationLine().isComment()){
                //the directive is located uncommented
                status=true;
            }else{
                //the directive is not located
                status=false;
            }
        }
        return status;
    }
    
    private void discommentDirective(Parser parser, String file, String directiveType, String directiveValue) throws Exception{
        boolean changed = false;
        StringBuffer fileText = new StringBuffer();
        ParsableLine[] parsableLines=parser.getFileParsableLines(file, true);
        String strLine = "";
        for(ParsableLine line : parsableLines){
            strLine= line.getConfigurationLine().getLine();
            if(line.getConfigurationLine().getLine().contains(directiveValue) && line.getConfigurationLine().getLine().contains("#"+directiveType) && line.getConfigurationLine().isComment()){
                changed = true;
                fileText.append(directiveType + " " + directiveValue);
                fileText.append(Const.newLine);
            }else{
                fileText.append(strLine + Const.newLine);
            }
        }
        if (changed) {
            Utils.writeStringBufferToFile(new File(file), fileText, Charset.forName("UTF-8"));
        }
    }
    
    private void discommentDirective(Parser parser, String file, String directiveType) throws Exception {
        //This method is only for directives who only have 1 entry with the same directiveType
        boolean changed = false;
        StringBuffer fileText = new StringBuffer();
        ParsableLine[] parsableLines=parser.getFileParsableLines(file, true);
        String strLine = "";
        for(ParsableLine line : parsableLines){
            strLine= line.getConfigurationLine().getLine();
            if(line.getConfigurationLine().getLine().contains("#"+directiveType) && line.getConfigurationLine().isComment()){
                changed = true;
                fileText.append(strLine.split("#")[1]);
                fileText.append(Const.newLine);
            }else{
                fileText.append(strLine + Const.newLine);
            }
        }
        if (changed) {
            Utils.writeStringBufferToFile(new File(file), fileText, Charset.forName("UTF-8"));
        }
    }
    
}
