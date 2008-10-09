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
package com.google.test.metric.example;

import com.google.test.metric.AutoFieldClearTestCase;
import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInfo;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.example.GlobalExample.Gadget;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

public class GlobalExampleTest extends AutoFieldClearTestCase {

  private final ClassRepository repo = new JavaClassRepository();
  private MetricComputerJavaDecorator decoratedComputer;

  @Override
  protected void setUp() throws Exception {
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
  }

  public void testAccessingAFinalStaticIsOK() throws Exception {
    MethodCost cost = decoratedComputer.compute(GlobalExample.class,
        "getInstance()Lcom/google/test/metric/example/GlobalExample$Gadget;");
    assertEquals(0, cost.getTotalGlobalCost());
  }

  public void testAccessingAFinalFieldDoesNotCountAgainstYou() throws Exception {
    MethodCost cost = decoratedComputer.compute(GlobalExample.class,
        "getGlobalId()Ljava/lang/String;");
    assertEquals(0, cost.getTotalGlobalCost());
  }

  public void testAccessingANonFinalFieldCountsAgainstYou() throws Exception {
    MethodCost cost = decoratedComputer.compute(GlobalExample.class, "getGlobalCount()I");
    assertEquals(1, cost.getTotalGlobalCost());
  }

  public void testWritingANonFinalFieldCountsAgainstYou() throws Exception {
    MethodCost cost = decoratedComputer.compute(GlobalExample.class, "globalIncrement()I");
    assertEquals(1, cost.getTotalGlobalCost());
  }

  public void testGadgetGetCountHasOneReturnOperation() throws Exception {
    MethodInfo getCount = repo.getClass(Gadget.class.getName()).getMethod("getCount()I");
    assertEquals(1, getCount.getOperations().size());
  }

  public void testGadgetTotalClassCosts() {
    ClassCost cost = decoratedComputer.compute(Gadget.class);
    assertEquals(0, cost.getHighestMethodComplexityCost());
    assertEquals(0, cost.getHighestMethodGlobalCost());
    assertEquals(0, cost.getOverallCost());
    assertEquals(0, cost.getTotalComplexityCost());
    assertEquals(0, cost.getTotalGlobalCost());
  }

  public void testGlobalExampleTotalClassCosts() {
    ClassCost cost = decoratedComputer.compute(GlobalExample.class);
    assertEquals(0, cost.getHighestMethodComplexityCost());
    assertEquals(1, cost.getHighestMethodGlobalCost());
    assertEquals(10, cost.getOverallCost());
    assertEquals(0, cost.getTotalComplexityCost());
    assertEquals(2, cost.getTotalGlobalCost());
  }

}
