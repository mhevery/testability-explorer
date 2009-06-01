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
import com.google.test.metric.FieldInfo;
import com.google.test.metric.LocalVariableInfo;
import com.google.test.metric.MethodInfo;
import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.cpp.dom.AssignmentExpression;
import com.google.test.metric.cpp.dom.ClassDeclaration;
import com.google.test.metric.cpp.dom.FunctionDefinition;
import com.google.test.metric.cpp.dom.Name;
import com.google.test.metric.cpp.dom.Node;
import com.google.test.metric.cpp.dom.ReturnStatement;
import com.google.test.metric.cpp.dom.TranslationUnit;
import com.google.test.metric.cpp.dom.VariableDeclaration;
import com.google.test.metric.cpp.dom.Visitor;
import com.google.test.metric.method.op.turing.FieldAssignment;
import com.google.test.metric.method.op.turing.LocalAssignment;
import com.google.test.metric.method.op.turing.Operation;
import com.google.test.metric.method.op.turing.ReturnOperation;

public class CppClassRepository implements ClassRepository {

  private final Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();

  private static class LocalVariableExtractor extends Visitor {

    private final List<LocalVariableInfo> variables = new ArrayList<LocalVariableInfo>();

    List<LocalVariableInfo> getResult() {
      return variables;
    }

    @Override
    public void visit(VariableDeclaration localVariableDeclaration) {
      Type variableType = CppType.fromName(localVariableDeclaration.getName(),
          localVariableDeclaration.isPointer());
      variables.add(new LocalVariableInfo(localVariableDeclaration.getName(),
          variableType));
    }
  }

  private static class OperationBuilder extends Visitor {

    private final List<Operation> operations = new ArrayList<Operation>();
    private final CppClassRepository repository;

    public OperationBuilder(CppClassRepository repository) {
      this.repository = repository;
    }

    List<Operation> getResult() {
      return operations;
    }

    @Override
    public void visit(AssignmentExpression assignmentExpression) {
      Node leftSide = assignmentExpression.getExpression(0);
      Node rightSide = assignmentExpression.getExpression(1);
      Variable leftVar = null;
      Variable rightVar = null;
      VariableDeclaration leftDeclaration = null;
      if (leftSide instanceof Name) {
        Name leftName = (Name) leftSide;
        leftDeclaration = leftName.lookupVariable(leftName.getIdentifier());
        leftVar = new Variable(leftDeclaration.getName(),
            CppType.fromName(leftDeclaration.getType()), false, false);
      }
      if (rightSide instanceof Name) {
        Name rightName = (Name) rightSide;
        VariableDeclaration declaration = rightName.lookupVariable(
            rightName.getIdentifier());
        rightVar = new Variable(declaration.getName(),
            CppType.fromName(declaration.getType()), false, false);
      }
      if (leftVar != null && rightVar != null) {
        Node leftParent = leftDeclaration.getParent();
        if (leftParent instanceof ClassDeclaration) {
          ClassInfo classInfo = repository.getClass(leftDeclaration.getName());
          Type fieldType = CppType.fromName(leftDeclaration.getType());
          FieldInfo fieldInfo = new FieldInfo(classInfo, leftDeclaration
              .getName(), fieldType, false, false, false);
          operations.add(new FieldAssignment(assignmentExpression
              .getLineNumber(), leftVar, fieldInfo, rightVar));
        } else {
          operations.add(new LocalAssignment(assignmentExpression
              .getLineNumber(), leftVar, rightVar));
        }
      }
    }

    @Override
    public void beginVisit(ReturnStatement returnStatement) {
      operations.add(new ReturnOperation(returnStatement.getLineNumber(), null));
    }
  }

  private class ClassInfoBuilder extends Visitor {

    private final Stack<ClassInfo> stack = new Stack<ClassInfo>();
    private final CppClassRepository repository;

    public ClassInfoBuilder(CppClassRepository repository) {
      this.repository = repository;
    }

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
      OperationBuilder operationBuilder = new OperationBuilder(repository);
      functionDefinition.accept(operationBuilder);

      ClassInfo classInfo = stack.peek();
      classInfo.addMethod(new MethodInfo(
          classInfo,
          functionDefinition.getName(),
          functionDefinition.getLine(),
          null,
          null,
          functionDefinition.getParameters(),
          localVariablesExtractor.getResult(),
          functionDefinition.getVisibility(),
          new ArrayList<Integer>(),
          operationBuilder.getResult(),
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
    ClassInfoBuilder builder = new ClassInfoBuilder(this);
    unit.accept(builder);
  }

  public void parse(String in) throws Exception {
    TranslationUnit unit = new Parser().parse(in);
    ClassInfoBuilder builder = new ClassInfoBuilder(this);
    unit.accept(builder);
  }
}
