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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.google.classpath.ClassPath;
import com.google.classpath.DirectoryClassPath;
import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.Testability;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class ClassSourceReportTest extends TestCase {

  private final ClassPath classPath = new DirectoryClassPath(new File("src"));
  private final String prefix = "com/google/test/metric/report/source/";
  private final int maxExcellentCost = 50;
  private final int maxAcceptableCost = 100;
  GradeCategories grades = new GradeCategories(maxExcellentCost , maxAcceptableCost );
  SourceReport report = new SourceReport(maxExcellentCost, maxAcceptableCost, 0,
      new SourceLoader(classPath));
  ClassRepository repo = new JavaClassRepository();
  MetricComputer computer = new MetricComputer(repo, null, new RegExpWhiteList(
      "!com.google"), new CostModel());
  ClassCost classCost = computer.compute(repo.getClass(Testability.class
      .getName()));
  Configuration cfg = new Configuration();

  @Override
  protected void setUp() throws Exception {
    cfg.setTemplateLoader(new ClassPathTemplateLoader(classPath, prefix));
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    cfg.setSharedVariable("maxExcellentCost", maxExcellentCost);
    cfg.setSharedVariable("maxAcceptableCost", maxAcceptableCost);
  }

  public void testDumpSourceToHtmlFile() throws Exception {
    ClassReport classReport = report.createClassReport(classCost);
    Template template = cfg.getTemplate("Class.html");
    FileOutputStream os = new FileOutputStream("Class.html");
    OutputStreamWriter out = new OutputStreamWriter(os);

    template.process(classReport, out);
    out.close();
  }

  public void testDumpPackageToHtmlFile() throws Exception {
    List<ClassCost> classCosts = Arrays.asList(
        computer.compute(Testability.class.getName()),
        computer.compute(ClassCost.class.getName()),
        computer.compute(ClassInfo.class.getName()),
        computer.compute(Cost.class.getName())
        );
    PackageReport pacakgeReport = new PackageReport(Testability.class
        .getPackage().getName(), classCosts, grades, new CostModel());
    Template template = cfg.getTemplate("Package.html");
    FileOutputStream os = new FileOutputStream("Package.html");
    OutputStreamWriter out = new OutputStreamWriter(os);

    template.process(pacakgeReport, out);
    out.close();
  }

}