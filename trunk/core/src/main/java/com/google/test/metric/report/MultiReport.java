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
public class MultiReport implements Report {

  private Report summaryReport;
  private List<Report> reports = new ArrayList<Report>();

  public MultiReport() {}

  public MultiReport(Report summaryReport) {
    this.summaryReport = summaryReport;
  }

  public MultiReport(Report summaryReport, Report... reports) {
    this(summaryReport);
    this.reports.addAll(Arrays.asList(reports));
  }

  /**
   * @see com.google.test.metric.report.Report#addClassCost(com.google.test.metric.ClassCost)
   */
  public void addClassCost(ClassCost classCost) {
    for (Report report : reports) {
      report.addClassCost(classCost);
    }
  }

  /**
   * @see com.google.test.metric.report.Report#printFooter()
   */
  public void printFooter() throws IOException {
    for (Report report : reports) {
      report.printFooter();
    }
  }

  /**
   * @see com.google.test.metric.report.Report#printHeader()
   */
  public void printHeader() throws IOException {
    for (Report report : reports) {
      report.printHeader();
    }
  }

  /**
   * Delegate to a SummaryReport, if we have one. The Report interface
   * doesn't provide this method.
   * @see SummaryReport#getOverall()
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
  public void setSummaryReport(Report summaryReport) {
    this.summaryReport = summaryReport;
    add(summaryReport);
  }

  /**
   * @param report
   */
  public void add(Report report) {
    reports.add(report);
  }

}
