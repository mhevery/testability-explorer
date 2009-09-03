// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report;

import com.google.test.metric.report.chart.Histogram;

/**
 * @author alexeagle@google.com (Alex Eagle)
*/
public class MultiHistogramDataModel {

  private final Histogram excellent;
  private final Histogram good;
  private final Histogram needsWork;
  private final Histogram overallHistogram;
  private final int binCount;
  private final int binWidth;

  public MultiHistogramDataModel(Histogram excellent, Histogram good, Histogram needsWork,
                                 Histogram overallHistogram, int binCount, int binWidth) {
    this.excellent = excellent;
    this.good = good;
    this.needsWork = needsWork;
    this.overallHistogram = overallHistogram;
    this.binCount = binCount;
    this.binWidth = binWidth;
  }

  public int getBinCount() {
    return binCount;
  }

  public Histogram getExcellent() {
    return excellent;
  }

  public Histogram getGood() {
    return good;
  }

  public Histogram getNeedsWork() {
    return needsWork;
  }

  public int getBinWidth() {
    return binWidth;
  }

  public Histogram getOverallHistogram() {
    return overallHistogram;
  }
}
