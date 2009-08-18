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
package com.google.test.metric.cpp.dom;

import com.google.test.metric.ParameterInfo;
import com.google.test.metric.Visibility;

import java.util.List;

public class FunctionDefinition extends Node {

  private final String name;
  private final int line;
  private final List<ParameterInfo> parameters;
  private final Visibility visibility;

  public FunctionDefinition(String name, int line,
      List<ParameterInfo> parameters, Visibility visibility) {
    this.name = name;
    this.line = line;
    this.parameters = parameters;
    this.visibility = visibility;
  }

  public String getName() {
    return name;
  }

  public int getLine() {
    return line;
  }

  public List<ParameterInfo> getParameters() {
    return parameters;
  }

  public Visibility getVisibility() {
    return visibility;
  }

  @Override
  public void accept(Visitor visitor) {
    visitor.beginVisit(this);
    visitChildren(visitor);
    visitor.endVisit(this);
  }
}
