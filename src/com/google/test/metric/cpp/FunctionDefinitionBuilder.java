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

import com.google.test.metric.cpp.dom.FunctionDefinition;
import com.google.test.metric.cpp.dom.Node;

class FunctionDefinitionBuilder extends DefaultBuilder {

  private final Node parent;
  private Node node;

  public FunctionDefinitionBuilder(Node parent) {
    this.parent = parent;
  }

  @Override
  public void functionDirectDeclarator(String name) {
    node = new FunctionDefinition(name);
    parent.addChild(node);
  }

  @Override
  public void endFunctionDefinition() {
    finished();
  }

  @Override
  public void simpleTypeSpecifier(List<String> sts) {
  }

  @Override
  public void beginCompoundStatement() {
    pushBuilder(new StatementBuilder(node));
  }
}
