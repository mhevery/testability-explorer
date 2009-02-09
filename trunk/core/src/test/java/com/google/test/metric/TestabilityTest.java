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

public class TestabilityTest extends AutoFieldClearTestCase {

  private WatchedOutputStream out = new WatchedOutputStream();
  private WatchedOutputStream err = new WatchedOutputStream();
  private CommandLineConfig commandLineConfig;
  private Testability testability;

  @Override
  protected void setUp() {
    commandLineConfig = new CommandLineConfig(new PrintStream(out), new PrintStream(err));
    testability = new Testability(new PrintStream(err), commandLineConfig);
  }

  public void testParseNoArgs() {
    testability.run();
    String expectedStartOfError = "Argument \"classes and packages to analyze\" is required";
    assertEquals(expectedStartOfError, err.toString().substring(0, expectedStartOfError.length()));
    assertTrue(err.toString().indexOf("Exiting...") > -1);
  }

  public void testParseClasspathAndSingleClass() throws Exception {
    testability.run("-cp", "not/default/path", "com.google.TestClass");

    assertEquals("", err.toString());
    assertEquals("not/default/path", commandLineConfig.cp);
    List<String> expectedArgs = new ArrayList<String>();
    expectedArgs.add("com.google.TestClass");
    assertNotNull(commandLineConfig.entryList);
    assertEquals(expectedArgs, commandLineConfig.entryList);
  }

  public void testParseMultipleClassesAndPackages() throws Exception {
	    testability.run("-cp", "not/default/path", 
	    		"com.google.FirstClass", 
	    		"com.google.second.package", 
	    		"com.google.third.package");

	    assertEquals("", err.toString());
	    assertEquals("not/default/path", commandLineConfig.cp);
	    List<String> expectedArgs = new ArrayList<String>();
	    expectedArgs.add("com.google.FirstClass");
	    expectedArgs.add("com.google.second.package");
	    expectedArgs.add("com.google.third.package");
	    assertNotNull(commandLineConfig.entryList);
	    assertEquals(expectedArgs, commandLineConfig.entryList);
	  }

  /*
   * If the main() method is called directly by another class,
   * as in the case of the TestabilityTask for Ant,
   * multiple classpaths may be passed as a single String arg
   * separated by spaces (" ")
   */
  public void testParseMultipleClassesAndPackagesSingleArg() throws Exception {
	    testability.run("-cp", "not/default/path", 
	    		"com.google.FirstClass com.google.second.package com.google.third.package");

	    assertEquals("", err.toString());
	    assertEquals("not/default/path", commandLineConfig.cp);
	    List<String> expectedArgs = new ArrayList<String>();
	    expectedArgs.add("com.google.FirstClass");
	    expectedArgs.add("com.google.second.package");
	    expectedArgs.add("com.google.third.package");
	    assertNotNull(commandLineConfig.entryList);
	    assertEquals(expectedArgs, commandLineConfig.entryList);
	  }

  public void testParseComplexityAndGlobal() throws Exception {
	    testability.run("-cp", "not/default/path", 
	    		"-cyclomatic", "10",
	    		"-global", "1",
	    		"com.google.TestClass");

	    assertEquals("", err.toString());
	    assertEquals("Classpath", "not/default/path", commandLineConfig.cp);
	    List<String> expectedArgs = new ArrayList<String>();
	    expectedArgs.add("com.google.TestClass");
	    assertNotNull(commandLineConfig.entryList);
	    assertEquals(expectedArgs, commandLineConfig.entryList);
	    assertEquals("Cyclomatic", 10.0, commandLineConfig.cyclomaticMultiplier);
	    assertEquals("Global", 1.0, commandLineConfig.globalMultiplier);
	  }

  public void testJarFileNoClasspath() throws Exception {
    Testability.main(new PrintStream(err), commandLineConfig, "junit.runner", "-cp");
    /**
     * we expect the error to say something about proper usage of the arguments.
     * The -cp needs a value
     */
    assertTrue(out.toString().length() == 0);
    assertTrue(err.toString().length() > 0);
  }

  public void testParseSrcFileUrlFlags() throws Exception {
    String lineUrl = "http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}#{line}";
    String fileUrl = "http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}";
    testability.run("-srcFileLineUrl", lineUrl, "-srcFileUrl", fileUrl);
    assertEquals(lineUrl, commandLineConfig.srcFileLineUrl);
    assertEquals(fileUrl, commandLineConfig.srcFileUrl);
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
