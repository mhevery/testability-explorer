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

public class TextHistorgramTest extends TestCase {

  public void testDrawHistogram() throws Exception {
    TextHistogram histogram = new TextHistogram(6, 3, new CharMarker('#'));
    histogram.setMin(0);
    histogram.setMax(30);
    String[] rows = histogram.graph(10,20,20,30,30,30);
    assertEquals("       0      3", rows[0]);
    assertEquals("     5 |##    :     1", rows[1]);
    assertEquals("    15 |####  :     2", rows[2]);
    assertEquals("    25 |######:     3", rows[3]);
  }

  public void testMax() throws Exception {
    assertEquals(5, new TextHistogram(-1,-1, null).max(1,2,5,4,5,3,0));
  }

  public void testCount() throws Exception {
    TextHistogram histogram = new TextHistogram(-1, 3, null);
    histogram.setMin(0);
    histogram.setMax(30);
    int[] counts = histogram.count(10, 10,20,20,30,30,30);
    assertEquals(1, counts[0]);
    assertEquals(2, counts[1]);
    assertEquals(3, counts[2]);
  }

}
