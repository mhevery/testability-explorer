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

import com.google.test.metric.ViolationCost.Reason;

public class MethodCost {

  private final String methodName;
  private final int lineNumber;
  private final List<ViolationCost> costSources = new ArrayList<ViolationCost>();
  private final Cost directCost = Cost.none();
  private final Cost totalCost = Cost.none();
  private boolean isLinked = false;

  /**
   * @param methodName
   *          name of the method, such as {@code void myMethod()}.
   * @param lineNumber
   *          line number
   * @param cyclomaticCost
   *          complexity cost for this method, alone. Later the costs of the
   *          other methods that this method calls will be added.
   */
  public MethodCost(String methodName, int lineNumber) {
    this.methodName = methodName;
    this.lineNumber = lineNumber;
  }

  private void assertLinked() {
    if (!isLinked) {
      throw new IllegalStateException("Need to link first.");
    }
  }

  private void assertNotLinked() {
    if (isLinked) {
      throw new IllegalStateException("Can not call after linked.");
    }
  }

  public Cost link(CostModel costModel) {
    if (!isLinked) {
      isLinked = true;
      Cost dependantCost = Cost.none();
      for (ViolationCost costSource : costSources) {
        costSource.link(directCost, dependantCost, costModel);
      }
      totalCost.addDependant(dependantCost);
      totalCost.add(directCost);
      totalCost.link(costModel);
      directCost.link(costModel);
    }
    return getTotalCost();
  }

  public Cost getTotalCost() {
    assertLinked();
    return totalCost;
  }

  public String getMethodName() {
    return methodName;
  }

  public void addCostSource(ViolationCost costSource) {
    assertNotLinked();
    costSources.add(costSource);
  }

  public void addMethodCost(int line, MethodCost cost, Reason costSourceType) {
    addCostSource(new MethodInvokationCost(line, cost, costSourceType));
  }

  public void addGlobalCost(int lineNumber, Variable variable) {
    addCostSource(new GlobalCost(lineNumber, variable));
  }

  public void addCyclomaticCost(int lineNumberWithComplexity) {
    addCostSource(new CyclomaticCost(lineNumberWithComplexity));
  }


  @Override
  public String toString() {
    return getMethodName() + toCostsString();
  }

  public String toCostsString() {
    return " [" + totalCost + " / " + directCost + "]";
  }

  public int getMethodLineNumber() {
    return lineNumber;
  }

  public List<ViolationCost> getViolationCosts() {
    return costSources;
  }

  public Cost getCost() {
    return directCost;
  }

  //TODO: inline
  public int getOverallCost() {
    return getTotalCost().getOverall();
  }

  public Map<String, Object> getAttributes() {
    Map<String, Object> map = totalCost.getAttributes();
    map.put("line", lineNumber);
    map.put("name", methodName);
    return map;
  }

}
