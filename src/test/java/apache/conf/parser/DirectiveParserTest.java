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
public class DirectiveParserTest {
    private static String rootConfFile;
    private static String serverRoot;
    private static String binFile;
    private static String parsableFile;
    private static StaticModuleParser staticParser;
    private static SharedModuleParser sharedParser;
    
    public DirectiveParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }

    /**
     * Test of extractDirectiveToParts method, of class DirectiveParser.
     */
    @Ignore
    @Test
    public void testExtractDirectiveToParts() {
        System.out.println("extractDirectiveToParts");
        String line = "";
        String[] expResult = null;
        String[] result = DirectiveParser.extractDirectiveToParts(line);
        assertArrayEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirective method, of class DirectiveParser.
     */
    @Test
    public void testGetDirective() throws Exception {
        System.out.println("getDirective");
        DirectiveParser dParser = new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        String directiveType = "ServerRoot";
        boolean includeVHosts = false;
        Directive[] result = dParser.getDirective(directiveType, includeVHosts);
        String[] dirValue = {"C:\\Program Files (x86)\\Apache Software Foundation\\Apache2.4"};
        Directive[] expResult = {new Directive("ServerRoot", dirValue)};
        assertEquals(expResult[0].toString(), result[0].toString());
    }

    /**
     * Test of getDirectiveValue method, of class DirectiveParser.
     */
    @Test
    public void testGetDirectiveValue_String_boolean() throws Exception {
        System.out.println("getDirectiveValue");
        DirectiveParser dParser = new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        String directiveType = "ServerRoot";
        boolean includeVHosts = false;
        String[] result = dParser.getDirectiveValue(directiveType, includeVHosts);
        String expResult = "ServerRoot C:\\Program Files (x86)\\Apache Software Foundation\\Apache2.4";
        assertEquals(expResult, result[0]);
    }

    /**
     * Test of insertDirectiveBeforeOrAfterFirstFound method, of class DirectiveParser.
     */
    @Test
    public void testInsertDirectiveBeforeOrAfterFirstFound_4args() throws Exception {
        System.out.println("insertDirectiveBeforeOrAfterFirstFound");
        DirectiveParser dParser = new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        String directiveType = "TestDirBefore1";
        String directiveString = "test";
        boolean before = false;
        boolean includeVHosts = true;
        dParser.insertDirectiveBeforeOrAfterFirstFound(directiveType, directiveString, before, includeVHosts);
        assertTrue(dParser.getDirectiveStatus(directiveType, directiveString));
        // TODO review the generated test code and remove the default call to fail.
    }

    /**
     * Test of insertDirectiveBeforeOrAfterFirstFound method, of class DirectiveParser.
     */
    @Test
    public void testInsertDirectiveBeforeOrAfterFirstFound_5args() throws Exception {
        System.out.println("insertDirectiveBeforeOrAfterFirstFound");
        DirectiveParser dParser = new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        String directiveType = "TestDirBefore2";
        String directiveString = "test";
        Pattern matchesPattern = Pattern.compile("C:\\Program Files (x86)\\Apache Software Foundation\\Apache2.4");
        boolean before = false;
        boolean includeVHosts = true;
        dParser.insertDirectiveBeforeOrAfterFirstFound(directiveType, directiveString,matchesPattern, before, includeVHosts);
        assertTrue(dParser.getDirectiveStatus(directiveType, directiveString));
    }

    /**
     * Test of getDirectiveFile method, of class DirectiveParser.
     */
    @Ignore
    @Test
    public void testGetDirectiveFile() throws Exception {
        System.out.println("getDirectiveFile");
        String directiveType = "";
        Pattern matchesPattern = Pattern.compile("C:\\Program Files (x86)\\Apache Software Foundation\\Apache2.4");
        boolean includeVHosts = false;
        DirectiveParser instance = null;
        String expResult = "";
        String result = instance.getDirectiveFile(directiveType, matchesPattern, includeVHosts);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeDirectiveFromFile method, of class DirectiveParser.
     */
    @Test
    public void testRemoveDirectiveFromFile() throws Exception {
        System.out.println("removeDirectiveFromFile");
        DirectiveParser dParser = new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        String directiveType = "TestRemoveDirectiveFromFile";
        String directiveValue = "test";
        boolean includeVHosts = true;
        Pattern matchesPattern = Pattern.compile("test");
        dParser.setDirectiveInFile(directiveType, directiveValue, matchesPattern, true, includeVHosts);
        assertTrue(dParser.getDirectiveStatus(directiveType, directiveValue));
        dParser.removeDirectiveFromFile(directiveType, matchesPattern, true, includeVHosts);
        assertFalse(dParser.getDirectiveStatus(directiveType, directiveValue));
    }

    /**
     * Test of setDirectiveInFile method, of class DirectiveParser.
     */
    @Test
    public void testSetDirectiveInFile() throws Exception {
        System.out.println("setDirectiveInFile");
        DirectiveParser dParser = new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        String directiveType = "TestsetDirectiveInFile";
        String directiveValue = "test";
        boolean includeVHosts = true;
        Pattern matchesPattern = Pattern.compile(directiveValue);
        dParser.setDirectiveInFile(directiveType, directiveValue, matchesPattern, true, includeVHosts);
        assertTrue(dParser.getDirectiveStatus(directiveType, directiveValue));
    }

    /**
     * Test of setUniqueDirective method, of class DirectiveParser.
     */
    @Test
    public void testSetUniqueDirective() throws Exception {
        System.out.println("setUniqueDirective");
        DirectiveParser dParser = new DirectiveParser(rootConfFile, serverRoot, staticParser.getStaticModules(), sharedParser.getSharedModules());
        String directiveType = "TestsetDirectiveInFile";
        String directiveValue = "test";
        boolean includeVHosts = true;
        Pattern matchesPattern = Pattern.compile(directiveValue);
        dParser.setDirectiveInFile(directiveType, directiveValue, matchesPattern, true, includeVHosts);
        assertTrue(dParser.getDirectiveStatus(directiveType, directiveValue));
        directiveType = "TestsetDirectiveInFile";
        directiveValue = "test4444";
        dParser.setUniqueDirective(directiveType, directiveValue);
        assertTrue(dParser.getDirectiveStatus(directiveType, directiveValue));
    }

    /**
     * Test of enableDirective method, of class DirectiveParser.
     */
    @Test
    public void testEnableDirective() throws Exception {
        System.out.println("enableDirective");
        String directiveType = "";
        String directiveValue = "";
        DirectiveParser instance = null;
        instance.enableDirective(directiveType, directiveValue);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of commentDirective method, of class DirectiveParser.
     */
    @Test
    public void testCommentDirective() throws Exception {
        System.out.println("commentDirective");
        String directiveType = "";
        String directiveValue = "";
        DirectiveParser instance = null;
        instance.commentDirective(directiveType, directiveValue);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirectiveStatus method, of class DirectiveParser.
     */
    @Test
    public void testGetDirectiveStatus() throws Exception {
        System.out.println("getDirectiveStatus");
        String directiveType = "";
        String directiveValue = "";
        DirectiveParser instance = null;
        boolean expResult = false;
        boolean result = instance.getDirectiveStatus(directiveType, directiveValue);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of lineIsPresent method, of class DirectiveParser.
     */
    @Test
    public void testLineIsPresent() throws Exception {
        System.out.println("lineIsPresent");
        String line = "";
        DirectiveParser instance = null;
        boolean expResult = false;
        boolean result = instance.lineIsPresent(line);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDirectiveValue method, of class DirectiveParser.
     */
    @Test
    public void testGetDirectiveValue_3args() throws Exception {
        System.out.println("getDirectiveValue");
        String directiveType = "";
        String directiveValueTrigger = "";
        boolean includeVHosts = false;
        DirectiveParser instance = null;
        String expResult = "";
        String result = instance.getDirectiveValue(directiveType, directiveValueTrigger, includeVHosts);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setDirectiveIntoEnclosure method, of class DirectiveParser.
     */
    @Test
    public void testSetDirectiveIntoEnclosure() throws Exception {
        System.out.println("setDirectiveIntoEnclosure");
        String parentEnclosureType = "";
        String directiveType = "";
        String directiveString = "";
        Pattern ParentMatchesPattern = null;
        Pattern directiveMatchesPattern = null;
        boolean add = false;
        boolean includeVHosts = false;
        DirectiveParser instance = null;
        instance.setDirectiveIntoEnclosure(parentEnclosureType, directiveType, directiveString, ParentMatchesPattern, directiveMatchesPattern, add, includeVHosts);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
