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
package com.google.classpath;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class DirectoryClasspathRootTest extends TestCase {

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

  public void testCreateNewDirectoryClasspathRoot() throws Exception {
    File dir = new File(CLASS_NO_EXTERNAL_DEPS);
    assertTrue(dir.isDirectory());
    ClasspathRoot root = ClasspathRootFactory.makeClasspathRoot(dir, "");
    assertNotNull(root);
    assertTrue(root instanceof DirectoryClasspathRoot);
  }

  public void testCreateNewJarsClasspathRootTest() throws Exception {
    final String cp = CLASS_NO_EXTERNAL_DEPS + File.pathSeparator +
      CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES;
    ClasspathRootGroup group = ClasspathRootFactory.makeClasspathRootGroup(cp);
    assertNotNull(group);
    assertEquals(2, group.getGroupCount());
    ArrayList<String> packageFilter = new ArrayList<String>();
    packageFilter.add("");
    List<String> names = group.getClassNamesToEnter(packageFilter);
    assertTrue(names.toString(), names.contains("com.google.classpath.ColonDelimitedStringParser"));
    assertTrue(names.toString(), names.contains("com.google.test.metric.AutoFieldClearTestCase"));
    assertTrue(names.toString(), names.contains("com.google.test.metric.ClassInfoTest"));
    assertTrue(names.toString(), names.contains("com.google.test.metric.x.SelfTest"));

    String wantedResource = "com/google/test/metric/x/SelfTest.class";
    InputStream is = group.getResourceAsStream(wantedResource);
    assertNotNull(is);
  }

  public void testWeirdBehaviorWithSpacesInFilenames() throws Exception {
    File file = new File("one and two");
    assertEquals("one and two", file.getName());
  }

}
