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

import com.google.test.metric.Visibility;
import com.google.test.metric.cpp.dom.ClassDeclaration;
import com.google.test.metric.cpp.dom.Node;
import com.google.test.metric.cpp.dom.VariableDeclaration;

import java.util.List;

class ClassBuilder extends DefaultBuilder {
  private final ClassDeclaration node;
  private Visibility currentVisibility;
  private List<String> sts;

  public ClassBuilder(Node parent, String identifier) {
    node = new ClassDeclaration(identifier);
    parent.addChild(node);
    currentVisibility = Visibility.PRIVATE;
  }

  @Override
  void setContext(BuilderContext context) {
    super.setContext(context);
    context.registerNode(node.getQualifiedName(), node);
  }

  @Override
  public void beginBaseSpecifier() {
    pushBuilder(new BaseClassBuilder(node));
  }

  @Override
  public void beginClassDefinition(String type, String identifier) {
    pushBuilder(new ClassBuilder(node, identifier));
  }

  @Override
  public void endClassDefinition() {
    finished();
  }

  @Override
  public void beginFunctionDefinition(int line) {
    pushBuilder(new FunctionDefinitionBuilder(node, line, currentVisibility));
  }

  @Override
  public void beginFunctionDeclaration() {
    pushBuilder(new FunctionDeclarationBuilder(node, currentVisibility));
  }

  @Override
  public void accessSpecifier(String accessSpec) {
    currentVisibility = visibilityFromCppString(accessSpec);
  }

  @Override
  public void beginMemberDeclaration() {
  }

  @Override
  public void endMemberDeclaration() {
  }

  private Visibility visibilityFromCppString(String access) {
    if (access.equals("public")) {
      return Visibility.PUBLIC;
    } else if (access.equals("protected")) {
      return Visibility.PROTECTED;
    } else if (access.equals("private")) {
      return Visibility.PRIVATE;
    }
    throw new IllegalArgumentException("invalid access specifier");
  }

  @Override
  public void simpleTypeSpecifier(List<String> sts) {
    this.sts = sts;
  }

  @Override
  public void directDeclarator(String id) {
    Node child = new VariableDeclaration(sts.get(0), id);
    node.addChild(child);
  }
}
