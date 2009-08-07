// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.inject.Inject;
import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.ViolationCost;
import com.google.test.metric.WeightedAverage;

/**
 * A cost model that can answer the hypothetical question "what would be the cost of this class
 * if I made a certain change to it?"
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HypotheticalCostModel {

  private final CostModel costModel;
  private final ClassMunger classMunger;
  private final MetricComputer computer;

  @Inject
  public HypotheticalCostModel(CostModel costModel, ClassMunger classMunger,
                               MetricComputer computer) {
    this.costModel = costModel;
    this.classMunger = classMunger;
    this.computer = computer;
  }

  public float computeContributionFromMethodInvocation(ClassCost classCost, MethodCost caller,
                                            MethodCost invoked) {
    ClassInfo withoutInvocation =
        classMunger.getClassWithoutInvocation(classCost.getClassName(), caller.getMethodName(),
            invoked.getClassName(), invoked.getMethodName());
    return contribution(classCost, withoutInvocation);
  }

  public float computeContributionFromMethodDirectCost(ClassCost classCost, MethodCost violationMethodCost) {
    return contribution(classCost,
        classMunger.getClassWithZeroDirectCostInMethod(classCost.getClassName(),
        violationMethodCost.getMethodName()));
  }



  public float computeContributionFromIssue(ClassCost classCost, MethodCost violationMethodCost,
                                            ViolationCost violationCost) {
    Cost adjustedCost = violationMethodCost.getTotalCost().add(violationCost.getCost().negate());
    WeightedAverage average = costModel.createWeighedAverage();
    for (MethodCost methodCost : classCost.getMethods()) {
      Cost cost = (violationMethodCost == methodCost ? adjustedCost : methodCost.getTotalCost());
      average.addValue(costModel.computeOverall(cost));
    }
    return 1 - (int) average.getAverage() / (float) computeClass(classCost);
  }

  /**
   * The overall contribution from an entire method. What would happen if this method did nothing?
   */
  public float computeContributionFromMethod(ClassCost classCost, MethodCost violationMethodCost) {
    return contribution(classCost,
        classMunger.getClassWithoutMethod(classCost.getClassName(),
        violationMethodCost.getMethodName()));
  }

  /**
   * The ratio of the cost of the class if the hypothetical change were made, to the
   * cost of the class with no changes
   * @param originalCost the cost originally assigned to the class
   * @param hypotheticalClass the changed class
   * @return the ratio
   */
  private float contribution(ClassCost originalCost, ClassInfo hypotheticalClass) {
    ClassCost costWithoutMethod = computer.compute(hypotheticalClass);
    return 1 - computeClass(costWithoutMethod) / (float) computeClass(originalCost);
  }

  /**
   * Provided for passthrough to the delegate {@link CostModel}.
   */
  public int computeClass(ClassCost classCost) {
    return costModel.computeClass(classCost);
  }

}
