package com.google.classpath;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class JarClasspathRootTest extends TestCase {
  /**
   * Directories to be used for testing that contains class files, for testing.
   * These are included in subversion so that any checkout will have a consistent
   * environment for testing.
   */
  public static final String JUNIT_JAR = DirectoryClasspathRootTest.CLASSES_FOR_TEST + "/lib/junit.jar";
  public static final String ASM_JAR = DirectoryClasspathRootTest.CLASSES_FOR_TEST + "/lib/asm-3.0.jar";
  public static final String JARJAR_JAR = DirectoryClasspathRootTest.CLASSES_FOR_TEST + "/lib/jarjar.jar";

  public void testCreateNewJarClasspathRootTest() throws Exception {
    File jar = new File(ASM_JAR);
    assertTrue(jar.isFile());
    ClasspathRoot root = ClasspathRootFactory.makeClasspathRoot(jar, "");
    assertNotNull(root);
    assertTrue(root instanceof JarClasspathRoot);
  }

  public void testCreateNewJarsClasspathRootTest() throws Exception {
    String classpath = ASM_JAR + ":" + JUNIT_JAR + ":" + JARJAR_JAR;
    ClasspathRootGroup group = ClasspathRootFactory.makeClasspathRootGroup(classpath);
    assertNotNull(group);
    assertEquals(3, group.getGroupCount());
    ArrayList<String> packageFilter = new ArrayList<String>();
    packageFilter.add("");
    List<String> names = group.getClassNamesToEnter(packageFilter);
    assertTrue(names.contains("junit.runner.Sorter"));
    assertTrue(names.contains("junit.textui.TestRunner"));
    assertTrue(names.contains("com.tonicsystems.jarjar.ext_util.JarProcessor"));

    String wantedResource = "com/tonicsystems/jarjar/asm/ClassReader.class";
    InputStream isTest = group.getResourceAsStream(wantedResource);
    assertNotNull(isTest);
  }

}
