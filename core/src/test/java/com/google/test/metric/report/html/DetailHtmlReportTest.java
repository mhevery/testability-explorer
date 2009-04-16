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
package com.google.test.metric.report.html;

import com.google.test.metric.report.SourceLinker;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvokationCost;
import com.google.test.metric.ViolationCost;
import static com.google.test.metric.Reason.*;

public class DetailHtmlReportTest extends TestCase {

  CostModel costModel = new CostModel(1, 1);

  String emptyLineTemplate = "";
  String emptyClassTemplate = "";

  String lineTemplate = "http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}#{line}";
  String classTemplate = "http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}";

  public void testWriteLineCost() throws Exception {
    MethodCost methodCost = createMethodCallWithOverallCost("a.methodName()V", 64);
    MethodInvokationCost cost = new MethodInvokationCost(123, methodCost,
      NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(64), null);
    cost.link(new Cost(), new Cost());

    DetailHtmlReport report = new DetailHtmlReport(costModel, new SourceLinker(
            emptyLineTemplate, emptyClassTemplate), 10, 10);
    report.write(cost, "");
    String text = report.getOutput();

    assertTrue(text, text.contains("<div class=\"Line\""));
    assertTrue(text, text.contains("123"));
    assertTrue(text, text.contains("methodName"));
    assertTrue(text, text.contains("64"));
    assertTrue(text, text.endsWith("</div>"));
  }

  public void testLinkedLineCost() throws Exception {
    MethodCost methodCost = createMethodCallWithOverallCost("a.methodName()V", 64);
    MethodInvokationCost lineCost = new MethodInvokationCost(123, methodCost,
      NON_OVERRIDABLE_METHOD_CALL, new Cost(), null);
    methodCost.link();

    DetailHtmlReport report = new DetailHtmlReport(costModel, new SourceLinker(
            lineTemplate, classTemplate), 10, 10);
    report.write(lineCost, "com/google/ant/TaskModel.java");
    String text = report.getOutput();

    assertTrue(text,
        text.contains("<a href=\"http://code.google.com/p/testability-explorer/source/browse/trunk/src/com/google/ant/TaskModel.java#123"));
  }

  public void testWriteMethodCost() throws Exception {
    DetailHtmlReport report = new DetailHtmlReport(costModel, new SourceLinker(
            emptyLineTemplate, emptyClassTemplate), 10, 10) {
      @Override
      public void write(ViolationCost cost, String classFilePath) {
        write(" MARKER:" + cost.getLineNumber());
      }
    };

    MethodCost methodCost = createMethodCallWithOverallCost("a.methodX()V", 0);
    methodCost.addCostSource(new MethodInvokationCost(123,
      createMethodCallWithOverallCost("cost1", 567),
      NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(567), null));
    methodCost.addCostSource(new MethodInvokationCost(543,
      createMethodCallWithOverallCost("cost2", 789),
      NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(789), null));
    methodCost.link();
    report.write(methodCost, "");
    String text = report.getOutput();
    assertTrue(text, text.contains("<div class=\"Method\""));
    assertTrue(text, text.contains("<span class='expand'>[+]</span>"));
    assertTrue(text, text.contains("methodX"));
    assertTrue(text, text.contains("[&nbsp;" + (567 + 789) + "&nbsp;]"));
    assertTrue(text, text.contains("MARKER:123"));
    assertTrue(text, text.contains("MARKER:543"));
    assertTrue(text, text.endsWith("</div>"));
  }

  public void testWriteClassCost() throws Exception {
    DetailHtmlReport report = new DetailHtmlReport(costModel, new SourceLinker(
            emptyLineTemplate, emptyClassTemplate), 10, 10) {
      @Override
      public void write(MethodCost methodCost, String classFilePath) {
        write(" MARKER:" + methodCost.getMethodName());
      }
    };

    List<MethodCost> methods = new ArrayList<MethodCost>();
    MethodCost m1 = createMethodCallWithOverallCost("methodX", 233);
    MethodCost m2 = createMethodCallWithOverallCost("methodY", 544);
    m1.link();
    m2.link();
    methods.add(m1);
    methods.add(m2);
    ClassCost classCost = new ClassCost("classFoo", methods);
    report.write(classCost);
    String text = report.getOutput();

    assertTrue(text, text.contains("<div class=\"Class\""));
    assertTrue(text, text.contains("<span class='expand'>[+]</span>"));
    assertTrue(text, text.contains("classFoo"));
    assertTrue(text, text.contains("[&nbsp;" + 475 + "&nbsp;]"));
    assertTrue(text, text.contains("MARKER:methodX"));
    assertTrue(text, text.contains("MARKER:methodY"));
    assertTrue(text, text.endsWith("</div>"));
  }

  public void testLinkedClassCost() throws Exception {
    DetailHtmlReport report = new DetailHtmlReport(costModel, new SourceLinker(
         lineTemplate , classTemplate), 10, 10) ;

    List<MethodCost> methods = new ArrayList<MethodCost>();
    ClassCost classCost = new ClassCost("com.google.ant.TaskModel", methods);
    report.write(classCost);
    String text = report.getOutput();

    assertTrue(text,
        text.contains("(<a href=\"http://code.google.com/p/testability-explorer/source/browse/" +
                "trunk/src/com/google/ant/TaskModel.java\" target=\"source\">source</a>)"));

  }

  private MethodCost createMethodCallWithOverallCost(String methodName,
      int overallCost) {
    MethodCost methodCost = new MethodCost(methodName, -1, false, false);
    for (int i = 0; i < overallCost; i++) {
      methodCost.addCostSource(new CyclomaticCost(i, Cost.cyclomatic(1)));
    }
    return methodCost;
  }

}
