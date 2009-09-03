// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric;

import junit.framework.TestCase;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class CostModelTest extends TestCase {
  static class HasConstructorCosts {
    HasConstructorCosts() {
      new CostUtil().instanceCost1();
    }

    void doThing() {
      new CostUtil().instanceCost3();
    }
  }
  public void testCostModelWithConstructorMultiplier() throws Exception {
    ClassCost cost =
        new MetricComputer(new JavaClassRepository(), null, null, 1)
            .compute(HasConstructorCosts.class.getCanonicalName());
    assertEquals((4 + 1) / 2, new CostModel().computeClass(cost));
    assertEquals((13 + 10) / 2, new CostModel(1, 1, 10).computeClass(cost));
  }

}
