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

  private final double cyclomaticMultiplier;
  private final double globalMultiplier;

  public CostModel() {
    this(1, 10);
  }

  public CostModel(double cyclomaticMultiplier, double globalMultiplier) {
    this.cyclomaticMultiplier = cyclomaticMultiplier;
    this.globalMultiplier = globalMultiplier;
  }

  public long computeMethod(long cyclomaticCost, long globalCost) {
    return (long)(cyclomaticMultiplier * cyclomaticCost + globalMultiplier * globalCost);
  }

  public long computeClass(List<MethodCost> methods) {
    WeightedAverage average = new WeightedAverage(1.5); // Favor expensive methods heavily
    for (MethodCost methodCost : methods) {
      average.addValue(methodCost.getOverallCost());
    }
    return (long) average.getAverage();
  }

}
