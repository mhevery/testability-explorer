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

import static java.lang.Math.pow;
import junit.framework.TestCase;

public class WeightedAverageTest extends TestCase {

  public void testNormalAverage() throws Exception {
    WeightedAverage avg = new WeightedAverage(1);
    avg.addValue(1);
    avg.addValue(3);
    assertEquals((1.0 + 3.0 * 3.0) / 4.0, avg.getAverage());
  }

  public void testWeightedAverageAsUsedInCostModel() throws Exception {
    double weight = CostModel.WEIGHT_TO_EMPHASIZE_EXPENSIVE_METHODS;
    WeightedAverage avg = new WeightedAverage(weight);
    avg.addValue(1);
    avg.addValue(3);
    double expected = (pow(1, weight + 1) + pow(3, weight + 1)) / (pow(1, weight) + pow(3, weight));
    assertEquals(expected, avg.getAverage());
  }
}
