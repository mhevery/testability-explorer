/*
 * Copyright 2007 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.test.metric;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;

public class TestabilityTest extends AutoFieldClearTestCase {
  /**
   * Directories to be used for testing that contains class files, for testing.
   * These are included in subversion so that any checkout will have a consistent
   * environment for testing.
   */
  public static final String CLASSES_FOR_TEST = "classes-for-test";

  /**
   * Directory root that contains one class with no external
   * dependencies.
   */
  public static final String CLASS_NO_EXTERNAL_DEPS = CLASSES_FOR_TEST +
    "/root1";

  /**
   * Directory root containing classes that extend from, and reference, external
   * classes outside of this directory.
   */
  public static final String CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES =
    CLASSES_FOR_TEST + "/root2";

  /**
   * Directory root containing classes extending from Object that reference
   * external classes outside of this directory.
   */
  public static final String CLASSES_EXTERNAL_DEPS_NO_SUPERCLASSES =
    CLASSES_FOR_TEST + "/root3";

  private WatchedOutputStream out;
  private WatchedOutputStream err;
  private Testability testability;

  @Override
  protected void setUp() {
    out = new WatchedOutputStream();
    err = new WatchedOutputStream();
    testability = new Testability(new PrintStream(out), new PrintStream(err));
  }

  public void testParseNoArgs() {
    try {
      testability.parseArgs();
      fail("Should have thrown a CmdLineException exception");
    } catch (CmdLineException expected) {
    }
    assertTrue(err.toString().indexOf(
        "Argument \"classes and packages\" is required") > -1);
  }

  public void testParseClasspathAndSingleClass() throws Exception {
    testability.parseArgs("-cp", "not/default/path", "com.google.TestClass");

    assertEquals("", err.toString());
    assertEquals("not/default/path", testability.cp);
    List<String> expectedArgs = new ArrayList<String>();
    expectedArgs.add("com.google.TestClass");
    assertNotNull(testability.entryList);
    assertEquals(expectedArgs, testability.entryList);
  }

  public void testParseMultipleClassesAndPackages() throws Exception {
	    testability.parseArgs("-cp", "not/default/path", 
	    		"com.google.FirstClass", 
	    		"com.google.second.package", 
	    		"com.google.third.package");

	    assertEquals("", err.toString());
	    assertEquals("not/default/path", testability.cp);
	    List<String> expectedArgs = new ArrayList<String>();
	    expectedArgs.add("com.google.FirstClass");
	    expectedArgs.add("com.google.second.package");
	    expectedArgs.add("com.google.third.package");
	    assertNotNull(testability.entryList);
	    assertEquals(expectedArgs, testability.entryList);
	  }

  /*
   * If the main() method is called directly by another class,
   * as in the case of the TestabilityTask for Ant,
   * multiple classpaths may be passed as a single String arg
   * separated by spaces (" ")
   */
  public void testParseMultipleClassesAndPackagesSingleArg() throws Exception {
	    testability.parseArgs("-cp", "not/default/path", 
	    		"com.google.FirstClass com.google.second.package com.google.third.package");

	    assertEquals("", err.toString());
	    assertEquals("not/default/path", testability.cp);
	    List<String> expectedArgs = new ArrayList<String>();
	    expectedArgs.add("com.google.FirstClass");
	    expectedArgs.add("com.google.second.package");
	    expectedArgs.add("com.google.third.package");
	    assertNotNull(testability.entryList);
	    assertEquals(expectedArgs, testability.entryList);
	  }

  public void testParseComplexityAndGlobal() throws Exception {
	    testability.parseArgs("-cp", "not/default/path", 
	    		"-cyclomatic", "10",
	    		"-global", "1",
	    		"com.google.TestClass");

	    assertEquals("", err.toString());
	    assertEquals("Classpath", "not/default/path", testability.cp);
	    List<String> expectedArgs = new ArrayList<String>();
	    expectedArgs.add("com.google.TestClass");
	    assertNotNull(testability.entryList);
	    assertEquals(expectedArgs, testability.entryList);
	    assertEquals("Cyclomatic", 10.0, testability.cyclomaticMultiplier);
	    assertEquals("Global", 1.0, testability.globalMultiplier);
	  }

  public void testJarFileNoClasspath() throws Exception {
    Testability.main(new PrintStream(out), new PrintStream(err),
        "junit.runner", "-cp");
    /**
     * we expect the error to say something about proper usage of the arguments.
     * The -cp needs a value
     */
    assertTrue(out.toString().length() == 0);
    assertTrue(err.toString().length() > 0);
  }

  public void testClassesNotInClasspath() throws Exception {
    testability.cp = CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES;
    testability.execute();
    assertTrue(out.toString().length() > 0);
    assertTrue(err.toString().length() > 0);
  }

  /*
   * The given classpath contains some classes from this project, but not all.
   * There are many references to classes that will not be in this test's -cp
   * classpath. This test verifies that when the ClassRepository encounters a
   * ClassNotFoundException, it continues nicely and prints the values for the
   * classes that it <em>does</em> find.
   */
  public void testIncompleteClasspath() throws Exception {
    testability.cp = CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES;
    testability.execute();
    assertTrue(out.toString(), out.toString().length() > 0);
    assertTrue(err.toString(), err.toString().length() > 0);
  }

  /*
   * Tries calculating the cost for classes that reference other classes not in
   * the classpath.
   */
  public void testForWarningWhenClassesRecurseToIncludeClassesOutOfClasspath()
      throws Exception {
    testability.cp = CLASSES_EXTERNAL_DEPS_NO_SUPERCLASSES;
    testability.printDepth = 1;
    testability.execute();
    assertTrue(out.toString(), out.toString().length() > 0);
    assertTrue(err.toString(), err.toString().length() > 0);
    assertTrue(err.toString(), err.toString().startsWith("WARNING: class not found: "));
  }

  /*
   * Tries calculating the cost for classes that extend from another class,
   * which does not exist in the classpath.
   */
  public void testForWarningWhenClassExtendsFromClassOutOfClasspath()
      throws Exception {
    testability.cp = CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES;
    testability.printDepth = 1;
    testability.execute();
    assertTrue(out.toString().length() > 0);
    assertTrue(err.toString().length() > 0);
    assertTrue(err.toString().startsWith("WARNING: can not analyze class "));
  }

  public void testParseSrcFileUrlFlags() throws Exception {
    String lineUrl = "http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}#{line}";
    String fileUrl = "http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}";
    testability.parseArgs("", "-srcFileLineUrl", lineUrl, "-srcFileUrl", fileUrl);
    assertEquals(lineUrl, testability.srcFileLineUrl);
    assertEquals(fileUrl, testability.srcFileUrl);
  }

  public static class WatchedOutputStream extends OutputStream {
    StringBuffer sb = new StringBuffer(5000);

    @Override
    public void write(int ch) {
      sb.append(ch);
    }

    @Override
    public void write(byte[] b) {
      sb.append(new String(b));
    }

    @Override
    public void write(byte[] b, int off, int len) {
      sb.append(new String(b, off, len));
    }

    @Override
    public String toString() {
      return sb.toString();
    }

    public void clear() {
      sb = new StringBuffer(5000);
    }

  }
}
