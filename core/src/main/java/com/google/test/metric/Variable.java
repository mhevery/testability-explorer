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
package com.google.test.metric;

import com.google.test.metric.report.issues.SourceElement;


public class Variable implements SourceElement {

  protected final Type type;
  private final boolean isFinal;
  private final boolean isGlobal;
  private String name;
  private int hashCode;

  public Variable(String name, Type type, boolean isFinal, boolean isGlobal) {
    setName(name);
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
    this.hashCode  = computeHashCode();
  }

  public int computeHashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  public boolean isGlobal() {
    return isGlobal;
  }

  public boolean isFinal() {
    return isFinal;
  }

  public String shortFormat() {
    String type = getType().toString();
    String typeSimpleName = type.substring(type.lastIndexOf(".") + 1);
    return String.format("%s %s", typeSimpleName, getName());
  }

}
