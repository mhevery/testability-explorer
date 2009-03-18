/*
 * Copyright 2009 Google Inc.
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
package com.google.test.metric.report.about;

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.report.*;
import com.google.test.metric.report.Source.Line;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;
import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static java.util.Arrays.asList;
import java.util.LinkedList;

/**
 * Tests that the About report can be generated.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class AboutTestabilityReportGeneratorTest extends TestCase {
  FreemarkerReportGenerator generator;
  ByteArrayOutputStream out = new ByteArrayOutputStream();

  public void testExample() throws Exception {
    IssuesReporter reporter = new IssuesReporter(new LinkedList<ClassIssues>(), new CostModel());
    ReportModel model = new AboutTestabilityReport(reporter, new SourceLoader(null) {
      @Override
      public Source load(String name) {
        return new Source(asList(
            new Line(1, "Copyright garbage!"),
            new Line(2, "package com.google.test.metric.example;"),
            new Line(3, "class SumOfPrimes {"),
            new Line(4, "  public void sum() {}"),
            new Line(5, "}")));
      }
    });
    generator = new FreemarkerReportGenerator(model, new PrintStream(out), new SourceLinker("", ""), "about/Report.html");
    generator.printHeader();
    generator.addClassCost(new ClassCost("com.google.test.metric.example.SumOfPrimes1",
        asList(new MethodCost("foo()", 1, false, false))));
    generator.printFooter();

    String text = out.toString();
    assertTrue(text, text.contains(">SumOfPrimes1<"));
    assertTrue(text, text.contains("sum"));
    assertFalse(text, text.contains("Copyright"));
  }
}
