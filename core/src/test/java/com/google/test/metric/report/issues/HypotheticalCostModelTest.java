// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.CostModel;
import com.google.test.metric.CostUtil;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvocationCost;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.testing.MetricComputerBuilder;

import junit.framework.TestCase;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HypotheticalCostModelTest extends TestCase {
  private CostModel costModel = new CostModel();
  private ClassRepository repo = new JavaClassRepository();
  private MetricComputer computer = new MetricComputerBuilder().withClassRepository(repo).build();
  private HypotheticalCostModel hypotheticalCostModel =
      new HypotheticalCostModel(costModel, new ClassMunger(repo), computer);

  static class HasDirectCost {
   void doThing() {
      new CostUtil().instanceCost4();
    }
  }
  public void testDirectCostOfAMethodCanBeSubtractedFromClassCost() {
    ClassCost classCost = computer.compute(HasDirectCost.class.getCanonicalName());
    MethodCost methodCost = classCost.getMethodCost("void doThing()");
    assertEquals(1.0f, hypotheticalCostModel.computeContributionFromMethod(classCost, methodCost));
  }

  static class HasCost {
    void doThing() {
      new CostUtil().instanceCost4();
      new CostUtil().instanceCost4();
    }

    void hasIndirect() {
      new CostUtil().instanceCost2();
      new CostUtil().instanceCost2();
      new OtherClass().method();
    }

    private class OtherClass {
      void method() {
        new CostUtil().instanceCost1();
        new CostUtil().instanceCost1();
      }
    }
  }

  public void testContributionFromOneMethodIsCorrect() {
    float costWithoutDoThing = (0 + (4 + 2)) / 3;
    float costWithDoThing = (8 + (4 + 2)) / 3;
    ClassCost classCost = computer.compute(HasCost.class.getCanonicalName());
    MethodCost methodCost = classCost.getMethodCost("void doThing()");
    float actual = hypotheticalCostModel.computeContributionFromMethod(classCost, methodCost);
    assertEquals(1 - costWithoutDoThing / costWithDoThing, actual);
  }

  private static class Example {
    public Example() {
      new CostUtil().instanceCost4();
    }

    public int doThing() {
      new CostUtil().instanceCost3();
      return 1;
    }
  }

  public void testHypotheticalModelGivesTheSameNumberWithNoOverrides() throws Exception {
    ClassInfo aClass = repo.getClass(Example.class.getCanonicalName());
    ClassCost cost = computer.compute(aClass);
    int originalCost = costModel.computeClass(cost);
    int newCost = hypotheticalCostModel.computeClass(cost);
    assertEquals(originalCost, newCost);
  }

  public void testMethodCostGoesDownWhenADependentCostIsRemoved() throws Exception {
    ClassInfo aClass = repo.getClass(Example.class.getCanonicalName());
    ClassCost classCost = computer.compute(aClass);
    float costWithWorkInConstructor = (4 + 7) / 2;
    float costWithoutWorkInConstructor = (0 + 3) / 2;
    MethodCost constructorCost = classCost.getMethodCost("Example()");
    MethodInvocationCost instanceCost4Invocation =
        (MethodInvocationCost) constructorCost.getViolationCosts().get(0);
    float actual = hypotheticalCostModel.computeContributionFromMethodInvocation(classCost, constructorCost,
        instanceCost4Invocation.getMethodCost());
    // TODO
    //assertEquals(1 - costWithoutWorkInConstructor / costWithWorkInConstructor, actual);
  }

  static class SharedNonInjectableVariable {
    private CostUtil costUtil;

    public SharedNonInjectableVariable() {
      costUtil = new CostUtil();
      costUtil.instanceCost4();
    }

    public int doThing() {
      costUtil.instanceCost3();
      return 1;
    }
  }

  public void testFixingThisConstructorRemovesAllCosts() throws Exception {
    ClassInfo aClass = repo.getClass(SharedNonInjectableVariable.class.getCanonicalName());
    ClassCost cost = computer.compute(aClass);
    // TODO: the costUtil variable should be marked as possibly injectable if we fix the constructor
//    assertEquals(1f, hypotheticalCostModel.computeContributionFromMethod(cost,
//        cost.getMethodCost("SharedNonInjectableVariable()")));
  }
}
