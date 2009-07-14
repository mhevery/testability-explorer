// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.inject.Inject;
import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.ViolationCost;
import com.google.test.metric.WeightedAverage;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HypotheticalCostModel {

  private final CostModel costModel;
  private final ClassMunger classMunger;

  @Inject
  public HypotheticalCostModel(CostModel costModel, ClassMunger classMunger) {
    this.costModel = costModel;
    this.classMunger = classMunger;
  }

  int computeClass(ClassCost classCost) {
    return costModel.computeClass(classCost);
  }
  private int computeClassWithoutMethod(ClassCost classCost, MethodCost adjustedMethod,
                                        Cost replacementCost) {

    WeightedAverage average = costModel.createWeighedAverage();
    for (MethodCost methodCost : classCost.getMethods()) {
      Cost cost = (adjustedMethod == methodCost ? replacementCost : methodCost.getTotalCost());
      average.addValue(costModel.computeOverall(cost));
    }
    return (int) average.getAverage();
  }


  public float computeContributionFromIssue(ClassCost classCost, MethodCost violationMethodCost,
                                            ViolationCost violationCost) {
    Cost adjustedCost = violationMethodCost.getTotalCost().add(violationCost.getCost().negate());
    return 1 - computeClassWithoutMethod(classCost, violationMethodCost, adjustedCost) /
               (float)computeClass(classCost);
  }

  public float computeContributionFromMethod(ClassCost classCost, MethodCost violationMethodCost) {
    // TODO(alexeagle): wire in the classMunger like so:
    // computer.compute(classMunger.getClassWithoutMethod(classCost.getClassName(), violationMethodCost.getMethodName()));
    final float costWithoutIssue =
        computeClassWithoutMethod(classCost, violationMethodCost,
            violationMethodCost.getDependentCost());
    final float totalCost = (float) computeClass(classCost);
    return 1 - costWithoutIssue / totalCost;
  }

}
