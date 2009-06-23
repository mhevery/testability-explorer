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

import com.google.test.metric.report.chart.Histogram.Linear;
import com.google.test.metric.report.chart.Histogram.Logarithmic;

import junit.framework.TestCase;

import static java.lang.Integer.MAX_VALUE;
import java.util.Arrays;

public class HistogramTest extends TestCase {

  public void testSimpleBreakdown() throws Exception {
    Histogram histogram = new Histogram(1, 1, 3, new Linear());
    histogram.value(1);
    histogram.value(1);
    histogram.value(1);
    histogram.value(2);
    histogram.value(2);
    histogram.value(3);
    assertArrayEquals(array(3, 2, 1), histogram.getBins());
    assertArrayEquals(array(30, 0, 0), histogram.getScaledBinRange(1, 2, 30));
    assertArrayEquals(array(0, 20, 0), histogram.getScaledBinRange(2, 3, 30));
    assertArrayEquals(array(0, 0, 10), histogram.getScaledBinRange(3, MAX_VALUE, 30));
  }

  private void assertArrayEquals(int[] expected, int[] actual) {
    assertTrue(String.format("Expected %s was %s", Arrays.toString(expected),
        Arrays.toString(actual)), Arrays.equals(expected, actual));
  }

  private void assertArrayEquals(String[] expected, String[] actual) {
    assertTrue(String.format("Expected %s was %s", Arrays.toString(expected),
        Arrays.toString(actual)), Arrays.equals(expected, actual));
  }

  public void testLogarithmicScaling() throws Exception {
    Histogram histogram = new Histogram(1, 1, 3, new Logarithmic());
    for (int i=0; i<100; i++) {
      histogram.value(1);
    }
    for (int i=0; i<10; i++) {
      histogram.value(2);
    }
    histogram.value(3);

    assertArrayEquals(array(100, 10, 1), histogram.getBins());
    assertArrayEquals(array(30, 0, 0), histogram.getScaledBinRange(1, 2, 30));
    assertArrayEquals(array(0, 20, 0), histogram.getScaledBinRange(2, 3, 30));
    assertArrayEquals(array(0, 0, 10), histogram.getScaledBinRange(3, MAX_VALUE, 30));
  }

  private int[] array(int... values) {
    return values;
  }

  private String[] array(String... values) {
    return values;
  }

}
