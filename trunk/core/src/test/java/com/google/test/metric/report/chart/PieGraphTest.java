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

import junit.framework.TestCase;
import com.google.test.metric.report.CharMarker;

public class PieGraphTest extends TestCase {

  CharMarker marker = new CharMarker('.', '=', '#');

  public void testChart() throws Exception {
    assertEquals("111223", new PieGraph(6, new CharMarker('1', '2', '3')).render(3, 2, 1));
  }

  public void testChartEven() throws Exception {
    assertEquals(".==###", new PieGraph(6, marker).render(1, 2, 3));
    assertEquals("..====######", new PieGraph(12, marker).render(1, 2, 3));
    assertEquals("....========############", new PieGraph(24, marker).render(1, 2, 3));
  }

  public void testChartOdd() throws Exception {
    assertEquals(".==##", new PieGraph(5, marker).render(1, 2, 3));
    assertEquals("..===######", new PieGraph(10, marker).render(1, 2, 3));
    assertEquals("....=======#########", new PieGraph(20, marker).render(1, 2, 3));
  }

}
