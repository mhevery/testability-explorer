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

import com.google.classpath.ClasspathRootGroup;
import com.google.test.metric.asm.ClassInfoBuilderVisitor;
import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.ClassHandle;

import org.objectweb.asm.ClassReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Internal comment: Most code from {@link ClassRepository}.
 */
public class JavaParser {

  private final ClasspathRootGroup classpathRoots;
  private final AbstractSyntaxTree ast;

  public JavaParser(AbstractSyntaxTree ast) {
    this(null, ast);
  }

  public JavaParser(ClasspathRootGroup classpathRoots, AbstractSyntaxTree ast) {
    this.classpathRoots = classpathRoots;
    this.ast = ast;
  }

  public ClassHandle getClass(Class<?> clazz) {
    return getClass(clazz.getName());
  }

  public ClassHandle getClass(String clazzName) {
    if (clazzName.startsWith("[")) {
      return getClass(Object.class);
    }

    ClassHandle classHandle = ast.getClass(clazzName.replace('/', '.'));
    if (classHandle == null) {
      try {
        classHandle = parseClass(inputStreamForClass(clazzName));
      } catch (ArrayIndexOutOfBoundsException e) {
        throw new ClassNotFoundException(clazzName);
      }
    }
    return classHandle;
  }

  private InputStream inputStreamForClass(String clazzName) {
    String classResource = clazzName.replace(".", "/") + ".class";
    InputStream classBytes;
    if (classpathRoots != null) {
      classBytes = classpathRoots.getResourceAsStream(classResource);
    } else {
      classBytes = ClassLoader.getSystemResourceAsStream(classResource);
    }
    if (classBytes == null) {
      throw new ClassNotFoundException(clazzName);
    }
    return classBytes;
  }

  private ClassHandle parseClass(InputStream classBytes) {
    try {
      ClassReader classReader = new ClassReader(classBytes);
      ClassInfoBuilderVisitor visitor =
          new ClassInfoBuilderVisitor(this, ast);
      classReader.accept(visitor, 0);
      return visitor.getClassHandle();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
