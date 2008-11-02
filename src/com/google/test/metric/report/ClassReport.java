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
import com.google.test.metric.MethodCost;

public class ClassReport {

  private final Source source;
  private final ClassCost classCost;
  private final GradeCategories grades;
  private ArrayList<Integer> costs;

  public ClassReport(Source source, ClassCost classCost, GradeCategories grades) {
    this.source = source;
    this.classCost = classCost;
    this.grades = grades;
  }

  public Source getSource() {
    return source;
  }

  public ClassCost getClassCost() {
    return classCost;
  }

  public int getOverallCost() {
    return classCost.getOverallCost();
  }

  public String getOverallCostChart() {
    GoodnessChart chart = grades.createOverallChart(getOverallCost());
    chart.setSize(150, 50);
    return chart.getHtml();
  }

  public int getExcelentCount() {
    return grades.getExcelentCount(getMethodCosts());
  }

  public int getGoodCount() {
    return grades.getGoodCount(getMethodCosts());
  }

  public int getNeedsWorkCount() {
    return grades.getNeedsWorkCount(getMethodCosts());
  }

  public String getDistributionChart() {
    List<Integer> costs = getMethodCosts();
    PieChartUrl chart = grades.createDistributionChart(costs);
    chart.setSize(280, 50);
    return chart.getHtml();
  }

  public String getHistogramChart() {
    List<Integer> costs = getMethodCosts();
    return grades.createHistogram(400, 100, costs).getHtml();
  }

  public List<Integer> getMethodCosts() {
    if (costs == null) {
      costs = new ArrayList<Integer>();
      for (MethodCost methodCost : classCost.getMethods()) {
        costs.add(methodCost.getOverallCost());
      }
    }
    return costs;
  }
}
