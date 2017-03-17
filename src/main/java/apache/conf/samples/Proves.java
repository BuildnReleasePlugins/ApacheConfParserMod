/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apache.conf.samples;


import apache.conf.modules.SharedModuleParser;
import apache.conf.modules.StaticModuleParser;
import apache.conf.parser.DirectiveParser;
import apache.conf.parser.EnclosureParser;
import apache.conf.parser.File;
import apache.conf.parser.Parser;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 *
 * @author boixmunl
 */
public class Proves {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        //String rootConfFile = "C:\\Users\\boixmunl\\Desktop\\VirtualBoxInfinity\\Apache24\\conf\\httpd.conf";
        String serverRoot = "C:\\Users\\boixmunl\\Desktop\\VirtualBoxInfinity\\Apache24";
        String binFile = "C:\\Users\\boixmunl\\Desktop\\VirtualBoxInfinity\\Apache24\\manual\\programs\\apachectl.html";
        String parsableFile="C:\\Users\\boixmunl\\Desktop\\httpd.conf";
        String rootConfFile=parsableFile;
        
        StaticModuleParser staticParser = new StaticModuleParser(new File(binFile));
        SharedModuleParser sharedParser = new SharedModuleParser(new File(binFile));

        Parser parser = new Parser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        DirectiveParser dParser= new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        EnclosureParser eParser=new EnclosureParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        
        dParser.setUniqueDirective("ServerRoot", "C:\\Program Files (x86)\\Apache Software Foundation\\Apache2.4");
        dParser.enableDirective("LoadModule", "deflate_module modules/mod_deflate.so");
        dParser.commentDirective("LoadModule", "headers_module modules/mod_headers.so");
        dParser.getDirectiveStatus("LoadModule", "rewrite_module modules/mod_rewrite.so");
        //dParser.lineIsPresent("ServerName", "0.0.0.0");
        dParser.getDirectiveValue(parsableFile, true);
        dParser.getDirectiveValue(parsableFile, parsableFile, true);
        //dParser.setDirectiveIntoEnclosure(parsableFile, parsableFile, serverRoot, ParentMatchesPattern, directiveMatchesPattern, true, true);
    }

}
