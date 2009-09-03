/*
 * Copyright 2009 Google Inc.
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
 package com.google.test.metric.javasrc;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import antlr.collections.AST;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.JavaType;
import com.google.test.metric.Type;
import com.google.test.metric.Visibility;

public class CompilationUnitBuilder {

  private final JavaSrcRepository repository;

  private final Stack<TypeBuilder> typeStack = new Stack<TypeBuilder>();
  private final Qualifier qualifier;
  private final String src;

  public TypeBuilder type;

  public CompilationUnitBuilder(JavaSrcRepository repository,
                                Qualifier qualifier, String src) {
    this.repository = repository;
    this.qualifier = qualifier;
    this.src = src;
  }

  public void startType(int line, String name, Type superType,
      List<Type> interfaceTypes) {
    String className = (type == null ? qualifier.getPackage() : type.getName() + "$") + name;
    boolean isInterface = false;
    List<ClassInfo> interfaceClasses = new ArrayList<ClassInfo>();
    for (Type interfaceType : interfaceTypes) {
      interfaceClasses.add(toClassInfo(interfaceType));
    }
    ClassInfo info = new ClassInfo(className,
        isInterface, toClassInfo(superType), interfaceClasses, src);
    repository.addClass(info);
    pushType(new TypeBuilder(info));
  }

  private String getContext() {
    return this.type == null ? "" : this.type.getName();
  }

  private void pushType(TypeBuilder typeBuilder) {
    typeStack.push(type);
    type = typeBuilder;
  }

  public void endType() {
    type = typeStack.pop();
  }

  public Type toType(AST ast) {
    return JavaType.fromJava(qualifier.qualify(getContext(), ast));
  }

  public ClassInfo toClassInfo(Type type) {
    if (type == null) {
      return null;
    }
    return repository.getClass(type.toString());
  }

  public Visibility visibility(AST modifiers) {
    if (contains(modifiers, "private"))
      return Visibility.PRIVATE;
    if (contains(modifiers, "protected"))
      return Visibility.PROTECTED;
    if (contains(modifiers, "public"))
      return Visibility.PUBLIC;
    return Visibility.PACKAGE;
  }

  public boolean isStatic(AST modifiers) {
    return contains(modifiers, "static");
  }

  public boolean isFinal(AST modifiers) {
    return contains(modifiers, "final");
  }

  private boolean contains(AST modifiers, String text) {
    AST modifier = modifiers.getFirstChild();
    while (modifier != null) {
      if (modifier.getText().equals(text))
        return true;
      modifier = modifier.getNextSibling();
    }
    return false;
  }

}
