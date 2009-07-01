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
  private static final int DEFAULT_CONSTRUCTOR_DOES_WORK_MULTIPLIER = 1;
  private final double cyclomaticMultiplier;
  private final double globalMultiplier;
  private final double constructorDoesWorkMultiplier;

  public double weightToEmphasizeExpensiveMethods = WEIGHT_TO_EMPHASIZE_EXPENSIVE_METHODS;

  /* For testing only */
  public CostModel() {
    this(DEFAULT_CYCLOMATIC_MULTIPLIER, DEFAULT_GLOBAL_MULTIPLIER, DEFAULT_CONSTRUCTOR_DOES_WORK_MULTIPLIER);
    weightToEmphasizeExpensiveMethods = 0;
  }

  public CostModel(double cyclomaticMultiplier, double globalMultiplier,
      double constructorDoesWorkMultiplier) {
    this.cyclomaticMultiplier = cyclomaticMultiplier;
    this.globalMultiplier = globalMultiplier;
    this.constructorDoesWorkMultiplier = constructorDoesWorkMultiplier;
  }

  public int computeOverall(Cost cost) {
    return computeOverallWithConstructor(cost, 1);
  }
  
  private int computeOverallWithConstructor(Cost cost, double constructorMultiplier) {
    int sum = 0;
    
    sum += cyclomaticMultiplier * cost.getCyclomaticComplexityCost();
    sum += globalMultiplier * cost.getGlobalCost();
    
    for (int count : cost.getLoDDistribution()) {
      sum += count;
    }
    sum *= constructorMultiplier;
    return sum;
  }

  public int computeClass(ClassCost classCost) {
    return computeClassWithoutMethod(classCost, null, null);
  }

  private int computeClassWithoutMethod(ClassCost classCost, MethodCost adjustedMethod,
                                        Cost replacementCost) {
    WeightedAverage average = new WeightedAverage(weightToEmphasizeExpensiveMethods);
    double constructorMultiplier;
    for (MethodCost methodCost : classCost.getMethods()) {
      constructorMultiplier = 1;
      if (methodCost.isConstructor()) {
        constructorMultiplier = this.constructorDoesWorkMultiplier;
      }
      
      int totalCost = 0;
      if (adjustedMethod == methodCost) {
        totalCost += computeOverallWithConstructor(replacementCost, constructorMultiplier);
      } else {
        totalCost += computeMethodCost(methodCost);
      }
      average.addValue(totalCost);
    }
    return (int) average.getAverage();
  }

  public int computeMethodCost(MethodCost methodCost) {
    double constructorMultiplier = 1;
    if (methodCost.isConstructor()) {
      constructorMultiplier = this.constructorDoesWorkMultiplier;
    }
    
    int totalCost = 0;
    totalCost += computeOverallWithConstructor(methodCost.getDirectCost(), constructorMultiplier);
    totalCost += computeOverallWithConstructor(methodCost.getDependentCost(), constructorMultiplier);
    totalCost += computeOverallWithConstructor(
        methodCost.getConstructorDependentCost(), this.constructorDoesWorkMultiplier);
    return totalCost;
  }

  public int computeViolationCost(MethodCost methodCost, ViolationCost violationCost) {
    double constructorMultiplier = 1;
    if (methodCost.isConstructor() || violationCost instanceof ConstructorInvokationCost) {
      constructorMultiplier = this.constructorDoesWorkMultiplier;
    }
    
    return computeOverallWithConstructor(violationCost.getCost(), constructorMultiplier);
  }
  
  public int computeViolationCost(ViolationCost violationCost) {
    double constructorMultiplier = 1;
    if (violationCost instanceof ConstructorInvokationCost) {
      constructorMultiplier = this.constructorDoesWorkMultiplier;
    }
    return computeOverallWithConstructor(violationCost.getCost(), constructorMultiplier);
  }
  
  public float computeContributionFromIssue(ClassCost classCost, MethodCost violationMethodCost,
                                            ViolationCost violationCost) {
    Cost adjustedCost = violationMethodCost.getTotalCost().add(violationCost.getCost().negate());
    return 1 - computeClassWithoutMethod(classCost, violationMethodCost, adjustedCost) /
               (float)computeClass(classCost);
  }

  public float computeDirectCostContributionFromMethod(ClassCost classCost,
                                                       MethodCost violationMethodCost) {

    final float costWithoutIssue =
        computeClassWithoutMethod(classCost, violationMethodCost,
            violationMethodCost.getDependentCost());
    final float totalCost = (float) computeClass(classCost);
    return 1 - costWithoutIssue / totalCost;
  }
}
