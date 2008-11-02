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

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;

public class PackageReport {

  private final String packageName;
  private final List<ClassCost> classCosts;
  private ArrayList<Integer> costs;
  private final GradeCategories grades;
  private final CostModel costModel;

  public PackageReport(String packageName, List<ClassCost> classCosts,
      GradeCategories grades, CostModel costModel) {
    this.packageName = packageName;
    this.classCosts = classCosts;
    this.grades = grades;
    this.costModel = costModel;
  }

  public String getPackageName() {
    return packageName;
  }

  public int getOverallCost() {
    return costModel.computeOverall(classCosts);
  }

  public String getOverallCostChart() {
    GoodnessChart chart = grades.createOverallChart(getOverallCost());
    chart.setSize(150, 50);
    return chart.getHtml();
  }

  public int getExcellentCount() {
    return grades.getExcellentCount(getPackageCosts());
  }

  public int getGoodCount() {
    return grades.getGoodCount(getPackageCosts());
  }

  public int getNeedsWorkCount() {
    return grades.getNeedsWorkCount(getPackageCosts());
  }

  public String getDistributionChart() {
    List<Integer> costs = getPackageCosts();
    PieChartUrl chart = grades.createDistributionChart(costs);
    chart.setSize(280, 50);
    return chart.getHtml();
  }

  public String getHistogramChart() {
    List<Integer> costs = getPackageCosts();
    return grades.createHistogram(400, 100, costs).getHtml();
  }

  public List<Integer> getPackageCosts() {
    if (costs == null) {
      costs = new ArrayList<Integer>();
      for (ClassCost classCost : classCosts) {
        costs.add(classCost.getOverallCost());
      }
    }
    return costs;
  }

  public List<ClassCost> getClassCosts() {
    return classCosts;
  }

}
