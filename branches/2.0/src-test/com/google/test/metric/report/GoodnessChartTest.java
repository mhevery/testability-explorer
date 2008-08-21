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

import junit.framework.TestCase;

public class GoodnessChartTest extends TestCase {

  public void testGood() throws Exception {
    GoodnessChart chart = new GoodnessChart(50, 100, 1000, 10000);
    chart.setUnscaledValues(0);
    assertEquals("t:0", chart.getMap().get("chd"));

    chart.setUnscaledValues(50);
    assertEquals("t:25", chart.getMap().get("chd"));

    chart.setUnscaledValues(10);
    assertEquals("t:5", chart.getMap().get("chd"));

    chart.setUnscaledValues(100);
    assertEquals("t:50", chart.getMap().get("chd"));

    chart.setUnscaledValues(1000);
    assertEquals("t:75", chart.getMap().get("chd"));

    chart.setUnscaledValues(3000);
    assertEquals("t:80", chart.getMap().get("chd"));

    chart.setUnscaledValues(10000);
    assertEquals("t:100", chart.getMap().get("chd"));

    chart.setUnscaledValues(15000);
    assertEquals("t:100", chart.getMap().get("chd"));
  }

}
