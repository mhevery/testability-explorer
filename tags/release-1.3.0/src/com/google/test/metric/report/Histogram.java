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

import static java.lang.Math.ceil;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Histogram {

  private final int min;
  private final int binWidth;
  private final int[] bins;
  private int maxBin;

  public Histogram(int min, int binWidth, int binCount) {
    this.min = min;
    this.binWidth = binWidth;
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

  public int[] getScaledBinRange(int from, int to, int maxScale) {
    double scale = (double) maxScale / maxBin;
    int[] subBins = new int[bins.length];
    int toBin = min(bins.length, bin(to));
    for (int i = max(0, bin(from)); i < toBin; i++) {
      subBins[i] = (int) ceil((scale * bins[i]));
    }
    return subBins;
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
