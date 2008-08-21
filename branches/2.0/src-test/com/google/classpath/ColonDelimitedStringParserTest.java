package com.google.classpath;

import java.io.File;
import java.net.URL;
import java.util.List;

import junit.framework.TestCase;

public class ColonDelimitedStringParserTest extends TestCase {
     List<String> list;

    public void testParsesEmptyProperly() throws Exception {
        list = new ColonDelimitedStringParser("")
                .getStrings();
        assertEquals(1, list.size());
        assertEquals("", list.get(0));
    }

    public void testParsesNormally() throws Exception {
        list = new ColonDelimitedStringParser("one" + File.pathSeparator + "two"
        + File.pathSeparator + "three.and.a.half").getStrings();
        assertEquals(3, list.size());
        assertEquals("one", list.get(0));
        assertEquals("two", list.get(1));
        assertEquals("three.and.a.half", list.get(2));
    }

  public void testParseAndAddToClasspathList() throws Exception {
    String classpath = "test-one.jar" + File.pathSeparator;
    List<URL> completeClasspath = new ColonDelimitedStringParser(classpath).getURLs();
    assertStringEndsWith("test-one.jar", completeClasspath.get(0).toString());

    completeClasspath.clear();
    classpath = "lib/one.jar" + File.pathSeparator + "lib/two.jar"
        + File.pathSeparator + "three.jar";
    completeClasspath = new ColonDelimitedStringParser(classpath).getURLs();

    assertStringEndsWith("lib/one.jar", completeClasspath.get(0).toString());
    assertStringEndsWith("lib/two.jar", completeClasspath.get(1).toString());
    assertStringEndsWith("three.jar", completeClasspath.get(2).toString());
  }

  private void assertStringEndsWith(String expectedEnding, String fullToTest) {
    assertTrue(fullToTest, fullToTest.endsWith(expectedEnding));
  }
}
