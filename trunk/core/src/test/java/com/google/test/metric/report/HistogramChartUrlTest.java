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

import junit.framework.TestCase;

public class HistogramChartUrlTest extends TestCase {

  // http://chart.apis.google.com/chart?cht=bvs&chs=300x125&chdl=Excellent|Good|Needs+Work&chco=00AA00,FFFF00,D22222&chd=s:ZGAA,AAGA,AAAL
  public void testURL() throws Exception {
    HistogramChartUrl url = new HistogramChartUrl();
    url.setSize(300, 125);
    url.setChartLabel("Excellent", "Good", "Needs Work");
    url.setColors(GREEN, YELLOW, RED);
    url.setValues(array(10, 5, 0, 0), array(0,0,3,0), array(0,0,0,2));
    assertEquals("bvs", url.getMap().get("cht"));
    assertEquals("300x125", url.getMap().get("chs"));
    assertEquals("Excellent|Good|Needs Work", url.getMap().get("chdl"));
    assertEquals("00AA00,FFFF00,D22222", url.getMap().get("chco"));
    assertEquals("s:KFAA,AADA,AAAC", url.getMap().get("chd"));
  }

  private int[] array(int... values) {
    return values;
  }

}
