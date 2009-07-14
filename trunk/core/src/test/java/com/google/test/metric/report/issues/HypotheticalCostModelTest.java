// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CostUtil;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvocationCost;
import com.google.test.metric.MetricComputer;
import static com.google.test.metric.Reason.NON_OVERRIDABLE_METHOD_CALL;
import com.google.test.metric.SourceLocation;
import com.google.test.metric.testing.MetricComputerBuilder;

import junit.framework.TestCase;

import java.util.Arrays;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HypotheticalCostModelTest extends TestCase {
  public MethodCost doThingMethod;
  private MethodCost methodWithIndirectCosts;
  private ClassRepository repo = new JavaClassRepository();
  private CostModel costModel = new CostModel();
  private ClassMunger classMunger = new ClassMunger(repo);
  private HypotheticalCostModel hypotheticalCostModel = new HypotheticalCostModel(costModel, classMunger);
  private MetricComputer computer = new MetricComputerBuilder().withClassRepository(repo).build();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    doThingMethod = new MethodCost("doThing()", 1, false, false, false);
    doThingMethod.addCostSource(new CyclomaticCost(new SourceLocation(null, 3), Cost.cyclomatic(100)));

    methodWithIndirectCosts = new MethodCost("hasIndirect()", 2, false, false, false);
    methodWithIndirectCosts.addCostSource(new CyclomaticCost(new SourceLocation(null, 4), Cost.cyclomatic(50)));
    methodWithIndirectCosts.addCostSource(new MethodInvocationCost(new SourceLocation(null, 1),
        doThingMethod, NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(33)));
    costModel = new CostModel();
  }

  public void testDirectCostOfAMethodCanBeSubtractedFromClassCost() {
    ClassCost classCost = new ClassCost("com.google.Foo", Arrays.asList(doThingMethod));
    MethodCost methodCost = doThingMethod;
    assertEquals(1.0f, hypotheticalCostModel.computeContributionFromMethod(classCost, methodCost));
  }

  public void testContributionFromOneMethodIsCorrect() {
    ClassCost classCost = new ClassCost("com.google.Foo", Arrays.asList(doThingMethod, methodWithIndirectCosts));
    MethodCost methodCost = doThingMethod;
    float costWithoutDoThing = (0 + (50 + 33)) / 2;
    float costWithDoThing = (100 + (50 + 33)) / 2;
    float actual = hypotheticalCostModel.computeContributionFromMethod(classCost, methodCost);
    assertEquals(1 - costWithoutDoThing / costWithDoThing,
        actual);
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
    float actual = hypotheticalCostModel.computeContributionFromIssue(classCost, constructorCost,
        instanceCost4Invocation);
    // TODO
    //assertEquals(1 - costWithoutWorkInConstructor / costWithWorkInConstructor,
      //  actual);
  }
}
