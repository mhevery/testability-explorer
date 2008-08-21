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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Builds a ClasspathRoot instance, depending on if you pass in a jar or directory.
 * Inspired by the <a href="http://code.google.com/p/google-singleton-detector">Google
 * Singleton Detector</a>.
 */
public class ClasspathRootFactory {

  private ClasspathRootFactory() {
    // do not instantiate
  }

  public static ClasspathRootGroup makeClasspathRootGroup(String classpath) {
    List<File> classpathElements = new ColonDelimitedStringParser(classpath).getFiles();
    List<ClasspathRoot> roots = new ArrayList<ClasspathRoot>();
    for (File fileOrJar: classpathElements) {
      try {
        roots.add(makeClasspathRoot(fileOrJar, classpath));
      } catch (MalformedURLException e) {
        throw new RuntimeException("Error while creating ClasspathRoot "
          + e.getMessage());
      }
    }
    return new ClasspathRootGroup(roots);
  }


  public static ClasspathRoot makeClasspathRoot(File jarOrDir, String classpath)
      throws MalformedURLException {
    ClasspathRoot classpathRoot;
    if (isJar(jarOrDir)) {
      URL jarRoot = jarOrDir.toURI().toURL();
      classpathRoot = new JarClasspathRoot(jarRoot, classpath);
    } else {
      URL dirRoot = jarOrDir.toURI().toURL();
      classpathRoot = new DirectoryClasspathRoot(dirRoot, classpath);
    }
    return classpathRoot;
  }


  private static boolean isJar(File file) {
    return existsWithExtension(file, ".jar");
  }

  private static boolean existsWithExtension(File file, String extension) {
    return file.isFile() &&
        file.getName().toLowerCase().endsWith(extension);
  }
}
