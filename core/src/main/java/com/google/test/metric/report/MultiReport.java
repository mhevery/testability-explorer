// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric.report;

import com.google.test.metric.ClassCost;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A report that directs metric recording into several delegated reports.
 * @author alexeagle@google.com (Alex Eagle)
 *
 */
public class MultiReport implements Report {

  private SummaryReport summaryReport;
  private List<Report> reports = new ArrayList<Report>();

  public MultiReport() {}

  public MultiReport(SummaryReport summaryReport) {
    this.summaryReport = summaryReport;
  }

  public MultiReport(SummaryReport summaryReport, Report... reports) {
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
  public void printFooter() {
    for (Report report : reports) {
      report.printFooter();
    }
  }

  /**
   * @see com.google.test.metric.report.Report#printHeader()
   */
  public void printHeader() {
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
      return summaryReport.getOverall();
    }
    return 0;
  }

  /**
   * One report may be the summary report.
   * @param summaryReport the summaryReport to set
   */
  public void setSummaryReport(SummaryReport summaryReport) {
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
