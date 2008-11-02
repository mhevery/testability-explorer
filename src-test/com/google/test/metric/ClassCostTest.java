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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.TestCase;

public class ClassCostTest extends TestCase {

  private final CostModel costModel = new CostModel();
  private final  MethodCost methodCost0 = new MethodCost("c.g.t.A.method0()V", 0);
  private final  MethodCost methodCost1 = new MethodCost("c.g.t.A.method1()V", 0);
  private final  MethodCost methodCost2 = new MethodCost("c.g.t.A.method2()V", 0);

  private ClassCost classCost0;
  private ClassCost classCost1;
  private ClassCost classCost2;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    methodCost1.addCyclomaticCost(0);

    methodCost2.addCyclomaticCost(0);
    methodCost2.addCyclomaticCost(0);

    CostModel context = new CostModel();
    methodCost0.link(context);
    methodCost1.link(context);
    methodCost2.link(context);
    classCost0 = new ClassCost("FAKE_classInfo0", asList(methodCost0), costModel);
    classCost1 = new ClassCost("FAKE_classInfo1", asList(methodCost1), costModel);
    classCost2 = new ClassCost("FAKE_classInfo2", asList(methodCost2), costModel);
  }

  public void testSumsUpTotalClassCostCorrectly() throws Exception {
    assertEquals(0, classCost0.getTotalComplexityCost());
    assertEquals(1, classCost1.getTotalComplexityCost());
    assertEquals(2, classCost2.getTotalComplexityCost());
  }

  public void testClassCostSortsByDescendingCost() throws Exception {
    List<ClassCost> classCosts = new ArrayList<ClassCost>();
    classCosts.add(classCost1);
    classCosts.add(classCost0);
    classCosts.add(classCost2);

    Collections.sort(classCosts, new ClassCost.CostComparator());

    assertEquals(classCost2, classCosts.get(0));
    assertEquals(classCost1, classCosts.get(1));
    assertEquals(classCost0, classCosts.get(2));
  }

  public void testGetPackageName() throws Exception {
    ClassCost classCost0 = new ClassCost("com.a.b.c.Dee", asList(methodCost0), costModel);

    assertEquals("com.a.b.c", classCost0.getPackageName());

    classCost0 = new ClassCost("Dee", asList(methodCost0), costModel);

    assertEquals("", classCost0.getPackageName());
  }

  public void testPackageComparator() throws Exception {
    ClassCost classCost0 = new ClassCost("com.a.b.c.Dab", asList(methodCost0), costModel);
    ClassCost classCost1 = new ClassCost("com.a.b.c.Dac", asList(methodCost1), costModel);
    ClassCost classCost2 = new ClassCost("com.a.b.c.Daa", asList(methodCost0), costModel);
    ClassCost classCost3 = new ClassCost("com.a.b.c.Dxx", asList(methodCost2), costModel);

    SortedSet<ClassCost> ss = new TreeSet<ClassCost>(new ClassCost.PackageComparator());

    ss.add(classCost3);
    ss.add(classCost0);
    ss.add(classCost2);
    ss.add(classCost1);

    ClassCost[] ccs = ss.toArray(new ClassCost[ss.size()]);

    assertEquals(ccs[0], classCost2);
    assertEquals(ccs[1], classCost0);
    assertEquals(ccs[2], classCost1);
    assertEquals(ccs[3], classCost3);
  }
}
