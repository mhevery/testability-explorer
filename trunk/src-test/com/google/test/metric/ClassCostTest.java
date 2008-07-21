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

import java.util.*;

import junit.framework.TestCase;

public class ClassCostTest extends TestCase {

  private final  MethodCost methodCost0 = new MethodCost("c.g.t.A.method0()V", 0, 0);
  private final  MethodCost methodCost1 = new MethodCost("c.g.t.A.method1()V", 0, 1);
  private final  MethodCost methodCost2 = new MethodCost("c.g.t.A.method2()V", 0, 2);

  ClassCost classCost0;
  ClassCost classCost1;
  ClassCost classCost2;
  private List<MethodCost> methodCosts0;
  private List<MethodCost> methodCosts1;
  private List<MethodCost> methodCosts2;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    CostModel context = new CostModel();
    methodCost0.link(context);
    methodCost1.link(context);
    methodCost2.link(context);

    methodCosts0 = new ArrayList<MethodCost>();
    methodCosts0.add(methodCost0);

    methodCosts1 = new ArrayList<MethodCost>();
    methodCosts1.add(methodCost0);
    methodCosts1.add(methodCost1);

    methodCosts2 = new ArrayList<MethodCost>();
    methodCosts2.add(methodCost0);
    methodCosts2.add(methodCost1);
    methodCosts2.add(methodCost2);

    classCost0 = new ClassCost("FAKE_classInfo0", methodCosts0);
    classCost1 = new ClassCost("FAKE_classInfo1", methodCosts1);
    classCost2 = new ClassCost("FAKE_classInfo2", methodCosts2);
  }

  public void testSumsUpTotalClassCostCorrectly() throws Exception {
    assertEquals(0, classCost0.getTotalComplexityCost());
    assertEquals(1, classCost1.getTotalComplexityCost());
    assertEquals(3, classCost2.getTotalComplexityCost());
  }

  public void testClassCostSortsByDescendingCost() throws Exception {
    List<ClassCost> classCosts = new ArrayList<ClassCost>();
    CostModel costModel = new CostModel();
    classCost0.link(costModel);
    classCost1.link(costModel);
    classCost2.link(costModel);
    classCosts.add(classCost1);
    classCosts.add(classCost0);
    classCosts.add(classCost2);
    Collections.sort(classCosts, new ClassCost.CostComparator());
    assertEquals(classCost2, classCosts.get(0));
    assertEquals(classCost1, classCosts.get(1));
    assertEquals(classCost0, classCosts.get(2));
  }

  public void testGetPackageName() throws Exception {
    ClassCost classCost0 = new ClassCost("com.a.b.c.Dee", methodCosts0);

    assertEquals("com.a.b.c", classCost0.getPackageName());

    classCost0 = new ClassCost("Dee", methodCosts0);

    assertEquals("", classCost0.getPackageName());
  }

  public void testPackageComparator() throws Exception {
    ClassCost classCost0 = new ClassCost("com.a.b.c.Dab", methodCosts0);
    ClassCost classCost1 = new ClassCost("com.a.b.c.Dac", methodCosts1);
    ClassCost classCost2 = new ClassCost("com.a.b.c.Daa", methodCosts0);
    ClassCost classCost3 = new ClassCost("com.a.b.c.Dxx", methodCosts2);

    SortedSet ss = new TreeSet<ClassCost>(new ClassCost.PackageComparator());

    ss.add(classCost3);
    ss.add(classCost0);
    ss.add(classCost2);
    ss.add(classCost1);

    ClassCost[] ccs = (ClassCost[]) ss.toArray(new ClassCost[ss.size()]);

    assertEquals(ccs[0], classCost2);
    assertEquals(ccs[1], classCost0);
    assertEquals(ccs[2], classCost1);
    assertEquals(ccs[3], classCost3);
  }
}
