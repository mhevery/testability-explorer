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

import static com.google.test.metric.report.GoogleChartAPI.GREEN;
import static com.google.test.metric.report.GoogleChartAPI.RED;
import static com.google.test.metric.report.GoogleChartAPI.YELLOW;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Math.ceil;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.List;

public class GradeCategories {
  private static final int MAX_HISTOGRAM_BINS = 100;
  private static final int HISTOGRAM_LEGEND_WIDTH = 130;
  private final int maxExcellentCost;
  private final int maxAcceptableCost;

  public GradeCategories(int maxExcellentCost, int maxAcceptableCost) {
    this.maxExcellentCost = maxExcellentCost;
    this.maxAcceptableCost = maxAcceptableCost;
  }

  public int getExcellentCount(List<Integer> costs) {
    int count = 0;
    for (int cost : costs) {
      if (cost <= maxExcellentCost) {
        count++;
      }
    }
    return count;
  }

  public int getGoodCount(List<Integer> costs) {
    int count = 0;
    for (int cost : costs) {
      if (cost > maxExcellentCost && cost <= maxAcceptableCost) {
        count++;
      }
    }
    return count;
  }

  public int getNeedsWorkCount(List<Integer> costs) {
    int count = 0;
    for (int cost : costs) {
      if (cost > maxAcceptableCost) {
        count++;
      }
    }
    return count;
  }

  public GoodnessChart createOverallChart(int value) {
    GoodnessChart chart = new GoodnessChart(maxExcellentCost, maxAcceptableCost,
        10 * maxAcceptableCost, 100 * maxAcceptableCost);
    chart.setUnscaledValues(value);
    return chart;
  }

  public PieChartUrl createDistributionChart(List<Integer> costs) {
    PieChartUrl chart = new PieChartUrl();
    chart.setItemLabel("Excellent", "Good", "Needs Work");
    chart.setColors(GREEN, YELLOW, RED);
    int excellentCount = getExcellentCount(costs);
    int goodCount = getGoodCount(costs);
    int needsWorkCount = getNeedsWorkCount(costs);
    chart.setValues(excellentCount, goodCount, needsWorkCount);
    return chart;
  }

  public HistogramChartUrl createHistogram(int width, int height,
      List<Integer> costs) {
    int binCount = min(MAX_HISTOGRAM_BINS, 10 * (int) log(costs.size()) + 1);
    int binWidth = (int) ceil((double) findMax(costs) / binCount);
    Histogram overallHistogram = new Histogram(0, binWidth, binCount);
    Histogram excellentHistogram = new Histogram(0, binWidth, binCount);
    Histogram goodHistogram = new Histogram(0, binWidth, binCount);
    Histogram needsWorkHistogram = new Histogram(0, binWidth, binCount);
    for (int overallCost : costs) {
      if (overallCost <= maxExcellentCost) {
        excellentHistogram.value(overallCost);
      } else if (overallCost <= maxAcceptableCost) {
        goodHistogram.value(overallCost);
      } else {
        needsWorkHistogram.value(overallCost);
      }
      overallHistogram.value(overallCost);
    }
    int maxBin = overallHistogram.getMaxBin();
    excellentHistogram.setMaxBin(maxBin);
    goodHistogram.setMaxBin(maxBin);
    needsWorkHistogram.setMaxBin(maxBin);


    int[] excellent = excellentHistogram.getScaledBinRange(0, MAX_VALUE, 61);
    int[] good = goodHistogram.getScaledBinRange(0, MAX_VALUE, 61);
    int[] needsWork = needsWorkHistogram.getScaledBinRange(0, MAX_VALUE, 61);

    HistogramChartUrl chart = new HistogramChartUrl();
    chart.setItemLabel(overallHistogram.getBinLabels(20));
    chart.setValues(excellent, good, needsWork);
    chart.setYMark(0, overallHistogram.getMaxBin());
    chart.setSize(width, height);
    chart.setBarWidth((width - HISTOGRAM_LEGEND_WIDTH) / binCount, 0, 0);
    chart.setChartLabel("Excellent", "Good", "Needs Work");
    chart.setColors(GREEN, YELLOW, RED);
    return chart;
  }

  private int findMax(List<Integer> values) {
    int maxValue = Integer.MIN_VALUE;
    for (int value : values) {
      maxValue = max(maxValue, value);
    }
    return maxValue;
  }
}
