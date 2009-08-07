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
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.ReportGeneratorProvider;
import com.google.test.metric.report.ClassPathTemplateLoader;
import com.google.test.metric.report.FreemarkerReportGenerator;
import com.google.test.metric.report.ReportModel;
import com.google.test.metric.report.Source;
import com.google.test.metric.report.Source.Line;
import com.google.test.metric.report.SourceLoader;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.ClassMunger;
import com.google.test.metric.report.issues.HypotheticalCostModel;
import com.google.test.metric.report.issues.IssuesReporter;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static java.util.Arrays.asList;
import java.util.LinkedList;
import static java.util.ResourceBundle.getBundle;

/**
 * Tests that the About report can be generated.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class AboutTestabilityReportGeneratorTest extends TestCase {
  FreemarkerReportGenerator generator;
  ByteArrayOutputStream out = new ByteArrayOutputStream();
  private ClassMunger classMunger = new ClassMunger(new JavaClassRepository());
  HypotheticalCostModel costModel = new HypotheticalCostModel(new CostModel(), classMunger, null);

  public void testExample() throws Exception {
    IssuesReporter reporter = new IssuesReporter(new LinkedList<ClassIssues>(), costModel);
    ReportModel model = new AboutTestabilityReport(reporter, new SourceLoader(null) {
      @Override
      public Source load(String name) {
        return new Source(asList(
            new Line(1, "Copyright garbage!"),
            new Line(2, "package com.google.test.metric.example;"),
            new Line(3, "import java.util.List;"),
            new Line(4, "  "),
            new Line(5, "class SumOfPrimes {"),
            new Line(6, "  public void sum() {}"),
            new Line(7, "}")));
      }
    });
    Configuration configuration = new Configuration();
    configuration.setTemplateLoader(new ClassPathTemplateLoader(ReportGeneratorProvider.PREFIX));
    BeansWrapper objectWrapper = new DefaultObjectWrapper();
    configuration.setObjectWrapper(objectWrapper);
    ResourceBundleModel bundleModel = new ResourceBundleModel(getBundle("messages"), objectWrapper);
    model.setMessageBundle(bundleModel);
    generator = new FreemarkerReportGenerator(model, new PrintStream(out),
            "about/Report.html", configuration);
    generator.printHeader();
    generator.addClassCost(new ClassCost("com.google.test.metric.example.Lessons.SumOfPrimes1",
        asList(new MethodCost("", "foo()", 1, false, false, false))));
    generator.printFooter();

    String text = out.toString();
    assertTrue(text, text.contains(">SumOfPrimes1<"));
    assertTrue(text, text.contains(">Lessons<"));
    assertTrue(text, text.contains("sum"));
    assertFalse(text, text.contains("Copyright"));
    assertFalse(text, text.contains("package com.google"));
    assertFalse(text, text.contains("import java.util"));
    assertFalse(text, text.contains("<span class=\"nocode\">4:"));
  }
}
