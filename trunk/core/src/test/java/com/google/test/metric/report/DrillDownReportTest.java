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

import static com.google.test.metric.Reason.NON_OVERRIDABLE_METHOD_CALL;
import static com.google.test.metric.report.DrillDownReportGenerator.NEW_LINE;
import static java.lang.Integer.MAX_VALUE;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.google.test.metric.AutoFieldClearTestCase;
import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.GlobalCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvocationCost;
import com.google.test.metric.SourceLocation;

public class DrillDownReportTest extends AutoFieldClearTestCase {

  private final  MethodCost methodCost0 = new MethodCost("c.g.t.A.method0()V", 0, false, false,
      false);
  private final  MethodCost methodCost1 = new MethodCost("c.g.t.A.method1()V", 0, false, false,
      false);
  private final  MethodCost methodCost2 = new MethodCost("c.g.t.A.method2()V", 0, false, false,
      false);
  private final  MethodCost methodCost3 = new MethodCost("c.g.t.A.method3()V", 0, false, false,
      false);
  private final ByteArrayOutputStream out = new ByteArrayOutputStream();
  private final CostModel costModel = new CostModel();

  @Override
  protected void setUp() throws Exception {
    methodCost1.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));

    methodCost2.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    methodCost2.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));

    methodCost3.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    methodCost3.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    methodCost3.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
}

  public void testSimpleCost() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    MethodCost costOnlyMethod1 = new MethodCost("c.g.t.A.method1()V", 0, false, false, false);
    costOnlyMethod1.addCostSource(new CyclomaticCost(new SourceLocation(null, 1), Cost.cyclomatic(1)));
    costOnlyMethod1.addCostSource(new GlobalCost(new SourceLocation(null, 0), null, Cost.global(1)));
    costOnlyMethod1.link();
    printer.print("", costOnlyMethod1, Integer.MAX_VALUE);
    assertStringEquals("c.g.t.A.method1()V [CC: 1, GC: 1 / CC: 1, GC: 1]\n", out.toString());
  }

  public void test2DeepPrintAll() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    methodCost2.addCostSource(new MethodInvocationCost(new SourceLocation(null, 81), methodCost1,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(1)));
    methodCost2.link();
    printer.print("", methodCost2, MAX_VALUE);
    assertStringEquals("c.g.t.A.method2()V [CC: 3 / CC: 2]\n" +
        "  line 81: c.g.t.A.method1()V [CC: 1 / CC: 1] " + NON_OVERRIDABLE_METHOD_CALL +
        "\n", out.toString());
  }

  public void test3DeepPrintAll() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    methodCost2.addCostSource(new MethodInvocationCost(new SourceLocation(null, 8), methodCost1,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(1)));
    methodCost3.addCostSource(new MethodInvocationCost(new SourceLocation(null, 2), methodCost2,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(3)));
    methodCost3.link();
    printer.print("", methodCost3, MAX_VALUE);
    assertStringEquals("c.g.t.A.method3()V [CC: 6 / CC: 3]\n" +
        "  line 2: c.g.t.A.method2()V [CC: 3 / CC: 2] " + NON_OVERRIDABLE_METHOD_CALL + "\n" +
        "    line 8: c.g.t.A.method1()V [CC: 1 / CC: 1] " + NON_OVERRIDABLE_METHOD_CALL + "\n",
        out.toString());
  }

  public void test2DeepSupress0Cost() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 2);
    methodCost1.addCostSource(new MethodInvocationCost(new SourceLocation(null, 8), methodCost0,
        NON_OVERRIDABLE_METHOD_CALL, new Cost()));
    methodCost1.addCostSource(new MethodInvocationCost(new SourceLocation(null, 13), methodCost3,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(3)));
    methodCost1.link();
    printer.print("", methodCost1, MAX_VALUE);
    assertStringEquals("c.g.t.A.method1()V [CC: 4 / CC: 1]\n" +
    		"  line 13: c.g.t.A.method3()V [CC: 3 / CC: 3] " + NON_OVERRIDABLE_METHOD_CALL + "\n",
    		out.toString());
  }

  public void test3DeepPrint2Deep() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    methodCost3.addCostSource(new MethodInvocationCost(new SourceLocation(null, 2), methodCost2,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(3)));
    methodCost2.addCostSource(new MethodInvocationCost(new SourceLocation(null, 2), methodCost1,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(1)));
    methodCost3.link();
    printer.print("", methodCost3, 2);
    assertStringEquals("c.g.t.A.method3()V [CC: 6 / CC: 3]\n" +
      "  line 2: c.g.t.A.method2()V [CC: 3 / CC: 2] " + NON_OVERRIDABLE_METHOD_CALL + "\n",
      out.toString());
  }

  public void testSupressAllWhenMinCostIs4() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 4);
    methodCost2.addCostSource(new MethodInvocationCost(new SourceLocation(null, 81), methodCost1,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(1)));
    methodCost2.link();
    printer.print("", methodCost2, MAX_VALUE);
    assertStringEquals("", out.toString());
  }

  public void testSupressPartialWhenMinCostIs2() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 2);
    methodCost2.addCostSource(new MethodInvocationCost(new SourceLocation(null, 81), methodCost1,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(1)));
    methodCost2.link();
    printer.print("", methodCost2, Integer.MAX_VALUE);
    assertStringEquals("c.g.t.A.method2()V [CC: 3 / CC: 2]\n", out.toString());
  }

  public void testSecondLevelRecursive() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    methodCost3.addCostSource(new MethodInvocationCost(new SourceLocation(null, 1), methodCost2,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(2)));
    methodCost2.addCostSource(new MethodInvocationCost(new SourceLocation(null, 2), methodCost2,
        NON_OVERRIDABLE_METHOD_CALL, new Cost()));
    methodCost3.link();
    printer.print("", methodCost3, 10);
    assertStringEquals("c.g.t.A.method3()V [CC: 5 / CC: 3]\n" +
      "  line 1: c.g.t.A.method2()V [CC: 2 / CC: 2] " + NON_OVERRIDABLE_METHOD_CALL + "\n",
      out.toString());
  }

  public void testAddOneClassCostThenPrintIt() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    ClassCost classCost0 = new ClassCost("FAKE_classInfo0", new ArrayList<MethodCost>());
    printer.addClassCost(classCost0);
    printer.printFooter();
    assertStringEquals("\nTestability cost for FAKE_classInfo0 [ cost = 0 ] [ 0 TCC, 0 TGC ]\n",
        out.toString());
  }

  public void testAddSeveralClassCostsAndPrintThem() throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    ClassCost classCost0 = new ClassCost("FAKE_classInfo0", new ArrayList<MethodCost>());
    ClassCost classCost1 = new ClassCost("FAKE_classInfo1", new ArrayList<MethodCost>());
    ClassCost classCost2 = new ClassCost("FAKE_classInfo2", new ArrayList<MethodCost>());
    printer.addClassCost(classCost0);
    printer.addClassCost(classCost1);
    printer.addClassCost(classCost2);
    printer.printFooter();
    assertStringEquals("\nTestability cost for FAKE_classInfo0 [ cost = 0 ] [ 0 TCC, 0 TGC ]\n" +
        "\nTestability cost for FAKE_classInfo1 [ cost = 0 ] [ 0 TCC, 0 TGC ]\n" +
        "\nTestability cost for FAKE_classInfo2 [ cost = 0 ] [ 0 TCC, 0 TGC ]\n",
        out.toString());
  }

  public void testAddSeveralClassCostsAndPrintThemInDescendingCostOrder()
      throws Exception {
    DrillDownReportGenerator printer =
      new DrillDownReportGenerator(new PrintStream(out), costModel, null, MAX_VALUE, 0);
    methodCost1.link();
    methodCost2.link();
    List<MethodCost> methodCosts1 = new ArrayList<MethodCost>();
    methodCosts1.add(methodCost1);
    List<MethodCost> methodCosts2 = new ArrayList<MethodCost>();
    methodCosts2.add(methodCost2);
    ClassCost classCost0 = new ClassCost("FAKE_classInfo0", new ArrayList<MethodCost>());
    ClassCost classCost1 = new ClassCost("FAKE_classInfo1", methodCosts1);
    ClassCost classCost2 = new ClassCost("FAKE_classInfo2", methodCosts2);
    printer.addClassCost(classCost0);
    printer.addClassCost(classCost1);
    printer.addClassCost(classCost2);
    printer.printFooter();
    assertStringEquals("\nTestability cost for FAKE_classInfo2 [ cost = 2 ] [ 2 TCC, 0 TGC ]\n" +
    		"  c.g.t.A.method2()V [CC: 2 / CC: 2]\n" +
        "\nTestability cost for FAKE_classInfo1 [ cost = 1 ] [ 1 TCC, 0 TGC ]\n" +
        "  c.g.t.A.method1()V [CC: 1 / CC: 1]\n" +
        "\nTestability cost for FAKE_classInfo0 [ cost = 0 ] [ 0 TCC, 0 TGC ]\n",
        out.toString());
  }

  private void assertStringEquals(String expected, String actual) {
    assertEquals(expected.replace("\n", NEW_LINE), actual);
  }

}
