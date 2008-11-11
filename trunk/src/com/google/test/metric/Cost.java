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
package com.google.test.metric;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Cost {

  private static final String COMPLEXITY_COST_HELP_URL = "http://code.google.com/p/testability-explorer/wiki/ComplexityCostExplanation";
  private static final String GLOBAL_COST_HELP_URL = "http://code.google.com/p/testability-explorer/wiki/GlobalCostExplanation";
  private static final String LAW_OF_DEMETER_COST_HELP_URL = "http://code.google.com/p/testability-explorer/wiki/LawOfDemeterCostExplanation";
  private int cyclomaticCost;
  private int globalCost;
  private int[] lodDistribution;

  private Cost(int cyclomaticCost, int globalCost, int[] lodDistribution) {
    this.cyclomaticCost = cyclomaticCost;
    this.globalCost = globalCost;
    this.lodDistribution = lodDistribution;
  }

  public static Cost none() {
    return new Cost(0, 0, new int[0]);
  }

  public static Cost global(int count) {
    return new Cost(0, count, new int[0]);
  }

  public static Cost lod(int distance) {
    int[] distribution = new int[distance + 1];
    distribution[distance] = 1;
    return new Cost(0, 0, distribution);
  }

  public static Cost cyclomatic(int cyclomaticCost) {
    return new Cost(cyclomaticCost, 0, new int[0]);
  }

  public static Object lodDistribution(int... counts) {
    return new Cost(0, 0, counts);
  }

  public Cost add(Cost cost) {
    cyclomaticCost += cost.cyclomaticCost;
    globalCost += cost.globalCost;
    int[] other = cost.lodDistribution;
    int size = Math.max(lodDistribution.length, other.length);
    int[] old = lodDistribution;
    if (lodDistribution.length < size) {
      lodDistribution = new int[size];
    }
    for (int i = 0; i < size; i++) {
      int count1 = i < old.length ? old[i] : 0;
      int count2 = i < other.length ? other[i] : 0;
      lodDistribution[i] = count1 + count2;
    }
    return this;
  }

  public void addDependant(Cost cost) {
    cyclomaticCost += cost.cyclomaticCost;
    globalCost += cost.globalCost;
  }

  public int getCyclomaticComplexityCost() {
    return cyclomaticCost;
  }

  public int getGlobalCost() {
    return globalCost;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    String sep = "";
    if (cyclomaticCost > 0) {
      builder.append(sep);
      builder.append("CC: " + cyclomaticCost);
      sep = ", ";
    }
    if (globalCost > 0) {
      builder.append(sep);
      builder.append("GC: " + globalCost);
      sep = ", ";
    }
    int loDSum = getLoDSum();
    if (loDSum > 0) {
      builder.append(sep);
      builder.append("LOD: " + loDSum);
      sep = ", ";
    }
    return builder.toString();
  }

  // TODO(jwolter): Refactor me so the html presentation representation is outside the Cost class.
  // Probably this means configure the urls in the template, and take all the cost types directly
  // in there. Build this string in the templating side of things, rather than in the core Cost
  // side of things here. But more important first is to write up useful html pages explaining the
  // costs.
  public String toHtmlReportString() {
    StringBuilder builder = new StringBuilder();
    String sep = "";
    if (cyclomaticCost > 0) {
      builder.append(sep);
      builder.append("<a href=\"" + COMPLEXITY_COST_HELP_URL + "\">CC: " + cyclomaticCost + "</a>");
      sep = ", ";
    }
    if (globalCost > 0) {
      builder.append(sep);
      builder.append("<a href=\"" + GLOBAL_COST_HELP_URL + "\">GC: " + globalCost + "</a>");
      sep = ", ";
    }
    int loDSum = getLoDSum();
    if (loDSum > 0) {
      builder.append(sep);
      builder.append("<a href=\"" + LAW_OF_DEMETER_COST_HELP_URL + "\">LOD: " + loDSum + "</a>");
      sep = ", ";
    }
    return builder.toString();
  }

  public int getLoDSum() {
    int sum = 0;
    for (int value : lodDistribution) {
      sum += value;
    }
    return sum;
  }

  public Cost copy() {
    return new Cost(cyclomaticCost, globalCost, lodDistribution);
  }

  public Cost copyNoLOD() {
    return new Cost(cyclomaticCost, globalCost, new int[0]);
  }


  public int[] getLoDDistribution() {
    return lodDistribution;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + cyclomaticCost;
    result = prime * result + globalCost;
    result = prime * result + Arrays.hashCode(lodDistribution);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Cost other = (Cost) obj;
    if (cyclomaticCost != other.cyclomaticCost) {
      return false;
    }
    if (globalCost != other.globalCost) {
      return false;
    }
    if (!Arrays.equals(lodDistribution, other.lodDistribution)) {
      return false;
    }
    return true;
  }

  Map<String, Object> getAttributes() {
    Map<String, Object> atts = new HashMap<String, Object>();
    atts.put("cyclomatic", getCyclomaticComplexityCost());
    atts.put("global", getGlobalCost());
    atts.put("lod", getLoDSum());
    return atts;
  }

  public static Cost create(int overall, int cyclomatic, int global, int lod) {
    return new Cost(cyclomatic, global, new int[] { lod });
  }

}
