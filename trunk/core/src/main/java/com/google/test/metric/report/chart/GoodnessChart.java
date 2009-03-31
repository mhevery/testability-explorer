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

public class GoodnessChart extends GoogleChartAPI {

  private final int[] milestones;

  public GoodnessChart(int...milestones) {
    this.milestones = milestones;
    setSize(150, 80);
    keys.put("cht", "gom");
    setColors("00FF00", "FFFF00", "FF0000", "550000");
  }

  public void setUnscaledValues(int value) {
    int lastMilestone = 0;
    int base = 0;
    int baseWidth = 100 / milestones.length;
    for (int milestone : milestones) {
      if (value <= milestone) {
        setValues(base + baseWidth * (value - lastMilestone) / (milestone - lastMilestone) );
        return;
      }
      lastMilestone = milestone;
      base += baseWidth;
    }
    setValues(100);
  }


}
