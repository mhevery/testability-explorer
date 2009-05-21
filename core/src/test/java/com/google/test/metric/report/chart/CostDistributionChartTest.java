/*
 * Copyright 2009 Google Inc.
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

import com.google.test.metric.report.GradeCategories;

import junit.framework.TestCase;

import java.io.File;
import java.io.FileOutputStream;
import static java.util.Arrays.asList;

/**
 * Tests for {@link com.google.test.metric.report.chart.CostDistributionChart}
 *
 * @author aeagle
 */
public class CostDistributionChartTest extends TestCase {

  public void testCreateChart() throws Exception {
    GradeCategories gradeCategories = new GradeCategories(50, 100);
    CostDistributionChart chart = new CostDistributionChart(gradeCategories);
    chart.addValues(asList(5,10,15,20,30,40, 23, 34, 34,45,45,45,23,34,1,2,3,4, 
        80,100, 76, 180,345,575, 883, 4012, 500));

    File tempFile = File.createTempFile("chart", ".png");
    chart.createChart(new FileOutputStream(tempFile));
    assertTrue(tempFile.exists());
    assertTrue(tempFile.length() > 1);
    System.out.println("Chart created in " + tempFile.getAbsolutePath());
  }
}
