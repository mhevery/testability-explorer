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

import com.google.test.metric.Type;
import com.google.test.metric.asm.Visibility;
import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.Language;
import com.google.test.metric.ast.MethodHandle;
import com.google.test.metric.ast.NodeHandle;

import java.util.List;

public class ASTFunctionBuilder extends ASTBuilderBase {

  private final AbstractSyntaxTree ast;
  private final NodeHandle parent;
  private MethodHandle methodHandle;

  public ASTFunctionBuilder(AbstractSyntaxTree ast, NodeHandle parent) {
    this.ast = ast;
    this.parent = parent;
  }

  @Override
  public void simpleTypeSpecifier(List sts) {
  }

  @Override
  public void functionDirectDeclarator(String identifier) {
    methodHandle = ast.createMethod(Language.CPP, parent, identifier,
        Visibility.PUBLIC, Type.VOID);
  }

  @Override
  public void endFunctionDefinition() {
    finished();
  }

  @Override
  public void beginCompoundStatement() {
  }

  @Override
  public void endCompoundStatement() {
  }

  @Override
  public void beginParameterDeclaration() {
  }

  @Override
  public void directDeclarator(String id) {
    ast.createMethodParameter(methodHandle, id, Type.INT);
  }

  @Override
  public void endParameterDeclaration() {
  }
}
