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

import junit.framework.TestCase;

public class WeightedAverageTest extends TestCase {

  public void testNormalAverage() throws Exception {
    WeightedAverage avg = new WeightedAverage(1);
    avg.addValue(1);
    avg.addValue(3);
    assertEquals((1.0 + 3.0 * 3.0) / 4.0, avg.getAverage());
  }

}
