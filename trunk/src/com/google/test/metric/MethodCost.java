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

import com.google.test.metric.LineNumberCost.CostSourceType;

public class MethodCost {

  private final String methodName;
  private final long cyclomaticCost;
  private final int lineNumber;
  private final List<LineNumberCost> operationCosts = new ArrayList<LineNumberCost>();
  private final List<GlobalStateCost> globalStateCosts = new ArrayList<GlobalStateCost>();
  private long totalGlobalCost;
  private long totalComplexityCost;
  private long overallCost;
  private boolean linked = false;

  /**
   * @param methodName name of the method, such as {@code void myMethod()}.
   * @param lineNumber line number
   * @param cyclomaticCost complexity cost for this method, alone. Later the costs of
   * the other methods that this method calls will be added.
   */
  public MethodCost(String methodName, int lineNumber, long cyclomaticCost) {
    this.methodName = methodName;
    this.lineNumber = lineNumber;
    this.cyclomaticCost = cyclomaticCost;
  }

  public long getTotalComplexityCost() {
    assertLinked();
    return totalComplexityCost;
  }

  public long getTotalGlobalCost() {
    assertLinked();
    return totalGlobalCost;
  }

  private void assertLinked() {
    if (!linked) {
      throw new IllegalStateException("Need to link first.");
    }
  }

  private void assertNotLinked() {
    if (linked) {
      throw new IllegalStateException("Can not call after linked.");
    }
  }

  public void link(CostModel costModel) {
    if (!linked) {
      linked = true;
      long totalGlobalCost = getGlobalCost();
      long totalComplexityCost = getCyclomaticCost();
      for (LineNumberCost operationCost : operationCosts) {
        MethodCost childCost = operationCost.getMethodCost();
        childCost.link(costModel);
        // TODO(Jwolter): have a Cost object that represents the type of cost rather than just a number.
        totalGlobalCost += childCost.getTotalGlobalCost();
        totalComplexityCost += childCost.getTotalComplexityCost();
      }
      this.totalComplexityCost = totalComplexityCost;
      this.totalGlobalCost = totalGlobalCost;
      overallCost = costModel.computeMethod(getTotalComplexityCost(),
          getTotalGlobalCost());
    }
  }

  public long getGlobalCost() {
    assertLinked();
    return globalStateCosts.size();
  }

  public List<LineNumberCost> getOperationCosts() {
    return operationCosts;
  }

  public String getMethodName() {
    return methodName;
  }

  public void addMethodCost(int lineNumber, MethodCost to, CostSourceType costSourceType) {
    assertNotLinked();
    operationCosts.add(new LineNumberCost(lineNumber, to, costSourceType));
  }

  public void addGlobalCost(int lineNumber, Variable variable) {
    assertNotLinked();
    globalStateCosts.add(new GlobalStateCost(lineNumber, variable));
  }

  // TODO(jwolter): enable reporting of *why* each class has the cost that it has.
  // How do you want this to be different from the DetailHtmlReport? Well, first look at it
  // and see how it works. In practice, real lifelike scenarios.
  public String getSourcesOfCost() {
    StringBuilder sb = new StringBuilder();
    sb.append("Complexity Cost of " + this.methodName + " itself " + this.cyclomaticCost + "\n")
      .append("Operation Costs:\n");

    return sb.toString();
  }

  @Override
  public String toString() {
    return getMethodName() + toCostsString();
  }

  public String toCostsString() {
    return " [" + getCyclomaticCost() + ", " + getGlobalCost() + " / "
        + getTotalComplexityCost() + ", " + getTotalGlobalCost() + "]";
  }

  public long getCyclomaticCost() {
    assertLinked();
    return cyclomaticCost;
  }

  public int getMethodLineNumber() {
    return lineNumber;
  }

  public long getOverallCost() {
    assertLinked();
    return overallCost;
  }

}
