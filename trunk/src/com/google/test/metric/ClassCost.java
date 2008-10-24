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

  public static class CostComparator implements java.util.Comparator<ClassCost> {
    public int compare(ClassCost c1, ClassCost c2) {
      int diff = (int) (c2.getOverallCost() - c1.getOverallCost());
      return diff == 0 ? c1.className.compareTo(c2.className) : diff;
    }
  }

  public static class PackageComparator implements java.util.Comparator<ClassCost> {
    public int compare(ClassCost c1, ClassCost c2) {
      // we sort by fully qualified path to get packages in right order
      return c1.getClassName().compareTo(c2.getClassName());
    }
  }

  private final List<MethodCost> methods;
  private final String className;
  private final long overallCost;

  public ClassCost(String className, List<MethodCost> methods, CostModel costModel) {
    this.className = className;
    this.methods = methods;
    this.overallCost = costModel.computeClass(methods);
  }

  public MethodCost getMethodCost(String methodName) {
    for (MethodCost cost : methods) {
      if (cost.getMethodName().equals(methodName)) {
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

  public String getPackageName() {

    return getClassName().lastIndexOf('.') == -1 ? "" : getClassName().substring(0, getClassName().lastIndexOf('.'));
  }

  public List<MethodCost> getMethods() {
    return methods;
  }

  public long getTotalComplexityCost() {
    long totalCost = 0;
    for (MethodCost methodCost : getMethods()) {
      totalCost += methodCost.getTotalCost().getCyclomaticComplexityCost();
    }
    return totalCost;
  }

  public long getHighestMethodComplexityCost() {
    long cost = 0;
    for (MethodCost methodCost : getMethods()) {
      if (methodCost.getTotalCost().getCyclomaticComplexityCost() > cost) {
        cost = methodCost.getTotalCost().getCyclomaticComplexityCost();
      }
    }
    return cost;
  }

  public long getTotalGlobalCost() {
    long totalCost = 0;
    for (MethodCost methodCost : getMethods()) {
      totalCost += methodCost.getTotalCost().getGlobalCost();
    }
    return totalCost;
  }

  public long getHighestMethodGlobalCost() {
    long cost = 0;
    for (MethodCost methodCost : getMethods()) {
      if (methodCost.getTotalCost().getGlobalCost() > cost) {
        cost = methodCost.getTotalCost().getGlobalCost();
      }
    }
    return cost;
  }

  public long getOverallCost() {
    return overallCost;
  }

}
