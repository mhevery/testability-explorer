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

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.report.*;
import com.google.test.metric.report.chart.*;
import static com.google.test.metric.report.chart.GoogleChartAPI.*;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;

import java.util.Date;
import java.util.List;
import java.io.*;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

/**
 * This model provides the data that backs the HTML report.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HtmlReport extends SummaryReport {
  private final IssuesReporter issuesReporter;

  public HtmlReport(CostModel costModel, IssuesReporter issuesReporter, ReportOptions options) {
    super(costModel, options.getMaxExcellentCost(), options.getMaxAcceptableCost(), options.getWorstOffenderCount());
    this.issuesReporter = issuesReporter;
  }

  public int getTotal() {
    return costs.size();
  }

  @Override
  public void addClassCost(ClassCost classCost) {
    super.addClassCost(classCost);
    issuesReporter.inspectClass(classCost);
  }

  public String getHistogram() {
    CostDistributionChart chart = new CostDistributionChart();
    chart.addValues(costs);

    ByteArrayOutputStream out = new ByteArrayOutputStream();
    chart.createChart(out);

    String dataUri = new String(encodeBase64(out.toByteArray()));
    return String.format("<img src=\"data:image/png;base64,%s\"/>", dataUri);
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
    return issuesReporter.getMostImportantIssues();
  }

  public Date getNow() {
    return new Date();
  }

}
