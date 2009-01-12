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

import com.google.test.metric.cpp.dom.Namespace;
import com.google.test.metric.cpp.dom.Node;

class NamespaceBuilder extends DefaultBuilder {
  private final Node node;

  public NamespaceBuilder(Node parent, String name) {
    node = new Namespace(name);
    parent.addChild(node);
  }

  @Override
  public void enterNamespaceScope(String ns) {
    pushBuilder(new NamespaceBuilder(node, ns));
  }

  @Override
  public void exitNamespaceScope() {
    finished();
  }

  @Override
  public void beginClassDefinition(String type, String identifier) {
    pushBuilder(new ClassBuilder(node, identifier));
  }

  @Override
  public void beginFunctionDeclaration() {
    pushBuilder(new FunctionDeclarationBuilder(node));
  }

  @Override
  public void beginFunctionDefinition(int line) {
    pushBuilder(new FunctionDefinitionBuilder(node, line));
  }
}
