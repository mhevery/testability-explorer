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
package com.google.test.metric.cpp.dom;

public class ClassDeclaration extends Node {
  private final String name;

  public ClassDeclaration(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.beginVisit(this);
    visitChildren(visitor);
    visitor.endVisit(this);
  }

  @Override
  VariableDeclaration findVariableDeclaration(Variable var, Node context) {
    VariableDeclaration result = null;
    NodeList children = getChildren();
    for (int index = 0; index < children.size() && result == null; ++index) {
      VariableDeclarationFinder visitor = new VariableDeclarationFinder(var, context);
      Node child = children.get(index);
      child.accept(visitor);
      result = visitor.getResult();
    }
    return result;
  }
}
