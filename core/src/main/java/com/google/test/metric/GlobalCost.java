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

package com.google.test.metric;

import static java.lang.String.format;

import com.google.test.metric.report.RemovePackageFormatter;

public class GlobalCost extends ViolationCost {

  private final Variable variable;

  public GlobalCost(SourceLocation sourceLocation, Variable variable, Cost globalCost) {
    super(sourceLocation, globalCost);
    this.variable = variable;
  }

  public String getReason() {
    return "dependency on global mutable state";
  }

  @Override
  public void link(Cost directCost, Cost dependentCost, Cost constructorDependentCost) {
    directCost.add(getCost());
  }

  @Override
  public String getDescription() {
    return format("%s %s",
        new RemovePackageFormatter().format(variable.getType().toString()), variable.getName());
  }

}