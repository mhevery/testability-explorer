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
 * A cost model that can answer the hypothetical question "what would be the cost of this class
 * if I made a certain change to it?"
 *
 * The values returned are only rough approximations. Figuring out the real cost requires
 * munging the ClassInfo to produce the class with the hypothetical change, then re-running the
 * analysis, which is far too CPU intensive.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HypotheticalCostModel {

  private final CostModel costModel;

  @Inject
  public HypotheticalCostModel(CostModel costModel) {
    this.costModel = costModel;
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
    WeightedAverage average = costModel.createWeighedAverage();
    for (MethodCost methodCost : classCost.getMethods()) {
      Cost cost = (violationMethodCost == methodCost ? adjustedCost : methodCost.getTotalCost());
      average.addValue(costModel.computeOverall(cost));
    }
    return 1 - (int) average.getAverage() / (float) computeClass(classCost);
  }

  public float computeContributionFromMethod(ClassCost classCost, MethodCost violationMethodCost) {
    final float costWithoutIssue =
        computeClassWithoutMethod(classCost, violationMethodCost,
            violationMethodCost.getDependentCost());
    final float totalCost = (float) computeClass(classCost);
    return 1 - costWithoutIssue / totalCost;
  }  

  /**
   * Provided for passthrough to the delegate {@link CostModel}.
   */
  public int computeClass(ClassCost classCost) {
    return costModel.computeClass(classCost);
  }

}
