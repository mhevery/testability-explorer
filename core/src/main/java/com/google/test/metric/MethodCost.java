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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MethodCost {

  private final String methodName;
  private final int lineNumber;
  private final boolean constructor;
  private final boolean aStatic;

  private final List<ViolationCost> costSources = new ArrayList<ViolationCost>();

  public boolean isConstructor() {
    return constructor;
  }

  public boolean isStatic() {
    return aStatic;
  }

  public Cost getDirectCost() {
    return directCost;
  }

  public Cost getDependantCost() {
    return dependantCost;
  }

  private final Cost directCost = new Cost();
  private final Cost dependantCost = new Cost();
  public static final String METHOD_NAME_ATTRIBUTE = "name";

  /**
   * @param methodName
   *          name of the method, such as {@code void myMethod()}.
   * @param lineNumber
   *          line number
   */
  public MethodCost(String methodName, int lineNumber, boolean isConstructor, boolean isStatic) {
    this.methodName = methodName;
    this.lineNumber = lineNumber;
    constructor = isConstructor;
    aStatic = isStatic;
  }

  public Cost getTotalCost() {
    return new Cost().add(directCost).add(dependantCost);
  }

  public String getMethodName() {
    return methodName;
  }

  public void addCostSource(ViolationCost costSource) {
    costSource.link(directCost, dependantCost);
    costSources.add(costSource);
  }

  @Override
  public String toString() {
    return getMethodName() + toCostsString();
  }

  public String toCostsString() {
    return " [" + getTotalCost() + " / " + directCost + "]";
  }

  public int getMethodLineNumber() {
    return lineNumber;
  }

  public List<? extends ViolationCost> getViolationCosts() {
    return costSources;
  }

  public List<? extends ViolationCost> getImplicitViolationCosts() {
    return filterViolationCosts(true);
  }
  public List<? extends ViolationCost> getExplicitViolationCosts() {
    return filterViolationCosts(false);
  }
  private List<? extends ViolationCost> filterViolationCosts(boolean implicit) {
    List<ViolationCost> result = new ArrayList<ViolationCost>();
    for (ViolationCost cost : getViolationCosts()) {
      if (cost.isImplicit() == implicit) {
        result.add(cost);
      }
    }
    return result;
  }

  public Cost getCost() {
    return directCost;
  }

  public Map<String, Object> getAttributes() {
    Map<String, Object> map = getTotalCost().getAttributes();
    map.put("line", lineNumber);
    map.put(METHOD_NAME_ATTRIBUTE, methodName);
    return map;
  }

  public void link() {
  }

}
