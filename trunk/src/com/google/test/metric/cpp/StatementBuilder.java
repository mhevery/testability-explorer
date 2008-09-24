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

import com.google.test.metric.cpp.dom.BreakStatement;
import com.google.test.metric.cpp.dom.CaseStatement;
import com.google.test.metric.cpp.dom.DefaultStatement;
import com.google.test.metric.cpp.dom.ElseStatement;
import com.google.test.metric.cpp.dom.IfStatement;
import com.google.test.metric.cpp.dom.LoopStatement;
import com.google.test.metric.cpp.dom.Node;
import com.google.test.metric.cpp.dom.ReturnStatement;
import com.google.test.metric.cpp.dom.SwitchStatement;

class StatementBuilder extends DefaultBuilder {
  private final Node parent;

  public StatementBuilder(Node parent) {
    this.parent = parent;
  }

  @Override
  public void beginCompoundStatement() {
    pushBuilder(new StatementBuilder(parent));
  }

  @Override
  public void endCompoundStatement() {
    finished();
  }

  @Override
  public void beginForStatement() {
    Node node = new LoopStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endForStatement() {
    finished();
  }

  @Override
  public void beginWhileStatement() {
    Node node = new LoopStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endWhileStatement() {
    finished();
  }

  @Override
  public void beginDoStatement() {
    Node node = new LoopStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endDoStatement() {
    finished();
  }

  @Override
  public void beginIfStatement() {
    Node node = new IfStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endIfStatement() {
    finished();
  }

  @Override
  public void beginElseStatement() {
    Node node = new ElseStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endElseStatement() {
    finished();
  }

  @Override
  public void beginCaseStatement() {
    Node node = new CaseStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endCaseStatement() {
    finished();
  }

  @Override
  public void beginSwitchStatement() {
    Node node = new SwitchStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endSwitchStatement() {
    finished();
  }

  @Override
  public void beginDefaultStatement() {
    Node node = new DefaultStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endDefaultStatement() {
    finished();
  }

  @Override
  public void beginReturnStatement() {
    Node node = new ReturnStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
  }

  @Override
  public void endReturnStatement() {
    finished();
  }

  @Override
  public void breakStatement() {
    Node node = new BreakStatement();
    parent.addChild(node);
    pushBuilder(new StatementBuilder(node));
    finished();
  }

  @Override
  public void beginPostfixExpression() {
    pushBuilder(new ExpressionBuilder(parent));
  }

  @Override
  public void simpleTypeSpecifier(List<String> sts) {
  }

  @Override
  public void directDeclarator(String id) {
  }

  @Override
  public void beginAssignmentExpression() {
    pushBuilder(new ExpressionBuilder(parent));
  }
}
