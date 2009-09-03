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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;

import com.google.classpath.ClassPath;
import com.google.test.metric.asm.ClassInfoBuilderVisitor;

public class JavaClassRepository implements ClassRepository {

  private final Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();
  private ClassPath classpathRoots;

  public JavaClassRepository() {
  }

  public JavaClassRepository(ClassPath classpathRoots) {
    this.classpathRoots = classpathRoots;
  }

  public ClassInfo getClass(String name) {
    if (name.startsWith("[")) {
      return getClass(Object.class.getCanonicalName());
    }
    if (name.contains("$") || name.contains("/")) {
      throw new IllegalStateException("Class name can not contain '$' or '/' in a name: " + name);
    }
    ClassInfo classInfo = classes.get(name);
    if (classInfo == null) {
        try {
          classInfo = parseClass(inputStreamForClass(name));
        } catch (ArrayIndexOutOfBoundsException e) {
          throw new ClassNotFoundException(name);
        } catch (ClassNotFoundException e) {
          throw new ClassNotFoundException(name, e);
        }
    }
    return classInfo;
  }

  private InputStream inputStreamForClass(String clazzName) {
    String resource = clazzName.replace(".", "/");
    InputStream classBytes = null;
    while (true) {
      classBytes = getResource(resource + ".class");
      if (classBytes != null) {
        return classBytes;
      }
      int index = resource.lastIndexOf('/');
      if (index == -1) {
        throw new ClassNotFoundException(clazzName);
      }
      resource = resource.substring(0, index) + "$" + resource.substring(index + 1);
    }
  }

  private InputStream getResource(String classResource) {
    InputStream classBytes = null;
    if (classpathRoots != null) {
      classBytes = classpathRoots.getResourceAsStream(classResource);
    }
    if (classBytes == null) {
      //Perhaps it is a JDK Class
      classBytes = ClassLoader.getSystemResourceAsStream(classResource);
    }
    return classBytes;
  }

  private ClassInfo parseClass(InputStream classBytes) {
    try {
      ClassReader classReader = new ClassReader(classBytes);
      ClassInfoBuilderVisitor visitor = new ClassInfoBuilderVisitor(this);
      classReader.accept(visitor, 0);
      return visitor.getClassInfo();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /* (non-Javadoc)
   * @see com.google.test.metric.ClassRepository#addClass(com.google.test.metric.ClassInfo)
   */
  public void addClass(ClassInfo classInfo) {
    String name = classInfo.getName();
    if (name.contains("$") || name.contains("/")) {
      throw new IllegalStateException();
    }
    classes.put(name, classInfo);
  }

}

