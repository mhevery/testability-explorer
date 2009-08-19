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

import static com.google.test.metric.report.chart.GoogleChartAPI.GREEN;
import static com.google.test.metric.report.chart.GoogleChartAPI.RED;
import static com.google.test.metric.report.chart.GoogleChartAPI.YELLOW;
import junit.framework.TestCase;

public class PieChartUrlTest extends TestCase {

  public void testPieChart() throws Exception {
    GoogleChartAPI url = new PieChartUrl();
    url.setSize(300, 100);
    url.setTitle("Class Breakdown");
    url.setItemLabel("Excellent", "Good", "Needs Work");
    url.setColors(GREEN, YELLOW, RED);
    url.setValues(30,15,5);
    assertEquals("http://chart.apis.google.com/chart", GoogleChartAPI.BASE_URL);
    assertEquals("p3", url.getMap().get("cht"));
    assertEquals("t:60,30,10", url.getMap().get("chd"));
    assertEquals("300x100", url.getMap().get("chs"));
    assertEquals("Excellent|Good|Needs Work", url.getMap().get("chl"));
    assertEquals("00AA00,FFFF00,D22222", url.getMap().get("chco"));
    assertEquals("Class Breakdown", url.getMap().get("chtt"));
  }

}
