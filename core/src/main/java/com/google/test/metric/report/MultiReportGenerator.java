// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric.report;

import com.google.test.metric.ClassCost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A report that directs metric recording into several delegated reports.
 * @author alexeagle@google.com (Alex Eagle)
 *
 */
public class MultiReportGenerator implements ReportGenerator {

  private ReportGenerator summaryReport;
  private List<ReportGenerator> reports = new ArrayList<ReportGenerator>();

  public MultiReportGenerator() {}

  public MultiReportGenerator(ReportGenerator summaryReport) {
    this.summaryReport = summaryReport;
  }

  public MultiReportGenerator(ReportGenerator summaryReport, ReportGenerator... reports) {
    this(summaryReport);
    this.reports.addAll(Arrays.asList(reports));
  }

  /**
   * @see com.google.test.metric.report.ReportGenerator#addClassCost(com.google.test.metric.ClassCost)
   */
  public void addClassCost(ClassCost classCost) {
    for (ReportGenerator report : reports) {
      report.addClassCost(classCost);
    }
  }

  /**
   * @see com.google.test.metric.report.ReportGenerator#printFooter()
   */
  public void printFooter() throws IOException {
    for (ReportGenerator report : reports) {
      report.printFooter();
    }
  }

  /**
   * @see com.google.test.metric.report.ReportGenerator#printHeader()
   */
  public void printHeader() throws IOException {
    for (ReportGenerator report : reports) {
      report.printHeader();
    }
  }

  /**
   * Delegate to a SummaryReport, if we have one. The Report interface
   * doesn't provide this method.
   * @see SummaryReportModel#getOverall()
   */
  public int getOverall() {
    if (summaryReport != null) {
      // TODO(alexeagle)
      throw new UnsupportedOperationException("This still needs some refactoring to work...");
      //return summaryReport.getOverall();
    }
    return 0;
  }

  /**
   * One report may be the summary report.
   * @param summaryReport the summaryReport to set
   */
  public void setSummaryReport(ReportGenerator summaryReport) {
    this.summaryReport = summaryReport;
    add(summaryReport);
  }

  /**
   * @param report
   */
  public void add(ReportGenerator report) {
    reports.add(report);
  }

}
