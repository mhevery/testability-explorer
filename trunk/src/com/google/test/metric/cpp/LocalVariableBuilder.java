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

import com.google.test.metric.cpp.dom.VariableDeclaration;
import com.google.test.metric.cpp.dom.Node;

public class LocalVariableBuilder extends DefaultBuilder {

  private final Node parent;
  private final String type;
  private boolean pointer = false;
  private Node node;

  public LocalVariableBuilder(Node parent, List<String> sts) {
    this.parent = parent;
    this.type = sts.get(0);
  }

  @Override
  public void beginAssignmentExpression() {
    pushBuilder(new ExpressionBuilder(node));
  }

  @Override
  public void beginInitializer() {
    pushBuilder(new ExpressionBuilder(node));
  }

  @Override
  public void beginPrimaryExpression() {
  }

  @Override
  public void idExpression(String text) {
  }

  @Override
  public void directDeclarator(String id) {
    node = new VariableDeclaration(type, id, pointer);
    parent.addChild(node);
    pointer = false;
  }

  @Override
  public void endInitDeclaratorList() {
    finished();
  }

  @Override
  public void beginPtrOperator() {
    pointer = true;
  }

  @Override
  public void ptrOperator(String ptrSymbol) {
  }

  @Override
  public void endPtrOperator() {
  }
}
