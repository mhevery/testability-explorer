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
  private final NodeList children = new NodeList();
  private final NodeList expressions = new NodeList();
  private Node parent;

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
    private final String name;
    private final Node finish;
    private boolean stop;
    private LocalVariableDeclaration result;

    public VariableDeclarationFinder(String name, Node finish) {
      this.name = name;
      this.finish = finish;
    }

    public LocalVariableDeclaration getResult() {
      return result;
    }

    @Override
    public void visit(LocalVariableDeclaration localVariableDeclaration) {
      if (!stop &&
          localVariableDeclaration.getParent() == finish.getParent() &&
          localVariableDeclaration.getName().equals(name)) {
        result = localVariableDeclaration;
      }
    }
  }

  public LocalVariableDeclaration lookupVariable(String name) {
    LocalVariableDeclaration result = null;
    if (parent != null) {
      int index = parent.children.indexOf(this);
      while (--index >= 0 && result == null) {
        VariableDeclarationFinder visitor = new VariableDeclarationFinder(name, this);
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
