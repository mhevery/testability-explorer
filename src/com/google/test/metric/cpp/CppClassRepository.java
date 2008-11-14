/*
 * Copyright 2008 Google Inc.
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
package com.google.test.metric.cpp;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.LocalVariableInfo;
import com.google.test.metric.MethodInfo;
import com.google.test.metric.cpp.dom.ClassDeclaration;
import com.google.test.metric.cpp.dom.FunctionDefinition;
import com.google.test.metric.cpp.dom.LocalVariableDeclaration;
import com.google.test.metric.cpp.dom.TranslationUnit;
import com.google.test.metric.cpp.dom.Visitor;
import com.google.test.metric.method.op.turing.Operation;

public class CppClassRepository implements ClassRepository {

  private final Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();

  private static class LocalVariableExtractor extends Visitor {

    private final List<LocalVariableInfo> variables = new ArrayList<LocalVariableInfo>();

    List<LocalVariableInfo> getResult() {
      return variables;
    }

    @Override
    public void visit(LocalVariableDeclaration localVariableDeclaration) {
      variables.add(new LocalVariableInfo(localVariableDeclaration.getName(),
          CppType.fromName(localVariableDeclaration.getName())));
    }
  }

  private class ClassInfoBuilder extends Visitor {

    private final Stack<ClassInfo> stack = new Stack<ClassInfo>();

    @Override
    public void beginVisit(ClassDeclaration classDeclaration) {
      ClassInfo classInfo = new ClassInfo(classDeclaration.getName(), false,
          null, new ArrayList<ClassInfo>());
      classes.put(classDeclaration.getName(), classInfo);
      stack.push(classInfo);
    }

    @Override
    public void endVisit(ClassDeclaration classDeclaration) {
      stack.pop();
    }

    @Override
    public void beginVisit(FunctionDefinition functionDefinition) {
      LocalVariableExtractor localVariablesExtractor = new LocalVariableExtractor();
      functionDefinition.accept(localVariablesExtractor);

      ClassInfo classInfo = stack.peek();
      classInfo.addMethod(new MethodInfo(
          classInfo,
          functionDefinition.getName(),
          functionDefinition.getLine(),
          null,
          null,
          functionDefinition.getParameters(),
          localVariablesExtractor.getResult(),
          null,
          new ArrayList<Integer>(),
          new ArrayList<Operation>(),
          false));
    }
  }

  public ClassInfo getClass(String clazzName) {
    return classes.get(clazzName);
  }

  public void addClass(ClassInfo classInfo) {
    classes.put(classInfo.getName(), classInfo);
  }

  void parse(InputStream in) throws Exception {
    TranslationUnit unit = new Parser().parse(in);
    ClassInfoBuilder builder = new ClassInfoBuilder();
    unit.accept(builder);
  }
}
