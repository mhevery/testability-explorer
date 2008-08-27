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
package com.google.test.metric.cpp;

import com.google.test.metric.Type;
import com.google.test.metric.asm.Visibility;
import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.ClassHandle;
import com.google.test.metric.ast.Language;
import com.google.test.metric.ast.ModuleHandle;
import com.google.test.metric.ast.NodeHandle;

import java.util.Collection;
import java.util.List;
import java.util.Stack;

public class ASTBuilder implements Builder {

  private final AbstractSyntaxTree ast;
  private final Stack<NodeHandle> nodeStack = new Stack<NodeHandle>();

  public ASTBuilder(AbstractSyntaxTree ast) {
    this.ast = ast;

    ModuleHandle defaultNamespace =
      ast.createModule(Language.CPP, null, "default");
    nodeStack.add(defaultNamespace);
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
    ast.createClass(Language.CPP, identifier, new ClassHandle[0]);
  }

  public void beginCompoundStatement() {
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
//    throw new UnsupportedOperationException();
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

  public void declarationSpecifiers(List declSpecs) {
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
//    throw new UnsupportedOperationException();
  }

  public void endCompoundStatement() {
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
  }

  public void endIfElseStatement() {
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
//    throw new UnsupportedOperationException();
  }

  public void endWhileStatement() {
    throw new UnsupportedOperationException();
  }

  public void enterNamespaceScope(String ns) {
    NodeHandle parent = nodeStack.peek();
    ModuleHandle moduleHandle = ast.createModule(
        Language.CPP, (ModuleHandle) parent, ns);
    nodeStack.push(moduleHandle);
  }

  public void exitNamespaceScope() {
    nodeStack.pop();
  }

  public void functionDirectDeclarator(String identifier) {
    NodeHandle parent = nodeStack.peek();
    ast.createMethod(Language.CPP, parent, identifier, Visibility.PUBLIC, Type.VOID);
  }

  public Collection getNewElements() {
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

  public void returnStatement() {
    throw new UnsupportedOperationException();
  }

  public void simpleTypeSpecifier(List sts) {
  }

  public void storageClassSpecifier(String storageClassSpec) {
    throw new UnsupportedOperationException();
  }

  public void typeQualifier(String typeQualifier) {
    throw new UnsupportedOperationException();
  }

}
