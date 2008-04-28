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
package com.google.test.metric.example;

import com.google.test.metric.AutoFieldClearTestCase;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInfo;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.example.GlobalExample.Gadget;

public class GlobalExampleTest extends AutoFieldClearTestCase {

  ClassRepository repo = new ClassRepository();
  RegExpWhiteList whitelist = new RegExpWhiteList();
  MetricComputer computer = new MetricComputer(repo, null, whitelist, new CostModel());

  private MethodCost cost(Class<?> clazz, String method) {
    return computer.compute(clazz, method);
  }

  public void testAccessingAFinalStaticIsOK() throws Exception {
    MethodCost cost = cost(GlobalExample.class,
        "getInstance()Lcom/google/test/metric/example/GlobalExample$Gadget;");
    assertEquals(0, cost.getTotalGlobalCost());
  }

  public void testAccessingAFinalFieldDoesNotCountAgainstYou() throws Exception {
    MethodCost cost = cost(GlobalExample.class,
        "getGlobalId()Ljava/lang/String;");
    assertEquals(0, cost.getTotalGlobalCost());
  }

  public void testAccessingANonFinalFieldCountsAgainstYou() throws Exception {
    MethodCost cost = cost(GlobalExample.class, "getGlobalCount()I");
    assertEquals(1, cost.getTotalGlobalCost());
  }

  public void testWritingANonFinalFieldCountsAgainstYou() throws Exception {
    MethodCost cost = cost(GlobalExample.class, "globalIncrement()I");
    assertEquals(1, cost.getTotalGlobalCost());
  }

  public void testGadgetGetCountHasOneReturnOperation() throws Exception {
    MethodInfo getCount = repo.getClass(Gadget.class).getMethod("getCount()I");
    assertEquals(1, getCount.getOperations().size());
  }

}
