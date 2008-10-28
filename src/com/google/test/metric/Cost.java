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

public class Cost {

  private int cyclomaticCost;
  private int globalCost;
  private int[] lodDistribution;
  private int overall;

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

  public void add(Cost cost) {
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
    return cyclomaticCost + ", " + globalCost;
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

  public int getOvarall() {
    return overall;
  }

  public void link(CostModel costModel) {
    overall = costModel.computeMethod(this);
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
    result = prime * result + overall;
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
    if (overall != other.overall) {
      return false;
    }
    return true;
  }

}
