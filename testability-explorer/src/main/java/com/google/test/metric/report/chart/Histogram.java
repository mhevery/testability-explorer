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
package com.google.test.metric.report.chart;

import static java.lang.Math.ceil;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.min;

import com.google.common.base.Function;
import com.google.common.base.Nullable;

public class Histogram {

  private final int min;
  private final int binWidth;
  private final int[] bins;
  private final Function<Integer, Double> scalingFunction;
  private int maxBin;
  public static class Linear implements Function<Integer, Double> {
    public Double apply(@Nullable Integer integer) {
      return Double.valueOf(integer);
    }
  }
  public static class Logarithmic implements Function<Integer, Double> {
    public Double apply(@Nullable Integer integer) {
      return log10(integer) + 1;
    }
  }

  public Histogram(int min, int binWidth, int binCount, Function<Integer, Double> scalingFunction) {
    this.min = min;
    this.binWidth = binWidth;
    this.scalingFunction = scalingFunction;
    this.bins = new int[binCount];
  }

  public void value(int value) {
    maxBin = max(maxBin, ++bins[Math.min(bins.length -1, bin(value))]);
  }

  private int bin(int value) {
    if (binWidth == 0) {
      return 0;
    }
    return ((value - min) / binWidth);
  }

  public int[] getBins() {
    return bins;
  }

  public int getMaxBin() {
    return maxBin;
  }

  public void setMaxBin(int maxBin) {
    this.maxBin = maxBin;
  }

  public int[] getBinRange() {
    return getScaledBinRange(0, Integer.MAX_VALUE, maxBin);
  }

  public int[] getScaledBinRange(int from, int to, int maxScale) {
    double scale = (double) maxScale / scalingFunction.apply(maxBin);
    int[] scaledBins = new int[bins.length];
    int firstBin = max(0, bin(from));
    int lastBin = min(bins.length, bin(to));
    for (int binNumber = firstBin; binNumber < lastBin; binNumber++) {
      scaledBins[binNumber] = (int) ceil(scale * scalingFunction.apply(bins[binNumber]));
    }
    return scaledBins;
  }

  public String[] getBinLabels(int maxLabels) {
    String[] labels = new String[bins.length];
    int everyN = (int) Math.ceil((float) bins.length / maxLabels);
    for (int i = 0; i < bins.length; i++) {
      if (i % everyN == 0) {
        labels[i] = Integer.toString(i * binWidth + binWidth / 2);
      } else {
        labels[i] = "";
      }
    }
    return labels;
  }

}
