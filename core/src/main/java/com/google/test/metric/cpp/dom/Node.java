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

/*
 * Base class for all C++ AST nodes.
 */
public class Node {
  private final NodeList children;
  private final NodeList expressions;
  private Node parent;

  protected Node() {
    children = new NodeList(this);
    expressions = new NodeList(this);
  }

  public Node getRoot() {
    Node result = this;
    while (result.parent != null) {
      result = result.parent;
    }
    return result;
  }

  public Node getParent() {
    return parent;
  }

  public void setParent(Node parent) {
    this.parent = parent;
  }

  public NodeList getChildren() {
    return children;
  }

  @SuppressWarnings("unchecked")
  public <T> T getChild(int index) {
    return (T) children.get(index);
  }

  public void addChild(Node child) {
    children.add(child);
  }

  public NodeList getExpressions() {
    return expressions;
  }

  @SuppressWarnings("unchecked")
  public <T> T getExpression(int index) {
    return (T) expressions.get(index);
  }

  public void addExpression(Node expression) {
    expressions.add(expression);
  }

  public void accept(Visitor visitor) {
  }

  protected void visitChildren(Visitor visitor) {
    for (Node child : expressions) {
      child.accept(visitor);
    }
    for (Node child : children) {
      child.accept(visitor);
    }
  }

  public VariableDeclaration lookupVariable(String name) {
    Variable var = new Variable(name);
    VariableDeclaration result = null;
    if (parent != null) {
      // very inefficient algorithm, we visit some nodes more than once
      result = parent.findVariableDeclaration(var, this);
      if (result == null) {
        result = parent.lookupVariable(name);
      }
    }
    return result;
  }

  VariableDeclaration findVariableDeclaration(Variable var, Node context) {
    VariableDeclaration result = null;
    int index = children.indexOf(context);
    while (--index >= 0 && result == null) {
      VariableDeclarationFinder visitor = new VariableDeclarationFinder(var, context);
      Node child = children.get(index);
      child.accept(visitor);
      result = visitor.getResult();
    }
    return result;
  }
}
