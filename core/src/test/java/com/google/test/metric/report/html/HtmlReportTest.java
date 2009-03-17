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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.text.MessageFormat;
import static java.text.MessageFormat.format;

import junit.framework.TestCase;

import com.google.test.metric.*;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;
import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.SourceLinker;

public class HtmlReportTest extends TestCase {
  private HtmlReport report;
  private ByteArrayOutputStream out;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    out = new ByteArrayOutputStream();
    CostModel costModel = new CostModel();
    IssuesReporter issuesReporter = new IssuesReporter(new LinkedList<ClassIssues>(), costModel);
    ReportOptions options = new ReportOptions(1, 10, 10, 20, 5, 100, 100, 1, 10, "", "");
    SourceLinker linker = new SourceLinker("http://code.repository/basepath/{path}&line={line}",
        "http://code.repository/basepath/{path}");
    report = new HtmlReport(new PrintStream(out), costModel, issuesReporter, options, linker);
  }

  public void testPrintReport() throws Exception {
    report.printHeader();
    report.addClassCost(new ClassCost("com.google.FooClass", Arrays.asList(new MethodCost("methodFoo", 1, false, false))));
    report.printFooter();
    String text = out.toString();

    assertTrue(text, text.contains("<script type=\"text/javascript\""));
    assertTrue(text, text.contains("Report generated"));
    assertTrue(text, text.contains("function toggle(element)"));
    assertTrue(text, text.contains("function clickHandler(event)"));
    assertTrue(text, text.contains("<a href=\"http://code.repository/basepath/com/google/FooClass&line"));
  }

  public void testConstructorCosts() throws Exception {
    report.printHeader();
    ClassCost cost = new ClassCost("classFoo", Arrays.asList(new MethodCost("methodFoo", 1, false, false)));
    report.addClassCost(cost);
    ClassIssues classIssues = new ClassIssues(cost.getClassName(), 0);
    classIssues.getConstructionIssues().workInConstructor(new MethodCost("foo()", 10, true, false),
        cost.getTotalComplexityCost(), cost.getTotalGlobalCost());
    report.getWorstOffenders().add(classIssues);
    report.printFooter();
    String text = out.toString();
    ResourceBundle bundle = ResourceBundle.getBundle("messages");
    String expected = format(bundle.getString("report.explain.class.hardToTest"),
        "<tt>", "classFoo", "</tt>");
    assertTrue(text, text.contains(expected));
  }
}
