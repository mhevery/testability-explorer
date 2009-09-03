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
package com.google.test.metric.report;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.WeightedAverage;

public abstract class SummaryReportModel extends ReportModel {

  protected final WeightedAverage weightedAverage = new WeightedAverage();
  protected SortedSet<ClassCost> worstOffenders;
  protected final List<Integer> costs = new ArrayList<Integer>();

  public int getExcellentCount() {
    return excellentCount;
  }

  public int getGoodCount() {
    return goodCount;
  }

  public int getNeedsWorkCount() {
    return needsWorkCount;
  }

  protected final int maxExcellentCost;
  protected final int maxAcceptableCost;
  protected final int worstOffenderCount;
  protected int excellentCount = 0;
  protected int goodCount = 0;
  protected int needsWorkCount = 0;
  protected int worstCost = 1;
  private final CostModel costModel;

  public SummaryReportModel(CostModel costModel, int maxExcellentCost, int maxAcceptableCost, int worstOffenderCount) {
    this.costModel = costModel;
    this.worstOffenders = new TreeSet<ClassCost>(new ClassCost.CostComparator(costModel));
    this.maxExcellentCost = maxExcellentCost;
    this.maxAcceptableCost = maxAcceptableCost;
    this.worstOffenderCount = worstOffenderCount;
  }

  @Override
  public void addClassCost(ClassCost classCost) {
    int cost = costModel.computeClass(classCost);
    if (cost < maxExcellentCost) {
      excellentCount++;
    } else if (cost < maxAcceptableCost) {
      goodCount++;
    } else {
      needsWorkCount++;
    }
    costs.add(cost);
    worstOffenders.add(classCost);
    if (worstOffenders.size() > worstOffenderCount) {
      worstOffenders.remove(worstOffenders.last());
    }
    worstCost = Math.max(worstCost, cost);
    weightedAverage.addValue(cost);
  }

  public int getClassCount() {
    return costs.size();
  }

  public int getOverall() {
    return (int) weightedAverage.getAverage();
  }
}