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

import java.util.List;

public class ClassCost {

  public static class Comparator implements java.util.Comparator<ClassCost> {
    public int compare(ClassCost c1, ClassCost c2) {
      int diff = (int) (c2.getOverallCost() - c1.getOverallCost());
      return diff == 0 ? c1.className.compareTo(c2.className) : diff;
    }
  }

  private final List<MethodCost> methods;
  private final String className;
  private long overallCost;

  public ClassCost(String className, List<MethodCost> methods) {
    this.className = className;
    this.methods = methods;
  }

  public MethodCost getMethodCost(String methodName) {
    for (MethodCost cost : methods) {
      if (cost.getMethodName().equals(className + "." + methodName)) {
        return cost;
      }
    }
    throw new IllegalArgumentException("Method '" + methodName
        + "' does not exist.");
  }

  @Override
  public String toString() {
    return className + " " + getOverallCost();
  }

  public String getClassName() {
    return className;
  }

  public List<MethodCost> getMethods() {
    return methods;
  }

  public long getTotalComplexityCost() {
    long totalCost = 0;
    for (MethodCost methodCost : getMethods()) {
      totalCost += methodCost.getTotalComplexityCost();
    }
    return totalCost;
  }

  public long getHighestMethodComplexityCost() {
    long cost = 0;
    for (MethodCost methodCost : getMethods()) {
      if (methodCost.getTotalComplexityCost() > cost) {
        cost = methodCost.getTotalComplexityCost();
      }
    }
    return cost;
  }

  public long getTotalGlobalCost() {
    long totalCost = 0;
    for (MethodCost methodCost : getMethods()) {
      totalCost += methodCost.getTotalGlobalCost();
    }
    return totalCost;
  }

  public long getHighestMethodGlobalCost() {
    long cost = 0;
    for (MethodCost methodCost : getMethods()) {
      if (methodCost.getTotalGlobalCost() > cost) {
        cost = methodCost.getTotalGlobalCost();
      }
    }
    return cost;
  }

  public void link(CostModel costModel) {
    for (MethodCost methodCost : methods) {
      methodCost.link(costModel);
    }
    overallCost = costModel.computeClass(methods);
  }

  public long getOverallCost() {
    return overallCost;
  }

}
