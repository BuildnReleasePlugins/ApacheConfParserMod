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
import org.junit.Ignore;

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
    @Ignore
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
    @Ignore
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
    @Ignore
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
        EnclosureParser eParser = new EnclosureParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        System.out.println("deleteEnclosure");
        //
        String enclosureType = "TestEnclosureDelete";
        String enclosureValue = "Test Value";
        eParser.setEnclosureInFile(enclosureType, enclosureValue, Pattern.compile(""), true, true);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
        Pattern matchesPattern = Pattern.compile(enclosureValue);
        eParser.deleteEnclosure(enclosureType, matchesPattern, true, true);
        assertFalse("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
    }

    /**
     * Test of setUniqueEnclosure method, of class EnclosureParser.
     */
    @Test
    public void testSetUniqueEnclosure() throws Exception {
        EnclosureParser eParser = new EnclosureParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        System.out.println("setUniqueEnclosure Test starts");
        //
        String enclosureType = "TestUniqueEnclosure";
        String enclosureValue = "Test Value";
        eParser.setEnclosureInFile(enclosureType, enclosureValue, Pattern.compile(""), true, true);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
        
        enclosureType = "TestUniqueEnclosure";
        enclosureValue = "prova";
        eParser.setUniqueEnclosure(enclosureType, enclosureValue);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
    }

    /**
     * Test of setEnclosureInFile method, of class EnclosureParser.
     */
    @Test
    public void testSetEnclosureInFile() throws Exception {
        System.out.println("setEnclosureInFile");
        String enclosureType = "TestEnclosureInFile";
        String insertValue = "TestEnclosureValue";
        Pattern matchesPattern = Pattern.compile("");
        boolean add = true;
        boolean includeVHosts = true;
        EnclosureParser eParser = new EnclosureParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        //
        String enclosureValue = "Test Value";
        eParser.setEnclosureInFile(enclosureType, enclosureValue, matchesPattern, add, includeVHosts);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
    }

    /**
     * Test of insertEnclosureIntoEnclosure method, of class EnclosureParser.
     */
    @Test
    public void testInsertEnclosureIntoEnclosure() throws Exception {
                EnclosureParser eParser = new EnclosureParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());

        System.out.println("insertEnclosureIntoEnclosure");
        String enclosureType = "TestEnclosureIntoEnclosure";
        String enclosureValue = "Test Value";
        eParser.setEnclosureInFile(enclosureType, enclosureValue, Pattern.compile(""), true, true);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
        Pattern matchesPattern = Pattern.compile("Test Value");
        String cildEnclosure = "TestEnclosure2";
        String enclosureString = "prova";
        eParser.insertEnclosureIntoEnclosure(enclosureType, cildEnclosure, enclosureString, matchesPattern, true);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(cildEnclosure, enclosureString));
    }

    /**
     * Test of getEnclosureStatus method, of class EnclosureParser.
     */
    @Test
    public void testGetEnclosureStatus() throws Exception {
        EnclosureParser eParser = new EnclosureParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        System.out.println("getEnclosureStatus");
        String enclosureType = "TestEnclosureStatus";
        String enclosureValue = "Test Value";
        eParser.setEnclosureInFile(enclosureType, enclosureValue, Pattern.compile(""), true, true);
        assertTrue("setUniqueEnclosure Fails",eParser.getEnclosureStatus(enclosureType, enclosureValue));
    }
    
}
