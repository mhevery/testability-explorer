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

import static com.google.test.metric.report.GoogleChartAPI.GREEN;
import static com.google.test.metric.report.GoogleChartAPI.RED;
import static com.google.test.metric.report.GoogleChartAPI.YELLOW;
import com.google.test.metric.report.*;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import org.apache.commons.io.IOUtils;
import freemarker.template.*;

public class HtmlReport extends SummaryReport {

  private static final int MAX_HISTOGRAM_BINS = 200;
  private static final int HISTOGRAM_WIDTH = 700;
  private static final int HISTOGRAM_LEGEND_WIDTH = 130;
  protected final PrintStream out;
  private final DetailHtmlReport detailHtmlReport;
  private final String PREFIX = "com/google/test/metric/report/html/";

  public HtmlReport(PrintStream out, CostModel costModel, int maxExcellentCount,
      int maxAcceptableCost, int worstOffenderCount,
      DetailHtmlReport detailHtmlReport) {
    super(costModel, maxExcellentCount, maxAcceptableCost, worstOffenderCount);
    this.out = out;
    this.detailHtmlReport = detailHtmlReport;
  }

  public HtmlReport(PrintStream out, CostModel costModel, ReportOptions options,
                    DetailHtmlReport detailHtmlReport) {
    this(out, costModel, options.getMaxExcellentCost(), options.getMaxAcceptableCost(),
            options.getWorstOffenderCount(), detailHtmlReport);

  }

  public void printFooter() throws IOException {
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(new ClassPathTemplateLoader(PREFIX));

    Template template = cfg.getTemplate("Report.html");
    try {
      template.process(this, new PrintWriter(out));
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    }
  }

  public int getTotal() {
    return costs.size();
  }

  public String getCss() throws IOException {
    return IOUtils.toString(getClass().getResourceAsStream("te.css"));
  }

  public String getJSscript() throws IOException {
    return IOUtils.toString(getClass().getResourceAsStream("report.js"));
  }

  public String getHistogram() {
    int binCount = min(MAX_HISTOGRAM_BINS, 10 * (int) log(costs.size()) + 1);
    int binWidth = (int) ceil((double) worstCost / binCount);
    Histogram excellentHistogram = new Histogram(0, binWidth, binCount);
    Histogram goodHistogram = new Histogram(0, binWidth, binCount);
    Histogram needsWorkHistogram = new Histogram(0, binWidth, binCount);
    for (int overallCost : costs) {
      if (overallCost < maxExcellentCost) {
        excellentHistogram.value(overallCost);
      } else if (overallCost < maxAcceptableCost) {
        goodHistogram.value(overallCost);
      } else {
        needsWorkHistogram.value(overallCost);
      }
    }
    int maxBin = excellentHistogram.getMaxBin();
    maxBin = max(maxBin, goodHistogram.getMaxBin());
    maxBin = max(maxBin, needsWorkHistogram.getMaxBin());
    excellentHistogram.setMaxBin(maxBin);
    goodHistogram.setMaxBin(maxBin);
    needsWorkHistogram.setMaxBin(maxBin);
    HistogramChartUrl chart = new HistogramChartUrl();
    int[] excellent = excellentHistogram.getScaledBinRange(0, MAX_VALUE, 61);
    int[] good = goodHistogram.getScaledBinRange(0, MAX_VALUE, 61);
    int[] needsWork = needsWorkHistogram.getScaledBinRange(0, MAX_VALUE, 61);
    chart.setItemLabel(excellentHistogram.getBinLabels(20));
    chart.setValues(excellent, good, needsWork);
    chart.setYMark(0, excellentHistogram.getMaxBin());
    chart.setSize(HISTOGRAM_WIDTH, 200);
    chart.setBarWidth((HISTOGRAM_WIDTH - HISTOGRAM_LEGEND_WIDTH) / binCount, 0,
        0);
    chart.setChartLabel("Excellent", "Good", "Needs Work");
    chart.setColors(GREEN, YELLOW, RED);
    return chart.getHtml();
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

  public String getWorstOffenders() {
    if (detailHtmlReport == null) {
      return "No details available, check configuration";
    }
    StringBuilder out = new StringBuilder();
    out.append("<h2 style=\"margin-bottom: 12px;\">Least Testable Classes</h2>");
    out.append("<div onclick='clickHandler(event)'>");
    for (ClassCost cost : worstOffenders) {
      detailHtmlReport.write(cost);
    }
    out.append(detailHtmlReport.getOutput());
    out.append("</div>");
    return out.toString();
  }

  public void printHeader() throws IOException {
  }

  public String getNow() {
    Calendar cal = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return sdf.format(cal.getTime());
  }

}
