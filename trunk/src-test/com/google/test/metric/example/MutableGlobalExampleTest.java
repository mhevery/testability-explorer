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
import com.google.test.metric.MetricComputer;
import com.google.test.metric.example.FinalGlobalExample.Gadget;
import com.google.test.metric.example.MutableGlobalExample.MutableGlobal;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

/**
 * @see FinalGlobalExampleTest FinalGlobalExampleTest for contrasting examples that access
 * non-mutable global state.
 *
 * @author Jonathan Wolter
 */
public class MutableGlobalExampleTest extends AutoFieldClearTestCase {

  private final ClassRepository repo = new JavaClassRepository();
  private MetricComputerJavaDecorator decoratedComputer;

  @Override
  protected void setUp() throws Exception {
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
  }

  public void testAccessingMutableStaticItselfDirectlyDoesntCountAgainstYou() throws Exception {
    MethodCost methodCost = decoratedComputer.compute(MutableGlobalExample.class,
        "getInstance()Lcom/google/test/metric/example/MutableGlobalExample$Gadget;");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());

    // Noteworthy: code which exposes global state to others does not have the cost itself.
    // TODO(jwolter): is this correct?
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getTotalCost().getGlobalCost());
    assertEquals(0, methodCost.getOverallCost());
  }

  public void testAccessingAFinalFieldDoesNotCountAgainstYouButInitialGlobalDoes() throws Exception {
    // This method goes into mutable global state (cost +1) and reads a final value (cost +0)
    MethodCost methodCost = decoratedComputer.compute(MutableGlobalExample.class,
        "getGlobalId()Ljava/lang/String;");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getCyclomaticComplexityCost());
    // Total Global Cost of 1, because of the {@code mutableInstance}.
    assertEquals(1, methodCost.getTotalCost().getGlobalCost());
    assertEquals(10, methodCost.getOverallCost());
  }

  public void testAccessingANonFinalFieldCountsAgainstYou() throws Exception {
    // This method goes into mutable global state (cost +1) and reads a mutable value (cost +1)
    MethodCost methodCost = decoratedComputer.compute(MutableGlobalExample.class, "getGlobalCount()I");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(2, methodCost.getTotalCost().getGlobalCost());
    assertEquals(20, methodCost.getOverallCost());
  }

  public void testWritingANonFinalFieldCountsAgainstYou() throws Exception {
    // This method goes into mutable global state (cost +1) and changes a mutable value (cost +1)
    MethodCost methodCost = decoratedComputer.compute(MutableGlobalExample.class, "globalIncrement()I");
    assertEquals(0, methodCost.getCost().getCyclomaticComplexityCost());
    assertEquals(0, methodCost.getCost().getGlobalCost());
    assertEquals(0, methodCost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(2, methodCost.getTotalCost().getGlobalCost());
    assertEquals(20, methodCost.getOverallCost());
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

  public void testMutableGlobalTotalClassCosts() {
    // This class has a static mutable instance that exposes global state.
    // Contrast this with {@code FinalGlobalExampleTest#testFinalGlobalTotalClassCosts()}
    ClassCost classCost = decoratedComputer.compute(MutableGlobal.class);
    assertEquals(0, classCost.getHighestMethodComplexityCost());
    assertEquals(1, classCost.getHighestMethodGlobalCost());
    assertEquals(10, classCost.getOverallCost());
    assertEquals(0, classCost.getTotalComplexityCost());
    assertEquals(2, classCost.getTotalGlobalCost());
  }

  public void testMutableGlobalExampleTotalClassCosts() {
    // This class uses a class which has a static mutable instance.
    ClassCost classCost = decoratedComputer.compute(MutableGlobalExample.class);
    assertEquals(0, classCost.getHighestMethodComplexityCost());
    assertEquals(2, classCost.getHighestMethodGlobalCost());
    assertEquals(18, classCost.getOverallCost());
    assertEquals(0, classCost.getTotalComplexityCost());

    /* There are three instance methods which access expensive (mutable) global state:
     * 1) Gadget#getGlobalId() - cost of 1
     * 2) Gadget#getGlobalCount() - cost of 2
     * 3) Gadget#globalIncrement() - cost of 2
     * The class' total global cost is 5.
     *
     * Note that the only other method is the constructor, which has zero global state cost, and
     * zero complexity cost.
     */
    assertEquals(5, classCost.getTotalGlobalCost());
  }

}
