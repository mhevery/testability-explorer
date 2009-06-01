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
    assertEquals(1, totalGlobalCost("instanceCost0()Z"));
    assertEquals(0, cyclomaticCost("instanceCost0()Z"));
    assertEquals(0, globalCost("instanceCost0()Z"));
    assertEquals(0, totalComplexityCost("instanceCost0()Z"));
  }

  public void testStaticCost0() {
    assertEquals(0, cyclomaticCost("staticCost0()Z"));
    assertEquals(0, globalCost("staticCost0()Z"));
    assertEquals(0, totalComplexityCost("staticCost0()Z"));
    assertEquals(1, totalGlobalCost("staticCost0()Z"));
  }

  public void testInstanceCost1() {
    assertEquals(1, totalGlobalCost("instanceCost1()Z"));
    assertEquals(1, cyclomaticCost("instanceCost1()Z"));
    assertEquals(0, globalCost("instanceCost1()Z"));
    assertEquals(1, totalComplexityCost("instanceCost1()Z"));
  }

  public void testStaticCost1() {
    assertEquals(1, cyclomaticCost("staticCost1()Z"));
    assertEquals(0, globalCost("staticCost1()Z"));
    assertEquals(1, totalComplexityCost("staticCost1()Z"));
    assertEquals(1, totalGlobalCost("staticCost1()Z"));
  }

  public void testInstanceCost2() {
    assertEquals(2, cyclomaticCost("instanceCost2()Z"));
    assertEquals(0, globalCost("instanceCost2()Z"));
    assertEquals(2, totalComplexityCost("instanceCost2()Z"));
    assertEquals(1, totalGlobalCost("instanceCost2()Z"));
  }

  public void testStatcCost2() {
    assertEquals(2, cyclomaticCost("staticCost2()Z"));
    assertEquals(0, globalCost("staticCost2()Z"));
    assertEquals(2, totalComplexityCost("staticCost2()Z"));
    assertEquals(1, totalGlobalCost("staticCost2()Z"));
  }

  public void testInstanceCost3() {
    assertEquals(3, cyclomaticCost("instanceCost3()Z"));
    assertEquals(0, globalCost("instanceCost3()Z"));
    assertEquals(3, totalComplexityCost("instanceCost3()Z"));
    assertEquals(1, totalGlobalCost("instanceCost3()Z"));
  }

  public void testStaticCost3() {
    assertEquals(3, cyclomaticCost("staticCost3()Z"));
    assertEquals(0, globalCost("staticCost3()Z"));
    assertEquals(3, totalComplexityCost("staticCost3()Z"));
    assertEquals(1, totalGlobalCost("staticCost3()Z"));
  }

  public void testInstanceCost4() {
    assertEquals(4, cyclomaticCost("instanceCost4()Z"));
    assertEquals(0, globalCost("instanceCost4()Z"));
    assertEquals(4, totalComplexityCost("instanceCost4()Z"));
    assertEquals(1, totalGlobalCost("instanceCost4()Z"));
  }

  public void testStaticCost4() {
    assertEquals(4, cyclomaticCost("staticCost4()Z"));
    assertEquals(0, globalCost("staticCost4()Z"));
    assertEquals(4, totalComplexityCost("staticCost4()Z"));
    assertEquals(1, totalGlobalCost("staticCost4()Z"));
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
