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

import static java.util.Arrays.asList;
import junit.framework.TestCase;

public class GradeCategoriesTest extends TestCase {

  GradeCategories grade = new GradeCategories(1, 2);

  public void testGetExcelentCount() throws Exception {
    assertEquals(2, grade.getExcelentCount(asList(1, 1, 2, 3)));
  }

  public void testGetGoodCount() throws Exception {
    assertEquals(2, grade.getGoodCount(asList(1, 2, 2, 3)));
  }

  public void testGetNeedsWorkCount() throws Exception {
    assertEquals(2, grade.getNeedsWorkCount(asList(1, 2, 3, 3)));
  }

  public void testCreateOverallChart() throws Exception {
    GoodnessChart chart = grade.createOverallChart(0);
    assertEquals("t:0", chart.getMap().get("chd"));
  }

  public void testCreateDistributionChart() throws Exception {
    PieChartUrl chart = grade.createDistributionChart(asList(1, 2, 2, 3, 3, 3));
    assertEquals("t:16,33,50", chart.getMap().get("chd"));
  }

  public void testCreateHistogram() throws Exception {
    HistogramChartUrl chart = grade.createHistogram(0, 0, asList(1, 2, 2, 3, 3, 3));
    assertEquals("s:AVAAAAAAAAA,AApAAAAAAAA,AAA9AAAAAAA", chart.getMap().get("chd"));
  }

}
