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
import com.google.test.metric.cpp.dom.VariableDeclaration;

import java.util.List;

class VariableDeclarationBuilder extends DefaultBuilder {

  private final Node parent;
  private final List<String> sts;

  public VariableDeclarationBuilder(Node parent, List<String> sts) {
    this.parent = parent;
    this.sts = sts;
  }

  @Override
  public void directDeclarator(String id) {
    parent.addChild(new VariableDeclaration(sts.get(0), id));
  }

  @Override
  public void beginInitializer() {
  }

  @Override
  public void endInitializer() {
  }

  @Override
  public void beginPostfixExpression() {
  }

  @Override
  public void endPostfixExpression() {
  }

  @Override
  public void endInitDeclaratorList() {
    finished();
  }
}
