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
import com.google.test.metric.example.FinalGlobalExample.FinalGlobal;
import com.google.test.metric.example.FinalGlobalExample.Gadget;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;


/**
 * @see MutableGlobalExampleTest MutableGlobalExampleTest for contrasting examples that access
 * mutable global state.
 *
 * @author Misko Hevery
 * @author Jonathan Wolter
 */
public class FinalGlobalExampleTest extends AutoFieldClearTestCase {

  private final ClassRepository repo = new JavaClassRepository();
  private MetricComputerJavaDecorator decoratedComputer;

  @Override
  protected void setUp() throws Exception {
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
  }

  public void testAccessingAFinalStaticIsOK() throws Exception {
    MethodCost methodCost = decoratedComputer.compute(FinalGlobalExample.class,
        "getInstance()Lcom/google/test/metric/example/FinalGlobalExample$Gadget;");
    assertEquals(0, methodCost.getCyclomaticCost());
    assertEquals(0, methodCost.getGlobalCost());
    assertEquals(0, methodCost.getTotalComplexityCost());
    assertEquals(0, methodCost.getTotalGlobalCost());
    assertEquals(0, methodCost.getOverallCost());
  }

  public void testAccessingAFinalFieldDoesNotCountAgainstYou() throws Exception {
    // This method goes into final global state (cost +0) and reads a final value (cost +0)
    MethodCost methodCost = decoratedComputer.compute(FinalGlobalExample.class,
        "getGlobalId()Ljava/lang/String;");
    assertEquals(0, methodCost.getCyclomaticCost());
    assertEquals(0, methodCost.getGlobalCost());
    assertEquals(0, methodCost.getTotalComplexityCost());
    assertEquals(0, methodCost.getTotalGlobalCost());
    assertEquals(0, methodCost.getOverallCost());
  }

  public void testAccessingANonFinalFieldCountsAgainstYou() throws Exception {
    // This method goes into final global state (cost +0) and reads a mutable value (cost +1)
    MethodCost methodCost = decoratedComputer.compute(FinalGlobalExample.class, "getGlobalCount()I");
    assertEquals(0, methodCost.getCyclomaticCost());
    assertEquals(0, methodCost.getGlobalCost());
    assertEquals(0, methodCost.getTotalComplexityCost());
    assertEquals(1, methodCost.getTotalGlobalCost());
    assertEquals(10, methodCost.getOverallCost());
  }

  public void testWritingANonFinalFieldCountsAgainstYou() throws Exception {
    // This method goes into final global state (cost +0) and writes a mutable value (cost +1)
    MethodCost methodCost = decoratedComputer.compute(FinalGlobalExample.class, "globalIncrement()I");
    assertEquals(0, methodCost.getCyclomaticCost());
    assertEquals(0, methodCost.getGlobalCost());
    assertEquals(0, methodCost.getTotalComplexityCost());
    assertEquals(1, methodCost.getTotalGlobalCost());
    assertEquals(10, methodCost.getOverallCost());
  }

  public void testGadgetGetCountHasOneReturnOperation() throws Exception {
    MethodInfo getCount = repo.getClass(Gadget.class.getName()).getMethod("getCount()I");
    assertEquals(1, getCount.getOperations().size());
  }

  public void testGadgetTotalClassCosts() {
    // This class has no cost, because there are no global references or cyclomatic complexity.
    ClassCost classCost = decoratedComputer.compute(Gadget.class);
    assertEquals(0, classCost.getHighestMethodComplexityCost());
    assertEquals(0, classCost.getHighestMethodGlobalCost());
    assertEquals(0, classCost.getOverallCost());
    assertEquals(0, classCost.getTotalComplexityCost());
    assertEquals(0, classCost.getTotalGlobalCost());
  }

  public void testFinalGlobalTotalClassCosts() {
    // This class has static (global) state, but has no cost.
    ClassCost classCost = decoratedComputer.compute(FinalGlobal.class);
    assertEquals(0, classCost.getHighestMethodComplexityCost());
    assertEquals(0, classCost.getHighestMethodGlobalCost());
    assertEquals(0, classCost.getOverallCost());
    assertEquals(0, classCost.getTotalComplexityCost());
    assertEquals(0, classCost.getTotalGlobalCost());
    // Note to the reader: This is interesting. This class does the harm,
    // exposing the mutable global state. He himself is easy to test, though.
    // (Low cyclomatic complexity, and no external global state is accessed).
    // It is only when others start to use him does he become a problem.

    // To repeat, the cost of his global state will be seen in his users, not
    // in him.
  }

  public void testFinalGlobalExampleTotalClassCosts() {
    // This class has methods which access both mutable and non-mutable global state.
    ClassCost classCost = decoratedComputer.compute(FinalGlobalExample.class);
    assertEquals(0, classCost.getHighestMethodComplexityCost());
    assertEquals(1, classCost.getHighestMethodGlobalCost());
    assertEquals(10, classCost.getOverallCost());
    assertEquals(0, classCost.getTotalComplexityCost());

    /* There are two instance methods which access expensive (mutable) global state:
     * 1) Gadget#getGlobalCount()
     * 2) Gadget#globalIncrement()
     * Each has a global state cost of 1, so the class' total global cost is 2.
     *
     * Note that the other methods Gadget#getInstance() and Gadget#getGlobalId() do not have a
     * global state cost. Why? Because while they access global state, it is <em>final</em>
     * global state. This is non-mutable, and it does not count against you.
     */
    assertEquals(2, classCost.getTotalGlobalCost());
  }

}
