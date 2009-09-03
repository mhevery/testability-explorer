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
package com.google.test.metric.report.html;

import static com.google.test.metric.report.chart.GoogleChartAPI.GREEN;
import static com.google.test.metric.report.chart.GoogleChartAPI.RED;
import static com.google.test.metric.report.chart.GoogleChartAPI.YELLOW;

import java.util.Date;
import java.util.List;

import com.google.test.metric.AnalysisModel;
import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.report.GradeCategories;
import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.SummaryReportModel;
import com.google.test.metric.report.chart.GoodnessChart;
import com.google.test.metric.report.chart.HistogramChartUrl;
import com.google.test.metric.report.chart.PieChartUrl;
import com.google.test.metric.report.chart.Histogram.Logarithmic;
import com.google.test.metric.report.issues.ClassIssues;

/**
 * This model provides the data that backs the HTML report.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HtmlReportModel extends SummaryReportModel {
  private static final int HISTOGRAM_WIDTH = 700;
  private static final int HISTOGRAM_HEIGHT = 200;

  private final AnalysisModel analysisModel;

  public HtmlReportModel(CostModel costModel, AnalysisModel analysisModel, ReportOptions options) {
    super(costModel, options.getMaxExcellentCost(), options.getMaxAcceptableCost(), options.getWorstOffenderCount());
    this.analysisModel = analysisModel;
  }

  public int getTotal() {
    return costs.size();
  }

  @Override
  public void addClassCost(ClassCost classCost) {
    super.addClassCost(classCost);
    analysisModel.addClassCost(classCost);
  }

  public String getHistogram() {
    GradeCategories gradeCategories = new GradeCategories(maxExcellentCost, maxAcceptableCost);
    HistogramChartUrl histogramChartUrl =
        gradeCategories.createHistogram(HISTOGRAM_WIDTH, HISTOGRAM_HEIGHT, costs, new Logarithmic());
    return histogramChartUrl.getHtml();
  }

  public String getOverallChart() {
    GoodnessChart chart = new GoodnessChart(maxExcellentCost,
        maxAcceptableCost, 10 * maxAcceptableCost, 100 * maxAcceptableCost);
    chart.setSize(200, 100);
    chart.setUnscaledValues(getOverall());
    return chart.getHtml();
  }

  public String getPieChart() {
    PieChartUrl chart = new PieChartUrl();
    chart.setSize(400, 100);
    chart.setItemLabel("Excellent", "Good", "Needs Work");
    chart.setColors(GREEN, YELLOW, RED);
    chart.setValues(excellentCount, goodCount, needsWorkCount);
    return chart.getHtml();
  }
  
  public List<ClassIssues> getWorstOffenders() {
    return analysisModel.getWorstOffenders();
  }

  public Date getNow() {
    return new Date();
  }

}
