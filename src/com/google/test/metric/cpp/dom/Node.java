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

import java.util.Stack;

/*
 * Base class for all C++ AST nodes.
 */
public class Node {
  private final NodeList children = new NodeList();
  private final NodeList expressions = new NodeList();
  private Node parent;

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

  public NodeList getChildren() {
    return children;
  }

  @SuppressWarnings("unchecked")
  public <T> T getChild(int index) {
    return (T) children.get(index);
  }

  public void addChild(Node child) {
    children.add(child);
    child.parent = this;
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
    expression.parent = this;
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

  private static class VariableDeclarationFinder extends Visitor {
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

  public VariableDeclaration lookupVariable(String name) {
    Variable var = new Variable(name);
    VariableDeclaration result = null;
    if (parent != null) {
      // very inefficient algorithm, we visit some nodes more than once
      int index = parent.children.indexOf(this);
      while (--index >= 0 && result == null) {
        VariableDeclarationFinder visitor = new VariableDeclarationFinder(var, this);
        Node child = parent.children.get(index);
        child.accept(visitor);
        result = visitor.getResult();
      }
      if (result == null) {
        result = parent.lookupVariable(name);
      }
    }
    return result;
  }
}
