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

import junit.framework.TestCase;

public class ClassCostTest extends TestCase {

  private final  MethodCost methodCost0 = new MethodCost("c.g.t.A.method0()V", 0);
  private final  MethodCost methodCost1 = new MethodCost("c.g.t.A.method1()V", 0);
  private final  MethodCost methodCost2 = new MethodCost("c.g.t.A.method2()V", 0);

  private ClassCost classCost0;
  private ClassCost classCost1;
  private ClassCost classCost2;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    methodCost1.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));

    methodCost2.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    methodCost2.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));

    methodCost0.link();
    methodCost1.link();
    methodCost2.link();
    classCost0 = new ClassCost("FAKE_classInfo0", asList(methodCost0));
    classCost1 = new ClassCost("FAKE_classInfo1", asList(methodCost1));
    classCost2 = new ClassCost("FAKE_classInfo2", asList(methodCost2));
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

    Collections.sort(classCosts, new ClassCost.CostComparator(new CostModel()));

    assertEquals(classCost2, classCosts.get(0));
    assertEquals(classCost1, classCosts.get(1));
    assertEquals(classCost0, classCosts.get(2));
  }

  public void testGetPackageName() throws Exception {
    ClassCost classCost0 = new ClassCost("com.a.b.c.Dee", asList(methodCost0));

    assertEquals("com.a.b.c", classCost0.getPackageName());

    classCost0 = new ClassCost("Dee", asList(methodCost0));

    assertEquals("", classCost0.getPackageName());
  }

}
