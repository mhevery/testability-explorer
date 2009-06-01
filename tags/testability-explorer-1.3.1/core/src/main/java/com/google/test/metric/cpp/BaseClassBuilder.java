/*
 * Copyright 2009 Google Inc.
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

import com.google.test.metric.cpp.dom.ClassDeclaration;

public class BaseClassBuilder extends DefaultBuilder {

  private final ClassDeclaration node;

  public BaseClassBuilder(ClassDeclaration node) {
    this.node = node;
  }

  @Override
  public void accessSpecifier(String access) {
    node.setAccessSpecifier(access);
  }

  @Override
  public void baseSpecifier(String identifier, boolean isVirtual) {
    node.setBase((ClassDeclaration) super.context.lookupNode(identifier));
  }

  @Override
  public void endBaseSpecifier() {
    finished();
  }
}
