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


import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class TextReportTest extends TestCase {

  ByteArrayOutputStream out = new ByteArrayOutputStream();
  TextReport report = new TextReport(new PrintStream(out), 50, 100, 0);
  CostModel costModel = new CostModel(1, 1);

  private void assertOutput(String... expected) {
    StringBuilder buf = new StringBuilder();
    for (String expect : expected) {
      buf.append(expect);
      buf.append(Constants.NEW_LINE);
    }
    assertEquals(buf.toString(), out.toString());
  }

  private ClassCost classCost(String name, int cost) {
    List<MethodCost> methods = new ArrayList<MethodCost>();
    MethodCost methodCost = new MethodCost("method_" + cost, 1, cost);
    methodCost.link(new CostModel(1, 1));
    methods.add(methodCost);
    ClassCost classCost = new ClassCost(name, methods, costModel);
    return classCost;
  }

  public void testPrintSummary() throws Exception {
    report.addClassCost(classCost("c.g.t.A", 1));
    report.addClassCost(classCost("c.g.t.B", 70));
    report.addClassCost(classCost("c.g.t.C", 70));
    report.addClassCost(classCost("c.g.t.D", 101));
    report.addClassCost(classCost("c.g.t.E", 101));
    report.addClassCost(classCost("c.g.t.F", 101));
    report.printSummary();
    assertOutput(
        "      Analyzed classes:     6",
        " Excellent classes (.):     1  16.7%",
        "      Good classes (=):     2  33.3%",
        "Needs work classes (@):     3  50.0%",
        "             Breakdown: [.........=================@@@@@@@@@@@@@@@@@@@@@@@@@]");
  }

  public void testPrintDistribution() throws Exception {
    report.addClassCost(classCost("c.g.t.A", 1));
    report.addClassCost(classCost("c.g.t.B", 10));
    report.addClassCost(classCost("c.g.t.C", 15));
    report.addClassCost(classCost("c.g.t.D", 30));
    report.addClassCost(classCost("c.g.t.E", 31));
    report.addClassCost(classCost("c.g.t.F", 32));
    report.printDistribution(3, 50);
    assertOutput(
        "       0                                                  3",
        "     5 |..................................                 :     2",
        "    16 |.................                                  :     1",
        "    27 |...................................................:     3"
        );
  }

}
