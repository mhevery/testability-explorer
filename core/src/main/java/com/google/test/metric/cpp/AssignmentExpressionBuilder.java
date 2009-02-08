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

import com.google.test.metric.cpp.dom.Name;
import com.google.test.metric.cpp.dom.Node;
import com.google.test.metric.cpp.dom.NodeList;

public class AssignmentExpressionBuilder extends ExpressionBuilder {

  private final NodeList nodes;

  public AssignmentExpressionBuilder(Node parent) {
    super(parent);
    this.nodes = parent.getExpressions();
  }

  public AssignmentExpressionBuilder(NodeList nodes) {
    super(nodes);
    this.nodes = nodes;
  }

  @Override
  public void idExpression(String id) {
    nodes.add(new Name(id));
  }

  @Override
  public void beginAssignmentExpression(int line) {
    pushBuilder(new AssignmentExpressionBuilder(nodes));
  }

  @Override
  public void endAssignmentExpression() {
    finished();
  }

  @Override
  public void beginPostfixExpression() {
  }

  @Override
  public void endPostfixExpression() {
  }
}
