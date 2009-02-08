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

  public void setYMark(int...value) {
    keys.put("chxt", "y");
    keys.put("chxl", "0:|" + toList("|", value));
  }

}
