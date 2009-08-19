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

import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

public class WorkInConstructorTest extends AutoFieldClearTestCase {

  private ClassRepository repo = new JavaClassRepository();
  private MetricComputerJavaDecorator computer = new MetricComputerJavaDecorator(
      new MetricComputerBuilder().withClassRepository(repo).build(), repo);

  static class ConstructorDoesWork {
    ConstructorDoesWork() {
      CostUtil.staticCost3();
    }

    void method() {
    }
  }

  public void testWorkInConstructorGetsPenalized() throws Exception {
    ClassCost classCost = computer.compute(ConstructorDoesWork.class);
    MethodCost constCost = classCost.getMethodCost("ConstructorDoesWork()");
    assertNotNull(constCost);
    assertEquals(3, constCost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(0, constCost.getDirectCost().getCyclomaticComplexityCost());
    assertEquals(3, constCost.getDependentCost().getCyclomaticComplexityCost());
    assertEquals(0, constCost.getConstructorDependentCost().getCyclomaticComplexityCost());

    MethodCost methodCost = classCost.getMethodCost("void method()");
    assertNotNull(methodCost);
    assertEquals(3, methodCost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getDirectCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getDependentCost().getCyclomaticComplexityCost());
    assertEquals(3, methodCost.getConstructorDependentCost().getCyclomaticComplexityCost());

    //fail("Add cost model assertions here");
  }

  static class DependantConstructorDoesWork {
   void method() {
     new ConstructorDoesWork().method();
   }
  }

  public void testWorkInDependantConstructorDoesNotGetPenalized() throws Exception {
    ClassCost classCost = computer.compute(DependantConstructorDoesWork.class);
    MethodCost constCost = classCost.getMethodCost("DependantConstructorDoesWork()");
    assertNotNull(constCost);
    assertEquals(0, constCost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(0, constCost.getDirectCost().getCyclomaticComplexityCost());
    assertEquals(0, constCost.getDependentCost().getCyclomaticComplexityCost());
    assertEquals(0, constCost.getConstructorDependentCost().getCyclomaticComplexityCost());

    MethodCost methodCost = classCost.getMethodCost("void method()");
    assertNotNull(methodCost);
    assertEquals(3, methodCost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getDirectCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getDependentCost().getCyclomaticComplexityCost());
    assertEquals(3, methodCost.getConstructorDependentCost().getCyclomaticComplexityCost());

    //fail("Add cost model assertions here");
  }

}
