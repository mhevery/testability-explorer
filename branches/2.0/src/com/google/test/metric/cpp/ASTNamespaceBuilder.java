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

import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.Language;
import com.google.test.metric.ast.ModuleHandle;

public class ASTNamespaceBuilder extends ASTBuilderBase {

  private final AbstractSyntaxTree ast;
  private final ModuleHandle namespace;
  private ASTBuilderLink builderLink;

  public ASTNamespaceBuilder(AbstractSyntaxTree ast) {
    this(ast, null, "default");
  }

  public ASTNamespaceBuilder(AbstractSyntaxTree ast, ModuleHandle parent, String name) {
    this.ast = ast;
    this.namespace = ast.createModule(Language.CPP, parent, name);
  }

  @Override
  public void beginTranslationUnit() {
  }

  @Override
  public void endTranslationUnit() {
  }

  @Override
  public void beginClassDefinition(String type, String identifier) {
    pushBuilder(new ASTClassBuilder(ast, type, identifier));
  }

  @Override
  public void beginFunctionDefinition() {
    pushBuilder(new ASTFunctionBuilder(ast, namespace));
  }

  @Override
  public void enterNamespaceScope(String ns) {
    pushBuilder(new ASTNamespaceBuilder(ast, namespace, ns));
  }

  @Override
  public void exitNamespaceScope() {
  }
}
