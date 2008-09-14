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

import java.util.Stack;

class RootBuilder extends DefaultBuilder implements BuilderContext {

  private final TranslationUnit root = new TranslationUnit();
  private final Stack<DefaultBuilder> builders = new Stack<DefaultBuilder>();
  private DefaultBuilder currentBuilder;

  public TranslationUnit getNode() {
    return root;
  }

  @Override
  public void pushBuilder(DefaultBuilder builder) {
    builder.setContext(this);
    builders.push(builder);
    currentBuilder = builder;
  }

  public void popBuilder() {
    builders.pop();
    if (builders.empty()) {
      currentBuilder = null;
    } else {
      currentBuilder = builders.peek();
    }
  }

  @Override
  public void beginClassDefinition(String type, String identifier) {
    if (currentBuilder == null) {
      pushBuilder(new ClassBuilder(root, identifier));
    } else {
      currentBuilder.beginClassDefinition(type, identifier);
    }
  }

  @Override
  public void endClassDefinition() {
    currentBuilder.endClassDefinition();
  }

  @Override
  public void beginMemberDeclaration() {
    currentBuilder.beginMemberDeclaration();
  }

  @Override
  public void endMemberDeclaration() {
    currentBuilder.endMemberDeclaration();
  }

  @Override
  public void enterNamespaceScope(String ns) {
    if (currentBuilder == null) {
      pushBuilder(new NamespaceBuilder(root, ns));
    } else {
      currentBuilder.enterNamespaceScope(ns);
    }
  }

  @Override
  public void exitNamespaceScope() {
    currentBuilder.exitNamespaceScope();
  }

  @Override
  public void beginTranslationUnit() {
  }

  @Override
  public void endTranslationUnit() {
  }

}
