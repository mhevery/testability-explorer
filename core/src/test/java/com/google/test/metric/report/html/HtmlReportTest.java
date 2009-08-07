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

import com.google.common.collect.Lists;
import com.google.test.metric.AnalysisModel;
import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvocationCost;
import com.google.test.metric.Reason;
import com.google.test.metric.ReportGeneratorProvider;
import com.google.test.metric.SourceLocation;
import com.google.test.metric.report.ClassPathTemplateLoader;
import com.google.test.metric.report.FreemarkerReportGenerator;
import static com.google.test.metric.report.FreemarkerReportGenerator.HTML_REPORT_TEMPLATE;
import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.SourceLinker;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.Issue;
import com.google.test.metric.report.issues.IssueSubType;
import com.google.test.metric.report.issues.IssueType;
import com.google.test.metric.report.issues.IssuesReporter;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static java.text.MessageFormat.format;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import static java.util.ResourceBundle.getBundle;

public class HtmlReportTest extends TestCase {
  private FreemarkerReportGenerator generator;
  private ByteArrayOutputStream out;
  private ClassCost cost;
  private List<ClassIssues> importantIssues = Lists.newLinkedList();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    out = new ByteArrayOutputStream();
    CostModel costModel = new CostModel();
    SourceLocation location = new SourceLocation("com/google/FooClass.java", 1);
    final Issue issue =
        new Issue(location, "void doThing()", 0.5f, IssueType.CONSTRUCTION, IssueSubType.SETTER);
    final MethodCost methodCost = new MethodCost("", "void setUp()", 1, false, false, false);
    methodCost.addCostSource(new MethodInvocationCost(location, methodCost,
        Reason.IMPLICIT_SETTER, new Cost(100, 1, new int[0])));

    //TODO: looks like we want IssuesReporter to be an interface
    IssuesReporter issuesReporter = new IssuesReporter(null, null) {
      @Override
      public List<ClassIssues> getMostImportantIssues() {
        ClassCost aClassCost = new ClassCost("com/google/FooClass", Arrays.asList(methodCost));
        importantIssues.add(determineIssues(aClassCost));
        return importantIssues;
      }

      @Override
      public void inspectClass(ClassCost classCost) {
      }

      @Override
      public ClassIssues determineIssues(ClassCost classCost) {
        return new ClassIssues("com/google/FooClass", 100, new LinkedList<Issue>(Arrays.asList(issue)));
      }
    };
    ReportOptions options = new ReportOptions(1, 10, 10, 20, 5, 100, 100, 1, 10, "", "");
    SourceLinker linker = new SourceLinker("http://code.repository/basepath/{path}&line={line}",
        "http://code.repository/basepath/{path}");

    cost = new ClassCost(getClass().getName(), Arrays.asList(methodCost));
    HtmlReportModel report =
        new HtmlReportModel(costModel, new AnalysisModel(issuesReporter), options);
    BeansWrapper objectWrapper = new DefaultObjectWrapper();
    Configuration configuration = new Configuration();
    configuration.setObjectWrapper(objectWrapper);
    ResourceBundleModel bundleModel = new ResourceBundleModel(getBundle("messages"), objectWrapper);
    configuration.setTemplateLoader(new ClassPathTemplateLoader(ReportGeneratorProvider.PREFIX));
    report.setMessageBundle(bundleModel);
    report.setSourceLinker(new SourceLinkerModel(linker));
    generator = new FreemarkerReportGenerator(report, new PrintStream(out),
        HTML_REPORT_TEMPLATE, configuration);
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
    assertTrue(text, text
        .contains("<a href=\"http://code.repository/basepath/com/google/FooClass.java&line"));
  }

  public void testConstructorCosts() throws Exception {
    ClassCost cost =
        new ClassCost("classFoo", Arrays.asList(new MethodCost("", "methodFoo", 1, false, false, false)));
    ClassIssues classIssues = new ClassIssues(cost.getClassName(), 0);
    classIssues.add(new Issue(new SourceLocation("", 1), null, 1f, null, null));
    importantIssues.add(classIssues);
    generator.printHeader();
    generator.addClassCost(cost);
    generator.printFooter();
    String text = out.toString();
    ResourceBundle bundle = ResourceBundle.getBundle("messages");
    String expected =
        format(bundle.getString("report.explain.class.hardToTest"), "<tt>", "classFoo", "</tt>");
    assertTrue(text, text.contains(expected));
  }
}
