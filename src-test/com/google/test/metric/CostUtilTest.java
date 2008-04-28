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

public class CostUtilTest extends TestCase {

  public void testVerifyCosts() throws Exception {
    assertEquals(3, cost("staticCost3()Z"));
    assertEquals(3, cost("instanceCost3()Z"));
    assertEquals(4, cost("staticCost4()Z"));
    assertEquals(4, cost("instanceCost4()Z"));
  }

  public void testInstanceCost2() {
    assertEquals(2, cost("instanceCost2()Z"));
  }

  public void testStatcCost2() {
    assertEquals(2, cost("staticCost2()Z"));
  }

  public void testInstanceCost1() {
    assertEquals(1, cost("instanceCost1()Z"));
  }

  public void testStaticCost1() {
    assertEquals(1, cost("staticCost1()Z"));
  }

  public void testInstanceCost0() {
    assertEquals(0, cost("instanceCost0()Z"));
  }

  public void testStaticCost0() {
    assertEquals(0, cost("staticCost0()Z"));
  }

  private long cost(String method) {
    MetricComputer computer = new MetricComputer(new ClassRepository(), null,
        new RegExpWhiteList(), new CostModel());
    MethodCost cost = computer.compute(CostUtil.class, method);
    return cost.getTotalComplexityCost();
  }
}
