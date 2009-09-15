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


public class CostModel {

  /**
   * Increase the weight we give on expensive methods. The ClassCost weighted
   * average will be skewed towards expensive-to-test methods' costs within the
   * class.
   */
  public static final double WEIGHT_TO_EMPHASIZE_EXPENSIVE_METHODS = 1.5;
  private static final int DEFAULT_CYCLOMATIC_MULTIPLIER = 1;
  private static final int DEFAULT_GLOBAL_MULTIPLIER = 10;
  private static final int DEFAULT_CONSTRUCTOR_MULTIPLIER = 1;

  private final double cyclomaticMultiplier;
  private final double globalMultiplier;
  private final double constructorMultiplier;

  public double weightToEmphasizeExpensiveMethods = WEIGHT_TO_EMPHASIZE_EXPENSIVE_METHODS;

  /* For testing only */
  public CostModel() {
    this(DEFAULT_CYCLOMATIC_MULTIPLIER, DEFAULT_GLOBAL_MULTIPLIER, DEFAULT_CONSTRUCTOR_MULTIPLIER);
    weightToEmphasizeExpensiveMethods = 0;
  }

  public CostModel(double cyclomaticMultiplier, double globalMultiplier, double constructorMultiplier) {
    this.cyclomaticMultiplier = cyclomaticMultiplier;
    this.globalMultiplier = globalMultiplier;
    this.constructorMultiplier = constructorMultiplier;
  }

  public int computeOverall(Cost cost) {
    int sum = 0;
    sum += cyclomaticMultiplier * cost.getCyclomaticComplexityCost();
    sum += globalMultiplier * cost.getGlobalCost();
    for (int count : cost.getLoDDistribution()) {
      sum += count;
    }
    return sum;
  }

  public int computeClass(ClassCost classCost) {
    WeightedAverage average = createWeighedAverage();
    for (MethodCost methodCost : classCost.getMethods()) {
      double overallCost;
      if (methodCost.isConstructor()) {
        overallCost = computeOverall(methodCost.getTotalCost()) * constructorMultiplier;
      } else {
        Cost constructorCost = methodCost.getConstructorDependentCost();
        Cost nonConstructorCost = methodCost.getNonConstructorCost();
        overallCost = computeOverall(nonConstructorCost)
                           + constructorMultiplier * computeOverall(constructorCost);
      }
      average.addValue((long) overallCost);
    }
    return (int) average.getAverage();
  }

  public WeightedAverage createWeighedAverage() {
    return new WeightedAverage(weightToEmphasizeExpensiveMethods);
  }
}
