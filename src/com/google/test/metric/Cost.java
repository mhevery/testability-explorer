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



public class Cost {

  private int cyclomaticCost;
  private int globalCost;
  private final int[] lodDistribution;
  private int overall;

  public Cost(int cyclomaticCost, int globalCost, int[] lodDistribution) {
    this.cyclomaticCost = cyclomaticCost;
    this.globalCost = globalCost;
    this.lodDistribution = lodDistribution;
  }

  public static Cost none() {
    return new Cost(0, 0, null);
  }

  public void add(Cost cost) {
    cyclomaticCost += cost.cyclomaticCost;
    globalCost += cost.globalCost;
  }

  public int getCyclomaticComplexityCost() {
    return cyclomaticCost;
  }

  public int getGlobalCost() {
    return globalCost;
  }

  @Override
  public String toString() {
    return cyclomaticCost + ", " + globalCost;
  }

  public Cost copy() {
    return new Cost(cyclomaticCost, globalCost, lodDistribution);
  }

  public int getOvarall() {
    return overall;
  }

  public void link(CostModel costModel) {
    overall = costModel.computeMethod(this);
  }

}
