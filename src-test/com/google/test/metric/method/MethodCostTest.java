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
package com.google.test.metric.method;

import junit.framework.TestCase;

import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.CostViolation.Reason;

public class MethodCostTest extends TestCase {

  public void testComputeOverallCost() throws Exception {
    MethodCost cost = new MethodCost("a", 0, 1);
    cost.addGlobalCost(0, null);
    cost.addMethodCost(0, new MethodCost("b", 0, 3), Reason.NON_OVERRIDABLE_METHOD_CALL);
    CostModel costModel = new CostModel(2, 10);
    cost.link(costModel);

    assertEquals((long) 2 * (3 + 1) + 10 * 1, cost.getOverallCost());
  }

}
