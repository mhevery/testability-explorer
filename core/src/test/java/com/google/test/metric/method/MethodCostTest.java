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

import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.GlobalCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvokationCost;
import static com.google.test.metric.Reason.IMPLICIT_STATIC_INIT;

public class MethodCostTest extends TestCase {

  public void testComputeOverallCost() throws Exception {
    MethodCost cost = new MethodCost("a", 0, false, false);
    cost.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    cost.addCostSource(new GlobalCost(0, null, Cost.global(1)));
    MethodCost cost3 = new MethodCost("b", 0, false, false);
    cost3.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    cost3.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    cost3.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    cost.addCostSource(new MethodInvokationCost(0, cost3,
      IMPLICIT_STATIC_INIT, Cost.cyclomatic(3)));
    CostModel costModel = new CostModel(2, 10);
    cost.link();

    assertEquals((long) 2 * (3 + 1) + 10 * 1, costModel.computeOverall(cost.getTotalCost()));
    assertEquals(2, cost.getExplicitViolationCosts().size());
    assertEquals(1, cost.getImplicitViolationCosts().size());
  }

  public void testShortFormatting() throws Exception {
    MethodCost methodCost = new MethodCost("int com.google.longpackagename.Foo.thing()",
        1, false, false);
    assertEquals("int thing()", methodCost.shortFormat());
  }

  public void testShortFormattingWithParameters() throws Exception {
    MethodCost methodCost = new MethodCost("t.n.e(p.e, a.b)",
        1, false, false);
    assertEquals("e(e, b)", methodCost.shortFormat());
  }

  public void testInnerClassMethodNamesShortFormatting() throws Exception {
    MethodCost methodCost = new MethodCost("String com.google.Outer$HasInner.computeString()",
        1, false, false);
    assertEquals("String computeString()", methodCost.shortFormat());
  }

  public void testInnerClassConstructorMethod() throws Exception {
    MethodCost methodCost = new MethodCost("com.google.test.metric.example.ExpensiveConstructor."
        + "StaticWorkInTheConstructor$StaticHolder()", 1, true, false);
    assertEquals("StaticWorkInTheConstructor$StaticHolder()", methodCost.shortFormat());
  }
}
