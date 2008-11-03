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
import java.util.Date;

import junit.framework.TestCase;

import com.google.classpath.ClassPath;
import com.google.classpath.DirectoryClassPath;
import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.CostModel;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.Testability;
import com.google.test.metric.WeightedAverage;

public class ClassSourceReportTest extends TestCase {

  private final ClassPath classPath = new DirectoryClassPath(new File("src"));
  GradeCategories grades = new GradeCategories(50, 100);
  File out = new File("test-out");
  SourceReport report = new SourceReport(grades, new SourceLoader(classPath),
      out, new Date());
  ClassRepository repo = new JavaClassRepository();
  MetricComputer computer = new MetricComputer(repo, null, new RegExpWhiteList(
      "!com.google"), new CostModel());
  ClassCost classCost = computer.compute(repo.getClass(Testability.class
      .getName()));

  @Override
  protected void setUp() throws Exception {
    report.printHeader();
  }

  public void testDumpClassToHtmlFile() throws Exception {
    ClassReport classReport = report.createClassReport(classCost);
    report.write("Class.html", classReport, new File(out, "Class.html"));
  }

  public void testDumpPackageToHtmlFile() throws Exception {
    PackageReport packageReport = new PackageReport(Testability.class
        .getPackage().getName(), grades, new WeightedAverage());
    packageReport.addClass("a.b.C", 30);
    packageReport.addClass("a.b.D", 80);
    packageReport.addClass("a.b.E", 130);
    report.write("Package.html", packageReport, new File(out, "Package.html"));
  }

  public void testDumpProjectToHtmlFile() throws Exception {
    ProjectReport packages = new ProjectReport("", grades,
        new WeightedAverage());
    packages.addPackage("a.b.c", 1);
    packages.addPackage("a.b.d", 51);
    packages.addPackage("a.b.e", 101);
    ProjectReport classes = new ProjectReport(Testability.class.getPackage()
        .getName(), grades, new WeightedAverage());
    classes.addClass("a.b.C", 30);
    classes.addClass("a.b.D", 80);
    classes.addClass("a.b.E", 130);
    classes.addClass("a.b.F", 13);
    classes.addClass("a.b.G", 10);
    classes.addClass("a.b.H", 3);
    report.write("Project.html", new ProjectSummaryReport(classes, packages),
        new File(out, "Project.html"));
  }

}