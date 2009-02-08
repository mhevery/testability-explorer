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
package com.google.test.metric.cpp.dom;

public abstract class Visitor {

  public void beginVisit(FunctionDefinition functionDefinition) {
  }

  public void endVisit(FunctionDefinition functionDefinition) {
  }

  public void beginVisit(IfStatement ifStatement) {
  }

  public void endVisit(IfStatement ifStatement) {
  }

  public void beginVisit(SwitchStatement switchStatement) {
  }

  public void endVisit(SwitchStatement switchStatement) {
  }

  public void beginVisit(CaseStatement caseStatement) {
  }

  public void endVisit(CaseStatement caseStatement) {
  }

  public void beginVisit(LoopStatement loopStatement) {
  }

  public void endVisit(LoopStatement loopStatement) {
  }

  public void beginVisit(ElseStatement elseStatement) {
  }

  public void endVisit(ElseStatement elseStatement) {
  }

  public void beginVisit(DefaultStatement defaultStatement) {
  }

  public void endVisit(DefaultStatement defaultStatement) {
  }

  public void visit(BreakStatement breakStatement) {
  }

  public void beginVisit(ReturnStatement returnStatement) {
  }

  public void endVisit(ReturnStatement returnStatement) {
  }

  public void beginVisit(TernaryOperation ternaryOperation) {
  }

  public void endVisit(TernaryOperation ternaryOperation) {
  }

  public void beginVisit(ClassDeclaration classDeclaration) {
  }

  public void endVisit(ClassDeclaration classDeclaration) {
  }

  public void beginVisit(TranslationUnit translationUnit) {
  }

  public void endVisit(TranslationUnit translationUnit) {
  }

  public void visit(VariableDeclaration localVariableDeclaration) {
  }

  public void visit(AssignmentExpression assignmentExpression) {
  }

  public void beginVisit(Namespace namespace) {
  }

  public void endVisit(Namespace namespace) {
  }

  public void beginVisit(ExpressionStatement expressionStatement) {
  }

  public void endVisit(ExpressionStatement expressionStatement) {
  }
}
