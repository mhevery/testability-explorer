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

class GlobalScopeBuilder extends DefaultBuilder {

  private final Node parent;

  GlobalScopeBuilder(Node parent) {
    this.parent = parent;
  }

  @Override
  public void beginClassDefinition(String type, String identifier) {
    pushBuilder(new ClassBuilder(parent, identifier));
  }

  @Override
  public void enterNamespaceScope(String ns) {
    pushBuilder(new NamespaceBuilder(parent, ns));
  }

  @Override
  public void beginFunctionDefinition() {
    pushBuilder(new FunctionDefinitionBuilder(parent));
  }

  @Override
  public void beginFunctionDeclaration() {
    pushBuilder(new FunctionDeclarationBuilder(parent));
  }
}
