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

import java.util.List;

class DefaultBuilder implements Builder {

  private BuilderContext context;

  void setContext(BuilderContext context) {
    this.context = context;
  }

  protected void pushBuilder(DefaultBuilder builder) {
    context.pushBuilder(builder);
  }

  protected void finished() {
    context.popBuilder();
  }

  public void accessSpecifier(String accessSpec) {
    throw new UnsupportedOperationException();
  }

  public void baseSpecifier(String identifier, boolean isVirtual) {
    throw new UnsupportedOperationException();
  }

  public void beginBaseSpecifier() {
    throw new UnsupportedOperationException();
  }

  public void beginClassDefinition(String type, String identifier) {
    throw new UnsupportedOperationException();
  }

  public void beginCompoundStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginCtorDefinition() {
    throw new UnsupportedOperationException();
  }

  public void beginDoStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginDtorHead() {
    throw new UnsupportedOperationException();
  }

  public void beginElseStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginForStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginFunctionDeclaration() {
    throw new UnsupportedOperationException();
  }

  public void beginFunctionDefinition() {
    throw new UnsupportedOperationException();
  }

  public void beginIfStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginInitializer() {
    throw new UnsupportedOperationException();
  }

  public void beginMemberDeclaration() {
    throw new UnsupportedOperationException();
  }

  public void beginParameterDeclaration() {
    throw new UnsupportedOperationException();
  }

  public void beginPtrOperator() {
    throw new UnsupportedOperationException();
  }

  public void beginTranslationUnit() {
    throw new UnsupportedOperationException();
  }

  public void beginWhileStatement() {
    throw new UnsupportedOperationException();
  }

  public void breakStatement() {
    throw new UnsupportedOperationException();
  }

  public void continueStatement() {
    throw new UnsupportedOperationException();
  }

  public void declarationSpecifiers(List<String> declSpecs) {
    throw new UnsupportedOperationException();
  }

  public void directDeclarator(String id) {
    throw new UnsupportedOperationException();
  }

  public void dtorDeclarator(String identifier) {
    throw new UnsupportedOperationException();
  }

  public void endBaseSpecifier() {
    throw new UnsupportedOperationException();
  }

  public void endClassDefinition() {
    throw new UnsupportedOperationException();
  }

  public void endCompoundStatement() {
    throw new UnsupportedOperationException();
  }

  public void endCtorDefinition() {
    throw new UnsupportedOperationException();
  }

  public void endDoStatement() {
    throw new UnsupportedOperationException();
  }

  public void endDtorHead() {
    throw new UnsupportedOperationException();
  }

  public void endForStatement() {
    throw new UnsupportedOperationException();
  }

  public void endFunctionDeclaration() {
    throw new UnsupportedOperationException();
  }

  public void endFunctionDefinition() {
    throw new UnsupportedOperationException();
  }

  public void endIfStatement() {
    throw new UnsupportedOperationException();
  }

  public void endElseStatement() {
    throw new UnsupportedOperationException();
  }

  public void endInitializer() {
    throw new UnsupportedOperationException();
  }

  public void endMemberDeclaration() {
    throw new UnsupportedOperationException();
  }

  public void endParameterDeclaration() {
    throw new UnsupportedOperationException();
  }

  public void endPtrOperator() {
    throw new UnsupportedOperationException();
  }

  public void endTranslationUnit() {
    throw new UnsupportedOperationException();
  }

  public void endWhileStatement() {
    throw new UnsupportedOperationException();
  }

  public void enterNamespaceScope(String ns) {
    throw new UnsupportedOperationException();
  }

  public void exitNamespaceScope() {
    throw new UnsupportedOperationException();
  }

  public void functionDirectDeclarator(String identifier) {
    throw new UnsupportedOperationException();
  }

  public void gotoStatement() {
    throw new UnsupportedOperationException();
  }

  public void makeNamespaceAlias(String ns, String alias) {
    throw new UnsupportedOperationException();
  }

  public void ptrOperator(String ptrSymbol) {
    throw new UnsupportedOperationException();
  }

  public void ptrToMember(String scopedItem, String star) {
    throw new UnsupportedOperationException();
  }

  public void qualifiedCtorId(String identifier) {
    throw new UnsupportedOperationException();
  }

  public void simpleTypeSpecifier(List<String> sts) {
    throw new UnsupportedOperationException();
  }

  public void storageClassSpecifier(String storageClassSpec) {
    throw new UnsupportedOperationException();
  }

  public void typeQualifier(String typeQualifier) {
    throw new UnsupportedOperationException();
  }

  public void beginCaseStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginSwitchStatement() {
    throw new UnsupportedOperationException();
  }

  public void endCaseStatement() {
    throw new UnsupportedOperationException();
  }

  public void endSwitchStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginDefaultStatement() {
    throw new UnsupportedOperationException();
  }

  public void endDefaultStatement() {
    throw new UnsupportedOperationException();
  }

  public void endTernaryOperator() {
    throw new UnsupportedOperationException();
  }

  public void beginTernaryOperator() {
    throw new UnsupportedOperationException();
  }

  public void endReturnStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginReturnStatement() {
    throw new UnsupportedOperationException();
  }

  public void beginPostfixExpression() {
    throw new UnsupportedOperationException();
  }

  public void beginPrimaryExpression() {
    throw new UnsupportedOperationException();
  }

  public void endPostfixExpression() {
    throw new UnsupportedOperationException();
  }

  public void endPrimaryExpression() {
    throw new UnsupportedOperationException();
  }

  public void idExpression(String text) {
    throw new UnsupportedOperationException();
  }

  public void beginParameterList() {
    throw new UnsupportedOperationException();
  }

  public void endParameterList() {
    throw new UnsupportedOperationException();
  }

  public void endAssignmentExpression() {
    throw new UnsupportedOperationException();
  }

  public void beginAssignmentExpression() {
    throw new UnsupportedOperationException();
  }

  public void beginMemberAccess() {
    throw new UnsupportedOperationException();
  }

  public void endMemberAccess() {
    throw new UnsupportedOperationException();
  }
}
