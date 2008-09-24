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

import com.google.test.metric.cpp.dom.FunctionInvocation;
import com.google.test.metric.cpp.dom.Node;
import com.google.test.metric.cpp.dom.NodeList;
import com.google.test.metric.cpp.dom.TernaryOperation;

public class ExpressionBuilder extends DefaultBuilder {

  private String name;
  private NodeList nodes;

  public ExpressionBuilder(NodeList nodes) {
    this.nodes = nodes;
  }

  public ExpressionBuilder(Node parent) {
    this(parent.getChildren());
  }

  @Override
  public void beginPrimaryExpression() {
  }

  @Override
  public void endPrimaryExpression() {
  }

  @Override
  public void idExpression(String id) {
    name = id;
  }

  @Override
  public void beginParameterList() {
    FunctionInvocation functionInvocation = new FunctionInvocation(name);
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
    pushBuilder(new ExpressionBuilder(nodes));
  }

  @Override
  public void endAssignmentExpression() {
    finished();
  }

  @Override
  public void beginTernaryOperator() {
    Node node = new TernaryOperation();
    nodes.add(node);
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
}
