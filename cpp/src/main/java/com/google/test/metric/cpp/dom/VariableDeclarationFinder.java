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
// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.cpp.dom;

import java.util.Stack;

class VariableDeclarationFinder extends Visitor {
  private final Variable variable;
  private final Stack<Node> parents = new Stack<Node>();
  private VariableDeclaration result;

  public VariableDeclarationFinder(Variable v, Node context) {
    this.variable = v;
    parents.push(context.getParent());
  }

  public VariableDeclaration getResult() {
    return result;
  }

  @Override
  public void visit(VariableDeclaration localVariableDeclaration) {
    Variable var = new Variable(localVariableDeclaration.getName(),
        localVariableDeclaration);
    Node parent = parents.peek();
    if (localVariableDeclaration.getParent() == parent &&
        var.equals(variable)) {
      result = localVariableDeclaration;
    }
  }

  @Override
  public void beginVisit(Namespace namespace) {
    parents.push(namespace);
  }

  @Override
  public void endVisit(Namespace namespace) {
    if (parents.peek() == namespace) {
      parents.pop();
    }
  }
}