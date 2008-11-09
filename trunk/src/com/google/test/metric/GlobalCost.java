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


public class GlobalCost extends ViolationCost {

  private final Variable variable;

  public GlobalCost(int lineNumber, Variable variable, Cost globalCost) {
    super(lineNumber, Reason.GLOBAL);
    this.variable = variable;
    this.cost = globalCost;
  }

  @Override
  public void link(Cost directCost, Cost dependantCost) {
    directCost.add(cost);
  }

  @Override
  public String getDescription() {
    return variable.getName() + ":" + variable.getType();
  }

}