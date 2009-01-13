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

import com.google.test.metric.cpp.dom.AssignmentExpression;
import com.google.test.metric.cpp.dom.Expression;
import com.google.test.metric.cpp.dom.FunctionInvocation;
import com.google.test.metric.cpp.dom.Name;
import com.google.test.metric.cpp.dom.Node;
import com.google.test.metric.cpp.dom.NodeList;
import com.google.test.metric.cpp.dom.TernaryOperation;

public class ExpressionBuilder extends DefaultBuilder {

  private NodeList nodes;

  public ExpressionBuilder(NodeList nodes) {
    this.nodes = nodes;
  }

  public ExpressionBuilder(Node parent) {
    this(parent.getExpressions());
  }

  @Override
  public void beginPrimaryExpression() {
  }

  @Override
  public void endPrimaryExpression() {
  }

  @Override
  public void idExpression(String id) {
    nodes.add(new Name(id));
  }

  @Override
  public void beginParameterList() {
    int index = nodes.size() - 1;
    Name name = nodes.get(index);
    nodes.remove(index);
    FunctionInvocation functionInvocation =
      new FunctionInvocation(name.getIdentifier());
    nodes.add(functionInvocation);
    pushBuilder(new ParameterListBuilder(functionInvocation.getParameters()));
    nodes = functionInvocation.getChildren();
  }

  @Override
  public void beginPostfixExpression() {
    pushBuilder(new ExpressionBuilder(nodes));
  }

  @Override
  public void endPostfixExpression() {
    finished();
  }

  @Override
  public void beginAssignmentExpression() {
    int index = nodes.size() - 1;
    Expression leftSide = nodes.get(index);
    nodes.remove(index);
    AssignmentExpression assignment = new AssignmentExpression();
    nodes.add(assignment);
    assignment.addExpression(leftSide);
    pushBuilder(new AssignmentExpressionBuilder(assignment));
  }

  @Override
  public void endAssignmentExpression() {
    finished();
  }

  @Override
  public void beginTernaryOperator() {
    int index = nodes.size() - 1;
    Expression name = nodes.get(index);
    nodes.remove(index);
    Node node = new TernaryOperation();
    nodes.add(node);
    node.addExpression(name);
    pushBuilder(new ExpressionBuilder(node));
  }

  @Override
  public void endTernaryOperator() {
    finished();
  }

  @Override
  public void beginMemberAccess() {
  }

  @Override
  public void endMemberAccess() {
  }

  @Override
  public void endInitializer() {
    finished();
  }

  @Override
  public void beginExpression() {
    pushBuilder(new ExpressionBuilder(nodes));
  }

  @Override
  public void endExpression() {
    finished();
  }

  @Override
  public void endExpressionStatement() {
    finished();
  }
}
