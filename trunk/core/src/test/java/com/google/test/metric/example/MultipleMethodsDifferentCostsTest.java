/*
 * Copyright 2008 Google Inc.
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
import com.google.test.metric.MetricComputer;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

/**
 * This test illustrates two principles:
 * <ol>
 * <li>That methods are not double counted if the same offense occurs several
 * times in one method.</li>
 * <li>That method calls are only counted if they are <em>non-overridable</em>.
 * This means final, static, or private methods have no ability to be altered in
 * tests. You're forced to deal with their complexity in tests.</li>
 * </ol>
 *
 * <p>
 * Note: Just because a method is overridable (i.e. by subclassing the SUT),
 * does not mean it is optimally test-friendly. We believe that subclassing a
 * SUT for testing is something to do only in a worst-case scenario. (i.e. with
 * legacy untestable code). For new code, prefer composition of your objects
 * through constructor Dependency Injection.
 *
 * @author Jonathan Wolter
 *
 */
public class MultipleMethodsDifferentCostsTest extends AutoFieldClearTestCase {

  MetricComputerJavaDecorator decoratedComputer;

  @Override
  protected void setUp() throws Exception {
    ClassRepository repo = new JavaClassRepository();
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
  }

  public void testOverridableTcc0Cost() throws Exception {
    MethodCost methodCost = cost("methodOverridableTcc4()V");
    assertEquals(4, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    assertEquals(4, methodCost.getTotalCost().getCyclomaticComplexityCost());

    methodCost = cost("methodTcc0BecauseOverridableMethodCalls()V");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testStaticTcc4Cost() throws Exception {
    MethodCost methodCost = cost("methodStaticTcc4()V");
    assertEquals(4, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    assertEquals(4, methodCost.getTotalCost().getCyclomaticComplexityCost());

    methodCost = cost("methodTcc4BecauseStaticMethodCalls()V");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    // TCC 4 because it doesn't double count for the same offense within one method
    assertEquals(4, methodCost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testPrivateTcc4Cost() throws Exception {
    MethodCost methodCost = cost("methodPrivateTcc4()V");
    assertEquals(4, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    assertEquals(4, methodCost.getTotalCost().getCyclomaticComplexityCost());

    methodCost = cost("methodTcc4BecausePrivateMethodCalls()V");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    // TCC 4 because it doesn't double count for the same offense within one method
    assertEquals(4, methodCost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testFinalTcc4Cost() throws Exception {
    MethodCost methodCost = cost("methodFinalTcc4()V");
    assertEquals(4, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    assertEquals(4, methodCost.getTotalCost().getCyclomaticComplexityCost());

    methodCost = cost("methodTcc4BecauseFinalMethodCalls()V");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    // TCC 4 because it doesn't double count for the same offense within one method
    assertEquals(4, methodCost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testMultipleDifferentNonOverridibleMethodsTcc12Cost() throws Exception {
    MethodCost methodCost = cost("methodTcc12BecauseMultipleDifferentNonOverridableMethodCalls()V");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    assertEquals(12, methodCost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testClassCost() throws Exception {
    ClassCost classCost = decoratedComputer.compute(MultipleMethodsDifferentCosts.class);
    assertEquals(12, classCost.getHighestMethodComplexityCost());
    assertEquals(0, classCost.getHighestMethodGlobalCost());
    // overall cost is a mysterious numbers that is calculated with a weighted average formula
    float weight = 1.5f;
    long expectedOverallCost = (long)
        ((Math.pow(4, weight + 1) * 7 + Math.pow(12, weight + 1))
          /
        (Math.pow(4, weight) * 7 + Math.pow(12, weight)));
    assertEquals(7, expectedOverallCost);
    assertEquals(4*7 + 12, classCost.getTotalComplexityCost());
    assertEquals(0, classCost.getTotalGlobalCost());
  }

  private MethodCost cost(String methodDesc) {
    return decoratedComputer.compute(MultipleMethodsDifferentCosts.class, methodDesc);
  }


  public static class MultipleMethodsDifferentCosts {

    void methodTcc0BecauseOverridableMethodCalls() {
      methodOverridableTcc4();
      methodOverridableTcc4();
    }

    void methodOverridableTcc4() {
      int i = 1;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
    }

    void methodTcc4BecauseStaticMethodCalls() {
      // Two of the same non-overidable method calls don't double count against you. Should they?
      methodStaticTcc4();
      methodStaticTcc4();
    }

    static void methodStaticTcc4() {
      int i = 1;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
    }

    void methodTcc4BecausePrivateMethodCalls() {
      // Two of the same non-overidable method calls don't double count against you. Should they?
      methodPrivateTcc4();
      methodPrivateTcc4();
    }

    private void methodPrivateTcc4() {
      int i = 1;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
    }

    void methodTcc4BecauseFinalMethodCalls() {
      // Two of the same non-overidable method calls don't double count against you. Should they?
      methodFinalTcc4();
      methodFinalTcc4();
    }

    final void methodFinalTcc4() {
      int i = 1;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
      i = i > 4 ? 33 : 0;
    }

    void methodTcc12BecauseMultipleDifferentNonOverridableMethodCalls() {
      // This method is overridable by subclassing.
      methodOverridableTcc4();

      // These methods are not overridable. There are no seams.
      methodStaticTcc4();
      methodPrivateTcc4();
      methodFinalTcc4();
    }
  }

}
