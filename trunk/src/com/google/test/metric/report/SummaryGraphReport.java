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

import com.google.test.metric.WeightedAverage;

public abstract class SummaryGraphReport<T extends SummaryGraphReport.Unit> {

  public static class Unit {
    private final int cost;
    private final String name;

    public Unit(String name, int cost) {
      this.name = name;
      this.cost = cost;
    }

    public int getCost() {
      return cost;
    }

    public String getName() {
      return name;
    }
  }

  private final GradeCategories grades;
  private final ArrayList<Integer> costs = new ArrayList<Integer>();
  private final ArrayList<Unit> unitCosts = new ArrayList<Unit>();
  private final WeightedAverage average;
  private final String name;


  public SummaryGraphReport(String name, GradeCategories grades, WeightedAverage average) {
    this.name = name;
    this.grades = grades;
    this.average = average;
  }

  public String getName() {
    return name;
  }

  public void addUnit(T unit) {
    int cost = unit.getCost();
    unitCosts.add(unit);
    costs.add(cost);
    average.addValue(cost);
  }

  public ArrayList<Unit> getUnitCosts() {
    return unitCosts;
  }

  public int getOverallCost() {
    return (int) average.getAverage();
  }

  public int getCount() {
    return unitCosts.size();
  }

  public double getExcellentPercent() {
    return getCount() == 0 ? 0 : getExcellentCount() / getCount();
  }

  public double getGoodPercent() {
    return getCount() == 0 ? 0 : getExcellentCount() / getCount();
  }

  public double getNeedsWorkPercent() {
    return getCount() == 0 ? 0 : getExcellentCount() / getCount();
  }

  public int getExcellentCount() {
    return grades.getExcellentCount(costs);
  }

  public int getGoodCount() {
    return grades.getGoodCount(costs);
  }

  public int getNeedsWorkCount() {
    return grades.getNeedsWorkCount(costs);
  }

  public String getOverallCostChart() {
    GoodnessChart chart = grades.createOverallChart(getOverallCost());
    chart.setSize(150, 50);
    return chart.getHtml();
  }

  public String getDistributionChart() {
    PieChartUrl chart = grades.createDistributionChart(costs);
    chart.setSize(280, 50);
    return chart.getHtml();
  }

  public String getHistogramChart() {
    return grades.createHistogram(500, 100, costs).getHtml();
  }

  public SummaryGraphReport<T> getSelf() {
    return this;
  }

}