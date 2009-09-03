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

import static java.lang.Math.pow;
import static java.lang.String.format;

import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.test.metric.report.chart.Histogram.Logarithmic;

public class HistogramChartUrl extends GoogleChartAPI {

  public HistogramChartUrl() {
    keys.put("cht", "bvs");
  }

  public void setBarWidth(int width) {
    keys.put("chbh", Integer.toString(width));
  }

  public void setBarWidth(int width, int spaceBar, int spaceGroup) {
    keys.put("chbh", toList(",", width, spaceBar, spaceGroup));
  }

  public void setYMark(int min, int max, Function<Integer, Double> scalingFunction) {
    keys.put("chxt", "y");
    if (scalingFunction instanceof Logarithmic) {
      List<String> yLabels = Lists.newLinkedList();
      List<String> yPositions = Lists.newLinkedList();
      double scaledMax = scalingFunction.apply(max);
      for (int labelExponent = 0; pow(10, labelExponent) < max; labelExponent++) {
        yLabels.add(String.valueOf((int)pow(10, labelExponent)));
        yPositions.add(String.valueOf(100 * (labelExponent + 1) / scaledMax));
      }
      keys.put("chxl", "0:|" + toList("|", yLabels.toArray(new String[yLabels.size()])));
      keys.put("chxp", "0," + toList(",", yPositions.toArray(new String[yPositions.size()])));
    } else {
      keys.put("chxr", format("0,%d,%d", min, max));
    }
  }

}
