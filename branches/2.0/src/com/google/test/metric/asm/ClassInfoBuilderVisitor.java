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
package com.google.test.metric.asm;


import com.google.test.metric.JavaParser;
import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.ClassHandle;
import com.google.test.metric.ast.Language;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.List;

public class ClassInfoBuilderVisitor extends NoopClassVisitor {

  private final AbstractSyntaxTree ast;
  private ClassHandle classHandle;
  private final JavaParser parser;

  public ClassInfoBuilderVisitor(JavaParser parser, AbstractSyntaxTree ast) {
    this.parser = parser;
    this.ast = ast;
  }

  @Override
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    ClassHandle superClass = null;
    superClass = superName == null ? null : parser.getClass(superName);

    List<ClassHandle> interfaceList = new ArrayList<ClassHandle>();
    for (String interfaze : interfaces) {
      interfaceList.add(ast.getClass(interfaze));
    }
    boolean isInterface = (access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE;
    interfaceList.add(superClass);
    classHandle = ast.createClass(Language.JAVA, name.replace('/', '.'),
        interfaceList.toArray(new ClassHandle[0]));
    ast.getJavaClassHandle(classHandle).setIsInterface(isInterface);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {
    boolean isStatic = (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    return new MethodVisitorBuilder(parser, ast, classHandle, name, desc, signature,
        exceptions, isStatic, Visibility.valueOf(access));
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc,
      String signature, Object value) {
    return new FieldVisitorBuilder(parser, ast, classHandle, access, name, desc,
        signature, value);
  }

  public ClassHandle getClassHandle() {
    return classHandle;
  }
}
