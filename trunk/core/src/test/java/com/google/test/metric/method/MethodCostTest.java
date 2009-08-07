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

import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.GlobalCost;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvocationCost;
import com.google.test.metric.MetricComputer;
import static com.google.test.metric.Reason.IMPLICIT_STATIC_INIT;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.SourceLocation;

import junit.framework.TestCase;

public class MethodCostTest extends TestCase {

  public void testComputeOverallCost() throws Exception {
    MethodCost cost = new MethodCost("", "a", 0, false, false, false);
    cost.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    cost.addCostSource(new GlobalCost(new SourceLocation(null, 0), null, Cost.global(1)));
    MethodCost cost3 = new MethodCost("", "b", 0, false, false, false);
    cost3.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    cost3.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    cost3.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    cost.addCostSource(new MethodInvocationCost(new SourceLocation(null, 0), cost3,
        IMPLICIT_STATIC_INIT, Cost.cyclomatic(3)));
    CostModel costModel = new CostModel(2, 10);
    cost.link();

    assertEquals((long) 2 * (3 + 1) + 10 * 1, costModel.computeOverall(cost.getTotalCost()));
    assertEquals(2, cost.getExplicitViolationCosts().size());
    assertEquals(1, cost.getImplicitViolationCosts().size());
  }

  private static class Setters {
    boolean foo;
    public void setFoo(String foo) {
      this.foo = (foo == null);
    }
  }

  public void testImplicitSetterCostShouldNotBeDoubleCounted() throws Exception {
    MetricComputer computer = new MetricComputer(new JavaClassRepository(), null, new RegExpWhiteList(), 1);
    ClassCost cost = computer.compute(Setters.class.getCanonicalName());
    MethodCost cost1 = cost.getMethodCost("void setFoo(java.lang.String)");
    assertEquals(1, cost1.getTotalCost().getCyclomaticComplexityCost());
  }
}
