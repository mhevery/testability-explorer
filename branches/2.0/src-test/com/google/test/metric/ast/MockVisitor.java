/*
 * Copyright 2007 Google Inc.
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

import java.util.HashSet;
import java.util.Set;

public class MockVisitor implements Visitor {

  public final Set<ClassInfo> classes = new HashSet<ClassInfo>();
  public final Set<MethodInfo> methods = new HashSet<MethodInfo>();
  public final Set<ModuleInfo> modules = new HashSet<ModuleInfo>();

  public void visitClass(ClassInfo classInfo) {
    classes.add(classInfo);
  }

  public void visitMethod(MethodInfo methodInfo) {
    methods.add(methodInfo);
  }

  public void visitModule(ModuleInfo moduleInfo) {
    modules.add(moduleInfo);
  }

  public ClassInfo getClassInfo(String name) {
    for (ClassInfo info : classes) {
      if (info.getName().equals(name)) {
        return info;
      }
    }
    return null;
  }
}
