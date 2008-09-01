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

import java.util.Collection;
import java.util.List;

public class ASTBuilderLink implements Builder {

  private Builder next;
  private ASTBuilderBase builder;
  private ASTBuilderLink nextLink;
  private final ASTBuilderLink previous;

  protected ASTBuilderLink() {
    this.previous = null;
  }

//  ASTBuilderLink(ASTBuilderLink previous) {
//    this.previous = previous;
//  }

  ASTBuilderLink(ASTBuilderBase builder) {
    this.previous = null;
    this.builder = builder;
    this.next = builder;
  }

  ASTBuilderLink(ASTBuilderLink previous, ASTBuilderBase builder) {
    this.previous = previous;
    this.builder = builder;
    this.next = builder;
  }

//  void setBuilder(ASTBuilderBase builder) {
//    this.builder = builder;
//    if (next == null) {
//      next = builder;
//    }
//  }

  private void setNextLink(ASTBuilderLink link) {
    if (nextLink != null) {
      throw new IllegalStateException("Next link is already set");
    }
    nextLink = link;
    next = link;
  }

  void createNextLink(ASTBuilderBase builder) {
    ASTBuilderLink link = new ASTBuilderLink(this, builder);
    setNextLink(link);
    builder.setLink(link);
  }

  private void dropNextLink() {
    if (nextLink == null) {
      throw new IllegalStateException("Next link is missing");
    }
//    if (builder == null) {
//      throw new IllegalStateException("Builder is not set");
//    }
    nextLink = null;
    next = builder;
  }

  void finished() {
    if (nextLink != null) {
      throw new IllegalStateException("Finished link is in the middle");
    }
    if (previous == null) {
      throw new IllegalStateException("First link is finished");
    }
    previous.dropNextLink();
  }

  public void accessSpecifier(String accessSpec) {
    next.accessSpecifier(accessSpec);
  }

  public void baseSpecifier(String identifier, boolean isVirtual) {
    next.baseSpecifier(identifier, isVirtual);
  }

  public void beginBaseSpecifier() {
    next.beginBaseSpecifier();
  }

  public void beginClassDefinition(String type, String identifier) {
    next.beginClassDefinition(type, identifier);
  }

  public void beginCompoundStatement() {
    next.beginCompoundStatement();
  }

  public void beginCtorDefinition() {
    next.beginCtorDefinition();
  }

  public void beginDoStatement() {
    next.beginDoStatement();
  }

  public void beginDtorHead() {
    next.beginDtorHead();
  }

  public void beginElseStatement() {
    next.beginElseStatement();
  }

  public void beginForStatement() {
    next.beginForStatement();
  }

  public void beginFunctionDeclaration() {
    next.beginFunctionDeclaration();
  }

  public void beginFunctionDefinition() {
    next.beginFunctionDefinition();
  }

  public void beginIfStatement() {
    next.beginIfStatement();
  }

  public void beginInitializer() {
    next.beginInitializer();
  }

  public void beginMemberDeclaration() {
    next.beginMemberDeclaration();
  }

  public void beginParameterDeclaration() {
    next.beginParameterDeclaration();
  }

  public void beginPtrOperator() {
    next.beginPtrOperator();
  }

  public void beginTranslationUnit() {
    next.beginTranslationUnit();
  }

  public void beginWhileStatement() {
    next.beginWhileStatement();
  }

  public void breakStatement() {
    next.breakStatement();
  }

  public void continueStatement() {
    next.continueStatement();
  }

  public void declarationSpecifiers(List declSpecs) {
    next.declarationSpecifiers(declSpecs);
  }

  public void directDeclarator(String id) {
    next.directDeclarator(id);
  }

  public void dtorDeclarator(String identifier) {
    next.dtorDeclarator(identifier);
  }

  public void endBaseSpecifier() {
    next.endBaseSpecifier();
  }

  public void endClassDefinition() {
    next.endClassDefinition();
  }

  public void endCompoundStatement() {
    next.endCompoundStatement();
  }

  public void endCtorDefinition() {
    next.endCtorDefinition();
  }

  public void endDoStatement() {
    next.endDoStatement();
  }

  public void endDtorHead() {
    next.endDtorHead();
  }

  public void endForStatement() {
    next.endForStatement();
  }

  public void endFunctionDeclaration() {
    next.endFunctionDeclaration();
  }

  public void endFunctionDefinition() {
    next.endFunctionDefinition();
  }

  public void endIfElseStatement() {
    next.endIfElseStatement();
  }

  public void endInitializer() {
    next.endInitializer();
  }

  public void endMemberDeclaration() {
    next.endMemberDeclaration();
  }

  public void endParameterDeclaration() {
    next.endParameterDeclaration();
  }

  public void endPtrOperator() {
    next.endPtrOperator();
  }

  public void endTranslationUnit() {
    next.endTranslationUnit();
  }

  public void endWhileStatement() {
    next.endWhileStatement();
  }

  public void enterNamespaceScope(String ns) {
    next.enterNamespaceScope(ns);
  }

  public void exitNamespaceScope() {
    next.exitNamespaceScope();
  }

  public void functionDirectDeclarator(String identifier) {
    next.functionDirectDeclarator(identifier);
  }

  public Collection getNewElements() {
    return next.getNewElements();
  }

  public void gotoStatement() {
    next.gotoStatement();
  }

  public void makeNamespaceAlias(String ns, String alias) {
    next.makeNamespaceAlias(ns, alias);
  }

  public void ptrOperator(String ptrSymbol) {
    next.ptrOperator(ptrSymbol);
  }

  public void ptrToMember(String scopedItem, String star) {
    next.ptrToMember(scopedItem, star);
  }

  public void qualifiedCtorId(String identifier) {
    next.qualifiedCtorId(identifier);
  }

  public void returnStatement() {
    next.returnStatement();
  }

  public void simpleTypeSpecifier(List sts) {
    next.simpleTypeSpecifier(sts);
  }

  public void storageClassSpecifier(String storageClassSpec) {
    next.storageClassSpecifier(storageClassSpec);
  }

  public void typeQualifier(String typeQualifier) {
    next.typeQualifier(typeQualifier);
  }
}
