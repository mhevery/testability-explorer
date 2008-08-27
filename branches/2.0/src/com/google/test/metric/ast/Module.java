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
package com.google.test.metric.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Internal representation of a "Module". Will never ever be passed to
 * somebody outside this class- only either as a ModuleHandle for creating
 * children, or as a ModuleInfo for reading the necessary data.
 */
class Module extends AbstractSyntaxTree.Node
    implements ModuleHandle, ModuleInfo {

  String name;
  List<MethodInfo> methods = new ArrayList<MethodInfo>();


  Module(String newName) {
    this.name = newName;
  }

  public ModuleHandle getHandle() {
    return this;
  }

  public String getName() {
    return name;
  }

  public List<MethodInfo> getMethods() {
    return Collections.unmodifiableList(methods);
  }

  public void registerMethod(Method method) {
    methods.add(method);
  }

  public void accept(Visitor v) {
    for (MethodInfo m : methods) {
      v.visitMethod(m);
    }
  }
}
