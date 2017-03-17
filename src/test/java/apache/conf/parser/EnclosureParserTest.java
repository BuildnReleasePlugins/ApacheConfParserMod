/*
 * Copyright 2017 boixmunl.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package apache.conf.parser;

import apache.conf.modules.SharedModuleParser;
import apache.conf.modules.StaticModuleParser;
import java.util.regex.Pattern;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author boixmunl
 */
public class EnclosureParserTest {
    private static String rootConfFile;
    private static String serverRoot;
    private static String binFile;
    private static String parsableFile;
    private static StaticModuleParser staticParser;
    private static SharedModuleParser sharedParser;
    
    public EnclosureParserTest() {
        
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
        System.out.println("* UtilsJUnit3Test: setUp() method");
        rootConfFile="C:\\Users\\boixmunl\\Desktop\\httpd.conf";
        serverRoot = "C:\\Users\\boixmunl\\Desktop\\VirtualBoxInfinity\\Apache24";
        binFile = "C:\\Users\\boixmunl\\Desktop\\VirtualBoxInfinity\\Apache24\\manual\\programs\\apachectl.html";
        parsableFile="C:\\Users\\boixmunl\\Desktop\\httpd.conf";
        rootConfFile=parsableFile;
        staticParser = new StaticModuleParser(new File(binFile));
        sharedParser = new SharedModuleParser(new File(binFile));
    }
    
    @AfterClass
    public static void tearDownClass() {
            System.out.println("* UtilsJUnit3Test: tearDown() method");
    }

    /**
     * Test of extractEnclosureToParts method, of class EnclosureParser.
     */
    @Test
    public void testExtractEnclosureToParts() {
        System.out.println("extractEnclosureToParts");
        String line = "";
        String[] expResult = null;
        String[] result = EnclosureParser.extractEnclosureToParts(line);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnclosure method, of class EnclosureParser.
     */
    @Test
    public void testGetEnclosure() throws Exception {
        System.out.println("getEnclosure");
        String enclosureType = "";
        boolean includeVHosts = false;
        EnclosureParser instance = null;
        Enclosure[] expResult = null;
        Enclosure[] result = instance.getEnclosure(enclosureType, includeVHosts);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of parseEnclosure method, of class EnclosureParser.
     */
    @Test
    public void testParseEnclosure() throws Exception {
        System.out.println("parseEnclosure");
        ParsableLine[] parsableLines = null;
        boolean includeVHosts = false;
        EnclosureParser instance = null;
        Enclosure expResult = null;
        Enclosure result = instance.parseEnclosure(parsableLines, includeVHosts);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteEnclosure method, of class EnclosureParser.
     */
    @Test
    public void testDeleteEnclosure() throws Exception {
        System.out.println("deleteEnclosure");
        String enclosureType = "";
        Pattern matchesValuePattern = null;
        boolean commentOut = false;
        boolean includeVHosts = false;
        EnclosureParser instance = null;
        instance.deleteEnclosure(enclosureType, matchesValuePattern, commentOut, includeVHosts);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setUniqueEnclosure method, of class EnclosureParser.
     */
    @Test
    public void testSetUniqueEnclosure() throws Exception {
        EnclosureParser eParser = new EnclosureParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        System.out.println("setUniqueEnclosure Test starts");
        //
        String enclosureType = "ServerRoot";
        String enclosureValue = "\"c:/Apache24\"";
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
        
        enclosureType = "ServerRoot";
        enclosureValue = "\"C:\\Program Files (x86)\\Apache Software Foundation\\Apache2.4\"";
        eParser.setUniqueEnclosure(enclosureType, enclosureValue);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
    }

    /**
     * Test of setEnclosureInFile method, of class EnclosureParser.
     */
    @Test
    public void testSetEnclosureInFile() throws Exception {
        System.out.println("setEnclosureInFile");
        String enclosureType = "";
        String insertValue = "";
        Pattern matchesPattern = null;
        boolean add = false;
        boolean includeVHosts = false;
        EnclosureParser instance = null;
        instance.setEnclosureInFile(enclosureType, insertValue, matchesPattern, add, includeVHosts);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of insertEnclosureIntoEnclosure method, of class EnclosureParser.
     */
    @Test
    public void testInsertEnclosureIntoEnclosure() throws Exception {
        System.out.println("insertEnclosureIntoEnclosure");
        String parentEnclosureType = "";
        String childEnclosureType = "";
        String enclosureString = "";
        Pattern ParentMatchesPattern = null;
        boolean includeVHosts = false;
        EnclosureParser instance = null;
        instance.insertEnclosureIntoEnclosure(parentEnclosureType, childEnclosureType, enclosureString, ParentMatchesPattern, includeVHosts);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getEnclosureStatus method, of class EnclosureParser.
     */
    @Test
    public void testGetEnclosureStatus() throws Exception {
        System.out.println("getEnclosureStatus");
        String enclosureType = "";
        String enclosureValue = "";
        EnclosureParser instance = null;
        boolean expResult = false;
        boolean result = instance.getEnclosureStatus(enclosureType, enclosureValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
