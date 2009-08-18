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

import com.google.test.metric.cpp.dom.Node;
import com.google.test.metric.cpp.dom.TranslationUnit;

import java.util.List;
import java.util.Stack;

class RootBuilder extends DefaultBuilder implements BuilderContext {

  private final TranslationUnit root = new TranslationUnit();
  private final Stack<DefaultBuilder> builders = new Stack<DefaultBuilder>();
  private final NodeDictionary knownNodes;
  private DefaultBuilder currentBuilder;

  public RootBuilder() {
    pushBuilder(new GlobalScopeBuilder(root));
    knownNodes = new NodeDictionary();
  }

  public RootBuilder(NodeDictionary dict) {
    pushBuilder(new GlobalScopeBuilder(root));
    knownNodes = dict;
  }

  public TranslationUnit getNode() {
    return root;
  }

  public void registerNode(String name, Node node) {
    knownNodes.registerNode(name, node);
  }

  public Node lookupNode(String name) {
    return knownNodes.lookupNode(name);
  }

  @Override
  public void pushBuilder(DefaultBuilder builder) {
    builder.setContext(this);
    builders.push(builder);
    currentBuilder = builder;
  }

  public void popBuilder() {
    builders.pop();
    currentBuilder = builders.peek();
  }

  @Override
  public void beginClassDefinition(String type, String identifier) {
    currentBuilder.beginClassDefinition(type, identifier);
  }

  @Override
  public void endClassDefinition() {
    currentBuilder.endClassDefinition();
  }

  @Override
  public void beginMemberDeclaration() {
    currentBuilder.beginMemberDeclaration();
  }

  @Override
  public void endMemberDeclaration() {
    currentBuilder.endMemberDeclaration();
  }

  @Override
  public void enterNamespaceScope(String ns) {
    currentBuilder.enterNamespaceScope(ns);
  }

  @Override
  public void exitNamespaceScope() {
    currentBuilder.exitNamespaceScope();
  }

  @Override
  public void beginFunctionDefinition(int line) {
    currentBuilder.beginFunctionDefinition(line);
  }

  @Override
  public void functionDirectDeclarator(String identifier) {
    currentBuilder.functionDirectDeclarator(identifier);
  }

  @Override
  public void beginCompoundStatement() {
    currentBuilder.beginCompoundStatement();
  }

  @Override
  public void endCompoundStatement() {
    currentBuilder.endCompoundStatement();
  }

  @Override
  public void endFunctionDefinition() {
    currentBuilder.endFunctionDefinition();
  }

  @Override
  public void beginFunctionDeclaration() {
    currentBuilder.beginFunctionDeclaration();
  }

  @Override
  public void endFunctionDeclaration() {
    currentBuilder.endFunctionDeclaration();
  }

  @Override
  public void directDeclarator(String id) {
    currentBuilder.directDeclarator(id);
  }

  @Override
  public void simpleTypeSpecifier(List<String> sts) {
    currentBuilder.simpleTypeSpecifier(sts);
  }

  @Override
  public void beginForStatement() {
    currentBuilder.beginForStatement();
  }

  @Override
  public void endForStatement() {
    currentBuilder.endForStatement();
  }

  @Override
  public void beginWhileStatement() {
    currentBuilder.beginWhileStatement();
  }

  @Override
  public void endWhileStatement() {
    currentBuilder.endWhileStatement();
  }

  @Override
  public void beginDoStatement() {
    currentBuilder.beginDoStatement();
  }

  @Override
  public void endDoStatement() {
    currentBuilder.endDoStatement();
  }

  @Override
  public void beginIfStatement() {
    currentBuilder.beginIfStatement();
  }

  @Override
  public void endIfStatement() {
    currentBuilder.endIfStatement();
  }

  @Override
  public void beginElseStatement() {
    currentBuilder.beginElseStatement();
  }

  @Override
  public void endElseStatement() {
    currentBuilder.endElseStatement();
  }

  @Override
  public void beginCaseStatement() {
    currentBuilder.beginCaseStatement();
  }

  @Override
  public void beginSwitchStatement() {
    currentBuilder.beginSwitchStatement();
  }

  @Override
  public void endCaseStatement() {
    currentBuilder.endCaseStatement();
  }

  @Override
  public void endSwitchStatement() {
    currentBuilder.endSwitchStatement();
  }

  @Override
  public void beginDefaultStatement() {
    currentBuilder.beginDefaultStatement();
  }

  @Override
  public void endDefaultStatement() {
    currentBuilder.endDefaultStatement();
  }

  @Override
  public void beginInitializer() {
    currentBuilder.beginInitializer();
  }

  @Override
  public void endInitializer() {
    currentBuilder.endInitializer();
  }

  @Override
  public void beginParameterDeclaration() {
    currentBuilder.beginParameterDeclaration();
  }

  @Override
  public void endParameterDeclaration() {
    currentBuilder.endParameterDeclaration();
  }

  @Override
  public void breakStatement() {
    currentBuilder.breakStatement();
  }

  @Override
  public void endReturnStatement() {
    currentBuilder.endReturnStatement();
  }

  @Override
  public void beginReturnStatement(int line) {
    currentBuilder.beginReturnStatement(line);
  }

  @Override
  public void endTernaryOperator() {
    currentBuilder.endTernaryOperator();
  }

  @Override
  public void beginTernaryOperator() {
    currentBuilder.beginTernaryOperator();
  }

  @Override
  public void beginPostfixExpression() {
    currentBuilder.beginPostfixExpression();
  }

  @Override
  public void beginPrimaryExpression() {
    currentBuilder.beginPrimaryExpression();
  }

  @Override
  public void endPostfixExpression() {
    currentBuilder.endPostfixExpression();
  }

  @Override
  public void endPrimaryExpression() {
    currentBuilder.endPrimaryExpression();
  }

  @Override
  public void idExpression(String text) {
    currentBuilder.idExpression(text);
  }

  @Override
  public void beginParameterList() {
    currentBuilder.beginParameterList();
  }

  @Override
  public void endParameterList() {
    currentBuilder.endParameterList();
  }

  @Override
  public void beginAssignmentExpression(int line) {
    currentBuilder.beginAssignmentExpression(line);
  }

  @Override
  public void endAssignmentExpression() {
    currentBuilder.endAssignmentExpression();
  }

  @Override
  public void beginMemberAccess() {
    currentBuilder.beginMemberAccess();
  }

  @Override
  public void endMemberAccess() {
    currentBuilder.endMemberAccess();
  }

  @Override
  public void beginInitDeclaratorList() {
    currentBuilder.beginInitDeclaratorList();
  }

  @Override
  public void endInitDeclaratorList() {
    currentBuilder.endInitDeclaratorList();
  }

  @Override
  public void accessSpecifier(String accessSpec) {
    currentBuilder.accessSpecifier(accessSpec);
  }

  @Override
  public void beginExpression() {
    currentBuilder.beginExpression();
  }

  @Override
  public void endExpression() {
    currentBuilder.endExpression();
  }

  @Override
  public void beginExpressionStatement() {
    currentBuilder.beginExpressionStatement();
  }

  @Override
  public void endExpressionStatement() {
    currentBuilder.endExpressionStatement();
  }

  @Override
  public void beginPtrOperator() {
    currentBuilder.beginPtrOperator();
  }

  @Override
  public void ptrOperator(String ptrSymbol) {
    currentBuilder.ptrOperator(ptrSymbol);
  }

  @Override
  public void endPtrOperator() {
    currentBuilder.endPtrOperator();
  }

  @Override
  public void beginBaseSpecifier() {
    currentBuilder.beginBaseSpecifier();
  }

  @Override
  public void endBaseSpecifier() {
    currentBuilder.endBaseSpecifier();
  }

  public void baseSpecifier(String identifier, boolean isVirtual) {
    currentBuilder.baseSpecifier(identifier, isVirtual);
  }

  @Override
  public void beginTranslationUnit() {
  }

  @Override
  public void endTranslationUnit() {
  }
}
