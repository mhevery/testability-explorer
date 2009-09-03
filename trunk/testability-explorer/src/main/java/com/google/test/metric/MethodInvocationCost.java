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

import java.util.Map;

public class MethodInvocationCost extends ViolationCost {
  private final MethodCost methodCost;

  public Reason getCostSourceType() {
    return costSourceType;
  }

  private Reason costSourceType;

  //TODO(misko): only need to pass the name of the method here??
  public MethodInvocationCost(SourceLocation location, MethodCost methodCost,
                              Reason costSourceType, Cost invocationCost) {
    super(location, invocationCost);
    this.methodCost = methodCost;
    this.costSourceType = costSourceType;
  }

  public String getReason() {
    return costSourceType.toString();
  }

  public boolean isImplicit() {
    return costSourceType.isImplicit();
  }

  @Override
  public void link(Cost directCost, Cost dependentCost, Cost constructorDependentCost) {
    dependentCost.addWithoutLod(cost);
  }

  public MethodCost getMethodCost() {
    return methodCost;
  }

  @Override
  public String getDescription() {
    return methodCost.getDescription();
  }

  @Override
  public Map<String, Object> getAttributes() {
    Map<String, Object> map = super.getAttributes();
    map.put("method", methodCost.getMethodName());
    return map;
  }
}