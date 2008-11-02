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
package com.google.test.metric;

import junit.framework.TestCase;

public class CostTest extends TestCase {

  public void testAddingZeroIsZero() throws Exception {
    Cost none = Cost.none();
    none.add(Cost.none());
    assertEquals(Cost.none(), Cost.none());
    assertEquals(0, Cost.none().getLoDSum());
  }

  public void testAddOfGreaterSize() throws Exception {
    Cost lod = Cost.lod(0);
    lod.add(Cost.lod(1));
    assertEquals(Cost.lodDistribution(1, 1), lod);
    assertEquals(2, lod.getLoDSum());
  }

  public void testAddOfSameSize() throws Exception {
    Cost lod = Cost.lod(0);
    lod.add(Cost.lod(0));
    assertEquals(Cost.lodDistribution(2), lod);
    assertEquals(2, lod.getLoDSum());
  }

  public void testAddOfSmallerSize() throws Exception {
    Cost lod = Cost.lod(1);
    lod.add(Cost.lod(0));
    assertEquals(Cost.lodDistribution(1, 1), lod);
    assertEquals(2, lod.getLoDSum());
  }

}
