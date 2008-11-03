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
  protected final ArrayList<Unit> unitCosts = new ArrayList<Unit>();
  private int overallCost;
  private final String name;

  public SummaryGraphReport(String name, GradeCategories grades) {
    this.name = name;
    this.grades = grades;
  }

  public String getName() {
    return name;
  }

  public void addUnit(T unit) {
    unitCosts.add(unit);
    costs.add(unit.getCost());
  }

  public ArrayList<Unit> getUnitCosts() {
    return unitCosts;
  }

  public int getOverallCost(){
    return overallCost;
  }

  public void setOverallCost(int overallCost) {
    this.overallCost = overallCost;
  }

  public int getCount() {
    return unitCosts.size();
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
    return grades.createHistogram(400, 100, costs).getHtml();
  }

}