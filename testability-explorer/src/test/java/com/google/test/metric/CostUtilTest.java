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

import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

public class CostUtilTest extends TestCase {

  private MetricComputerJavaDecorator decoratedComputer;
  private final ClassRepository repo = new JavaClassRepository();

  @Override
  protected void setUp() throws Exception {
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
  }

  public void testInstanceCost0() {
    assertEquals(1, totalGlobalCost("boolean instanceCost0()"));
    assertEquals(0, cyclomaticCost("boolean instanceCost0()"));
    assertEquals(0, globalCost("boolean instanceCost0()"));
    assertEquals(0, totalComplexityCost("boolean instanceCost0()"));
  }

  public void testStaticCost0() {
    assertEquals(0, cyclomaticCost("boolean staticCost0()"));
    assertEquals(0, globalCost("boolean staticCost0()"));
    assertEquals(0, totalComplexityCost("boolean staticCost0()"));
    assertEquals(1, totalGlobalCost("boolean staticCost0()"));
  }

  public void testInstanceCost1() {
    assertEquals(1, totalGlobalCost("boolean instanceCost1()"));
    assertEquals(1, cyclomaticCost("boolean instanceCost1()"));
    assertEquals(0, globalCost("boolean instanceCost1()"));
    assertEquals(1, totalComplexityCost("boolean instanceCost1()"));
  }

  public void testStaticCost1() {
    assertEquals(1, cyclomaticCost("boolean staticCost1()"));
    assertEquals(0, globalCost("boolean staticCost1()"));
    assertEquals(1, totalComplexityCost("boolean staticCost1()"));
    assertEquals(1, totalGlobalCost("boolean staticCost1()"));
  }

  public void testInstanceCost2() {
    assertEquals(2, cyclomaticCost("boolean instanceCost2()"));
    assertEquals(0, globalCost("boolean instanceCost2()"));
    assertEquals(2, totalComplexityCost("boolean instanceCost2()"));
    assertEquals(1, totalGlobalCost("boolean instanceCost2()"));
  }

  public void testStatcCost2() {
    assertEquals(2, cyclomaticCost("boolean staticCost2()"));
    assertEquals(0, globalCost("boolean staticCost2()"));
    assertEquals(2, totalComplexityCost("boolean staticCost2()"));
    assertEquals(1, totalGlobalCost("boolean staticCost2()"));
  }

  public void testInstanceCost3() {
    assertEquals(3, cyclomaticCost("boolean instanceCost3()"));
    assertEquals(0, globalCost("boolean instanceCost3()"));
    assertEquals(3, totalComplexityCost("boolean instanceCost3()"));
    assertEquals(1, totalGlobalCost("boolean instanceCost3()"));
  }

  public void testStaticCost3() {
    assertEquals(3, cyclomaticCost("boolean staticCost3()"));
    assertEquals(0, globalCost("boolean staticCost3()"));
    assertEquals(3, totalComplexityCost("boolean staticCost3()"));
    assertEquals(1, totalGlobalCost("boolean staticCost3()"));
  }

  public void testInstanceCost4() {
    assertEquals(4, cyclomaticCost("boolean instanceCost4()"));
    assertEquals(0, globalCost("boolean instanceCost4()"));
    assertEquals(4, totalComplexityCost("boolean instanceCost4()"));
    assertEquals(1, totalGlobalCost("boolean instanceCost4()"));
  }

  public void testStaticCost4() {
    assertEquals(4, cyclomaticCost("boolean staticCost4()"));
    assertEquals(0, globalCost("boolean staticCost4()"));
    assertEquals(4, totalComplexityCost("boolean staticCost4()"));
    assertEquals(1, totalGlobalCost("boolean staticCost4()"));
  }

  public void testCostUtilClassCost() throws Exception {
    ClassCost classCost = decoratedComputer.compute(CostUtil.class);
    assertEquals(4, classCost.getHighestMethodComplexityCost());
    assertEquals(1, classCost.getHighestMethodGlobalCost());
    assertEquals(20, classCost.getTotalComplexityCost());
    assertEquals(12, classCost.getTotalGlobalCost());
  }

  private int totalComplexityCost(String method) {
    return methodCostFor(method).getTotalCost().getCyclomaticComplexityCost();
  }

  private int cyclomaticCost(String method) {
    return methodCostFor(method).getCost().getCyclomaticComplexityCost();
  }

  private int globalCost(String method) {
    return methodCostFor(method).getCost().getGlobalCost();
  }

  private int totalGlobalCost(String method) {
    return methodCostFor(method).getTotalCost().getGlobalCost();
  }

  private MethodCost methodCostFor(String method) {
    MethodCost cost = decoratedComputer.compute(CostUtil.class, method);
    return cost;
  }
}
