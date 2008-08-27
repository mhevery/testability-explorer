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
// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric.ast;

import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.asm.Visibility;

final class JavaMethod extends Method implements
    JavaMethodHandle, JavaMethodInfo {

  boolean isAbstract;
  boolean isFinal;
  final JavaClazz specOwner;


  JavaMethod(JavaClazz owner, String name, Type returnType, Visibility access) {
    super(owner, name, returnType, access);
    specOwner = owner;
  }

  private void setIsFinal(boolean newFinal) {
    isFinal = newFinal;
  }

  public boolean getIsFinal() {
    return isFinal;
  }

  private void setIsAbstract(boolean newIsAbstract) {
    isAbstract = newIsAbstract;
  }

  public boolean getIsAbstract() {
    return isAbstract;
  }

  @Override
  public boolean isStaticConstructor() {
    return name.equals("<clinit>");
  }

  @Override
  public boolean isStatic() {
    return localVars.containsKey("this");
  }

  @Override
  public JavaClassInfo getClassInfo() {
    return specOwner;
  }

  @Override
  public Variable getMethodThis() {
    return localVars.get("this");
  }
}