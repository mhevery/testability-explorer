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

import java.util.*;

import com.google.test.metric.ClassCost;

public abstract class SummaryReport implements Report {

  private static final double WEIGHT = 0.3;
  protected SortedSet<ClassCost> worstOffenders;
  protected final List<Integer> costs = new ArrayList<Integer>();
  protected final int maxExcellentCost;
  protected final int maxAcceptableCost;
  protected final int worstOffenderCount;
  protected int excellentCount = 0;
  protected int goodCount = 0;
  protected int needsWorkCount = 0;
  protected int worstCost = 1;
  protected double overallSum = 0;
  protected double overallSqr = 0;

  public SummaryReport(int maxExcellentCost, int maxAcceptableCost, int worstOffenderCount) {
    this.worstOffenders = new TreeSet<ClassCost>(new ClassCost.CostComparator());
    this.maxExcellentCost = maxExcellentCost;
    this.maxAcceptableCost = maxAcceptableCost;
    this.worstOffenderCount = worstOffenderCount;
  }

  public SummaryReport(int maxExcellentCost, int maxAcceptableCost, int worstOffenderCount,
                       Comparator comparator) {
    this(maxExcellentCost, maxAcceptableCost, worstOffenderCount);
    this.worstOffenders = new TreeSet<ClassCost>(comparator);
  }

  public void addClassCost(ClassCost classCost) {
    int cost = (int) classCost.getOverallCost();
    if (cost < maxExcellentCost) {
      excellentCount++;
    } else if (cost < maxAcceptableCost) {
      goodCount++;
    } else {
      needsWorkCount++;
    }
    costs.add((int)classCost.getOverallCost());
    worstOffenders.add(classCost);
    if (worstOffenders.size() > worstOffenderCount) {
      worstOffenders.remove(worstOffenders.last());
    }
    overallSqr += Math.pow(cost, WEIGHT + 1);
    overallSum += Math.pow(cost, WEIGHT);
    worstCost = Math.max(worstCost, cost);
  }

  public int getClassCount() {
    return costs.size();
  }

  public int getOverall() {
    return (int) (overallSqr / overallSum);
  }

}