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
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarClasspathRoot extends ClasspathRoot {

  private final Map<String, Set<String>> resourceNamesByPackage =
      new HashMap<String, Set<String>>();

  public JarClasspathRoot(URL url, String classpath) {
    this.url = url;
    List<URL> cp = new ColonDelimitedStringParser(classpath).getURLs();
    classloader = new URLClassLoader(cp.toArray(new URL[cp.size()]), null);
    preloadNamesFromJar();
  }

  @Override
  public Collection<String> getResources(String packageName) {
    if (packageName.endsWith(File.separator)) {
      packageName = packageName.substring(0, packageName.length() - 1);
    }
    Set<String> resources = resourceNamesByPackage.get(packageName);
    return resources == null ? new HashSet<String>() : resources;
  }

  private void preloadNamesFromJar() {
    Enumeration<JarEntry> enumeration = jarFileForUrl().entries();
    while (enumeration.hasMoreElements()) {
      JarEntry entry = enumeration.nextElement();
      String path = entry.getName();
      int index = Math.max(0, path.lastIndexOf(File.separatorChar));
      String location = path.substring(0, index);
      String name = path.substring(index);
      name = name.replace(File.separator, "");
      addName(location, name);
    }
  }

  private JarFile jarFileForUrl() {
    try {
      return new JarFile(url.toURI().getPath());
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void addName(String location, String name) {
    int slash = location.lastIndexOf(File.separator);
    if (slash >= 0) {
      String child = location.substring(slash + 1);
      String parent = location.substring(0, slash);
      addName(parent, child);
    } else if (!location.equals("")) {
      addName("", location);
    }
    Set<String> names = resourceNamesByPackage.get(location);
    if (names == null) {
      names = new HashSet<String>();
      resourceNamesByPackage.put(location, names);
    }
    if (!name.equals("")) {
      names.add(name);
    }
  }

}
