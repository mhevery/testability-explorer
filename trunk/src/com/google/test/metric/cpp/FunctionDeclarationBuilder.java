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

import com.google.test.metric.cpp.dom.FunctionDeclaration;
import com.google.test.metric.cpp.dom.Node;

/*
 * Ignoring function declarations for now because they don't contribute to
 * testability score.
 */
class FunctionDeclarationBuilder extends DefaultBuilder {

  private final Node parent;

  public FunctionDeclarationBuilder(Node parent) {
    this.parent = parent;
  }

  @Override
  public void directDeclarator(String id) {
    parent.addChild(new FunctionDeclaration(id));
  }

  @Override
  public void simpleTypeSpecifier(List<String> sts) {
  }

  @Override
  public void endFunctionDeclaration() {
    finished();
  }
}
