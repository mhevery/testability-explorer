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
package com.google.test.metric.method.op.stack;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.google.test.metric.JavaType;
import com.google.test.metric.Variable;

public class SwapTest extends TestCase {

  public void testOperatorCountIs2() throws Exception {
    assertEquals(2, new Swap(-1).getOperatorCount());
  }

  public void testApplySwaps() throws Exception {
    Swap swap = new Swap(-1);
    Variable first = new Variable(null, JavaType.BOOLEAN, false, false);
    Variable second = new Variable(null, JavaType.BOOLEAN, false, false);
    List<Variable> inputs = new ArrayList<Variable>();
    inputs.add(first);
    inputs.add(second);
    List<Variable> output = swap.apply(inputs);
    assertSame(second, output.get(0));
    assertSame(first, output.get(1));
  }

  public void testToString() {
    assertEquals("swap", new Swap(-1).toString());
  }

}
