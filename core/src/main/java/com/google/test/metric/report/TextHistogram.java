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

import com.google.test.metric.report.chart.PieGraph;

import java.util.Formatter;

public class TextHistogram {

  private final int width;
  private final int height;
  private final Marker marker;
  private int min = 0;
  private int max = -1;

  public TextHistogram(int width, int height, Marker marker) {
    this.width = width;
    this.height = height;
    this.marker = marker;
  }

  public void setMax(int max) {
    this.max = max;
  }

  public void setMin(int min) {
    this.min = min;
  }

  public String[] graph(float... values) {
    String[] rows = new String[height + 1];
    if (max == -1) {
      max = (int) Math.ceil(maxFloat(values));
    }
    int bucketWidth = (int)Math.ceil((float)(max - min) / height);
    int[] counts = count(bucketWidth, values);
    int maxCount = max(counts);
    StringBuilder out = new StringBuilder();
    Formatter formatter = new Formatter(out);
    formatter.format("%8d %" + width + "d", 0, maxCount);
    rows[0] = out.toString();
    for (int i = 0; i < counts.length; i++) {
      out.setLength(0);
      int bucketId = (int) (min + bucketWidth * i + bucketWidth / 2f);
      PieGraph pieGraph = new PieGraph(width, new CharMarker(marker.get(i, bucketId), ' '));
      String bar = pieGraph.render(counts[i], maxCount - counts[i]);
      formatter.format("%6d |%s:%6d", bucketId, bar, counts[i]);
      rows[i + 1] = out.toString();
    }
    return rows;
  }

  public int max(int... values) {
    int max = 0;
    for (int value : values) {
      max = Math.max(max, value);
    }
    return max;
  }

  public float maxFloat(float... values) {
    float max = 0;
    for (float value : values) {
      max = Math.max(max, value);
    }
    return max;
  }

  public int[] count(int binSize, float... values) {
    int[] counts = new int[height];
    for (float value : values) {
      int bin = min;
      for (int row = 0; row < height; row++) {
        bin += binSize;
        if (bin >= value) {
          counts[row]++;
          break;
        }
      }
    }
    return counts;
  }

}
