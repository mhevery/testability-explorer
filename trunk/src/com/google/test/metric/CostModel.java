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

import java.util.List;

public class CostModel {

  /**
   * Increase the weight we give on expensive methods. The ClassCost weighted
   * average will be skewed towards expensive-to-test methods' costs within the class.
   */
  public static final double WEIGHT_TO_EMPHASIZE_EXPENSIVE_METHODS = 1.5;
  private static final int DEFAULT_CYCLOMATIC_MULTIPLIER = 1;
  private static final int DEFAULT_GLOBAL_MULTIPLIER = 10;
  private final double cyclomaticMultiplier;
  private final double globalMultiplier;

  public CostModel() {
    this(DEFAULT_CYCLOMATIC_MULTIPLIER, DEFAULT_GLOBAL_MULTIPLIER);
  }

  public CostModel(double cyclomaticMultiplier, double globalMultiplier) {
    this.cyclomaticMultiplier = cyclomaticMultiplier;
    this.globalMultiplier = globalMultiplier;
  }

  public int computeMethod(Cost cost) {
    return (int)(cyclomaticMultiplier * cost.getCyclomaticComplexityCost() + globalMultiplier * cost.getGlobalCost());
  }

  public long computeClass(List<MethodCost> methods) {
    WeightedAverage average = new WeightedAverage(WEIGHT_TO_EMPHASIZE_EXPENSIVE_METHODS);
    for (MethodCost methodCost : methods) {
      average.addValue(computeMethod(methodCost.getTotalCost()));
    }
    return (long) average.getAverage();
  }

}
