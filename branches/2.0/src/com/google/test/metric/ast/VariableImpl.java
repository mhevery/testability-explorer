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

import com.google.test.metric.Type;

public class VariableImpl implements Variable {
// Stolen the code from com.google.test.metric.Variable
  protected final Type type;
  private final boolean isFinal;
  private final boolean isGlobal;
  private String name;

  public VariableImpl(String name, Type type, boolean isFinal,
      boolean isGlobal) {
    this.name = name;
    this.type = type;
    this.isFinal = isFinal;
    this.isGlobal = isGlobal;
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name + "{" + type + "}";
  }

  public Type getType() {
    return type;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isGlobal() {
    return isGlobal;
  }

  public boolean isFinal() {
    return isFinal;
  }

}
