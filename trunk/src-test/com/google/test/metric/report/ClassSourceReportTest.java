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

public class ClassSourceReportTest extends TestCase {

  private final ClassPath classPath = new DirectoryClassPath(new File("src"));
  private final String prefix = "com/google/test/metric/report/source/";
  private final int maxExcellentCost = 50;
  private final int maxAcceptableCost = 100;
  GradeCategories grades = new GradeCategories(maxExcellentCost,
      maxAcceptableCost);
  SourceReport report = new SourceReport(grades, new SourceLoader(classPath),
      new File("self-report"));
  ClassRepository repo = new JavaClassRepository();
  MetricComputer computer = new MetricComputer(repo, null, new RegExpWhiteList(
      "!com.google"), new CostModel());
  ClassCost classCost = computer.compute(repo.getClass(Testability.class
      .getName()));

  public void testDumpClassToHtmlFile() throws Exception {
    ClassReport classReport = report.createClassReport(classCost);
    report.write("Class.html", classReport, new File("Class.html"));
  }

  public void testDumpPackageToHtmlFile() throws Exception {
    PackageReport packageReport = new PackageReport(Testability.class
        .getPackage().getName(), grades);
    packageReport.addClass("a.b.C", 30);
    packageReport.addClass("a.b.D", 80);
    packageReport.addClass("a.b.E", 130);
    report.write("Package.html", packageReport, new File("Package.html"));
  }

  public void testDumpProjectToHtmlFile() throws Exception {
    ProjectReport projectReport = new ProjectReport("", grades);
    projectReport.addProject("a.b.c", 30);
    projectReport.addProject("a.b.d", 80);
    projectReport.addProject("a.b.e", 130);
    report.write("Project.html", projectReport, new File("Project.html"));
  }

}