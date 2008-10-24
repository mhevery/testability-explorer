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

import static com.google.classpath.DirectoryClasspathRootTest.CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES;
import static com.google.classpath.DirectoryClasspathRootTest.CLASSES_EXTERNAL_DEPS_NO_SUPERCLASSES;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;

public class TestabilityTest extends AutoFieldClearTestCase {
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
    assertTrue(out.toString().length() > 0);
    assertTrue(err.toString().length() > 0);
    assertTrue(err.toString().startsWith("WARNING: class not found: "));
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
