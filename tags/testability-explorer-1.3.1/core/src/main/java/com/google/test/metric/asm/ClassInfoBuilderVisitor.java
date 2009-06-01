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

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.JavaClassRepository;

public class ClassInfoBuilderVisitor extends NoopClassVisitor {

  private final JavaClassRepository repository;
  private ClassInfo classInfo;

  public ClassInfoBuilderVisitor(JavaClassRepository repository) {
    this.repository = repository;
  }

  @Override
  public void visit(int version, int access, String name, String signature,
      String superName, String[] interfaces) {
    ClassInfo superClass = null;
    superClass = superName == null ? null : repository.getClass(superName);

    List<ClassInfo> interfaceList = new ArrayList<ClassInfo>();
    for (String interfaze : interfaces) {
      interfaceList.add(repository.getClass(interfaze));
    }
    boolean isInterface = (access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE;
    classInfo = new ClassInfo(name, isInterface, superClass, interfaceList);
    repository.addClass(classInfo);
  }

  @Override
  public MethodVisitor visitMethod(int access, String name, String desc,
      String signature, String[] exceptions) {
    boolean isStatic = (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    boolean isFinal = (access & Opcodes.ACC_FINAL) == Opcodes.ACC_FINAL;
    return new MethodVisitorBuilder(repository, classInfo, name, desc, signature,
        exceptions, isStatic, isFinal, JavaVisibility.valueFromJavaBytecode(access));
  }

  @Override
  public FieldVisitor visitField(int access, String name, String desc,
      String signature, Object value) {
    return new FieldVisitorBuilder(classInfo, access, name, desc,
        signature, value);
  }

  public ClassInfo getClassInfo() {
    return classInfo;
  }

}
