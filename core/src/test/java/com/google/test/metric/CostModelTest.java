/*
 * Copyright 2009 Google Inc. All Rights Reserved.
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

import static com.google.test.metric.Reason.NON_OVERRIDABLE_METHOD_CALL;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * Tests for {@link com.google.test.metric.CostModel}
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class CostModelTest extends TestCase {

  public MethodCost doThingMethod;
  public CostModel costModel;
  private MethodCost methodWithIndirectCosts;


  public void testDirectCostOfAMethodCanBeSubtractedFromClassCost() {
    ClassCost classCost = new ClassCost("com.google.Foo", Arrays.asList(doThingMethod));
    MethodCost methodCost = doThingMethod;
    assertEquals(1.0f, costModel.computeDirectCostContributionFromMethod(classCost, methodCost));
  }

  public void testContributionFromOneMethodIsCorrect() {
    ClassCost classCost = new ClassCost("com.google.Foo", Arrays.asList(doThingMethod, methodWithIndirectCosts));
    MethodCost methodCost = doThingMethod;
    float costWithoutDoThing = (0 + (50 + 33)) / 2;
    float costWithDoThing = (100 + (50 + 33)) / 2;
    assertEquals(1 - costWithoutDoThing / costWithDoThing,
        costModel.computeDirectCostContributionFromMethod(classCost, methodCost));
  }

  

  protected void setUp() throws Exception {
    super.setUp();
    doThingMethod = new MethodCost("doThing()", 1, false, false, false);
    doThingMethod.addCostSource(new CyclomaticCost(new SourceLocation(null, 3), Cost.cyclomatic(100)));
    
    methodWithIndirectCosts = new MethodCost("hasIndirect()", 2, false, false, false);
    methodWithIndirectCosts.addCostSource(new CyclomaticCost(new SourceLocation(null, 4), Cost.cyclomatic(50)));
    methodWithIndirectCosts.addCostSource(new MethodInvocationCost(new SourceLocation(null, 1),
        doThingMethod, NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(33)));
    costModel = new CostModel();
  }
}
