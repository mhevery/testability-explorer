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

import com.google.test.metric.*;
import com.google.test.metric.MethodInvokationCost.Reason;
import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.SourceLinker;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static java.text.MessageFormat.format;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;

public class HtmlReportTest extends TestCase {
  private HtmlReport report;
  private HtmlReportGenerator generator;
  private ByteArrayOutputStream out;
  private ClassCost cost;
  private SourceLinker linker;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    out = new ByteArrayOutputStream();
    CostModel costModel = new CostModel();
    IssuesReporter issuesReporter = new IssuesReporter(new LinkedList<ClassIssues>(), costModel);
    ReportOptions options = new ReportOptions(1, 10, 10, 20, 5, 100, 100, 1, 10, "", "");
    linker = new SourceLinker("http://code.repository/basepath/{path}&line={line}",
        "http://code.repository/basepath/{path}");
    MethodCost methodCost = new MethodCost("methodFoo", 1, false, false);
    methodCost.addCostSource(new MethodInvokationCost(1, methodCost, Reason.IMPLICIT_SETTER, new Cost(100, 1, new int[0])));
    cost = new ClassCost("com.google.FooClass", Arrays.asList(methodCost));
    report = new HtmlReport(costModel, issuesReporter, options);
    generator = new HtmlReportGenerator(report, new PrintStream(out), issuesReporter, linker);
  }

  public void testPrintReport() throws Exception {
    generator.printHeader();
    generator.addClassCost(cost);
    generator.printFooter();
    String text = out.toString();

    assertTrue(text, text.contains("<script type=\"text/javascript\""));
    assertTrue(text, text.contains("Report generated"));
    assertTrue(text, text.contains("function toggle(element)"));
    assertTrue(text, text.contains("function clickHandler(event)"));
    assertTrue(text, text.contains("<a href=\"http://code.repository/basepath/com/google/FooClass&line"));
  }

  public void testConstructorCosts() throws Exception {
    generator.printHeader();
    ClassCost cost = new ClassCost("classFoo", Arrays.asList(new MethodCost("methodFoo", 1, false, false)));
    generator.addClassCost(cost);
    ClassIssues classIssues = new ClassIssues(cost.getClassName(), 0);
    classIssues.getConstructionIssues().add(null, true);
    report.getWorstOffenders().add(classIssues);
    generator.printFooter();
    String text = out.toString();
    ResourceBundle bundle = ResourceBundle.getBundle("messages");
    String expected = format(bundle.getString("report.explain.class.hardToTest"),
        "<tt>", "classFoo", "</tt>");
    assertTrue(text, text.contains(expected));
  }
}
