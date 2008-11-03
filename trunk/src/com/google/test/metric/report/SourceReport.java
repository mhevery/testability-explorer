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
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.classpath.ClassPathFactory;
import com.google.test.metric.ClassCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.ViolationCost;
import com.google.test.metric.report.Source.Line;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

public class SourceReport implements Report {

  private final String PREFIX = "com/google/test/metric/report/source/";
  private final SourceLoader sourceLoader;
  private final GradeCategories grades;
  private final File directory;
  private final Configuration cfg;
  private ProjectReport projectReport;
  private final Map<String, PackageReport> packageReports = new HashMap<String, PackageReport>();

  public SourceReport(GradeCategories grades, SourceLoader sourceLoader,
      File outputDirectory) {
    this.grades = grades;
    this.sourceLoader = sourceLoader;
    this.directory = outputDirectory;
    cfg = new Configuration();
    cfg.setTemplateLoader(new ClassPathTemplateLoader(new ClassPathFactory()
        .createFromJVM(), PREFIX));
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    try {
      cfg.setSharedVariable("maxExcellentCost", grades.getMaxExcellentCost());
      cfg.setSharedVariable("maxAcceptableCost", grades.getMaxAcceptableCost());
    } catch (TemplateModelException e) {
      throw new RuntimeException(e);
    }
  }

  public void printHeader() {
    directory.mkdirs();
  }

  public void printFooter() {
    projectReport = new ProjectReport("index", grades);
    for (PackageReport packageReport : packageReports.values()) {
      write("Package.html", packageReport);
    }
    write("Project.html", projectReport);
  }

  public void addClassCost(ClassCost classCost) {
    ClassReport classReport = createClassReport(classCost);
    write("Class.html", classReport);
    String packageName = classCost.getPackageName();
    PackageReport packageReport = packageReports.get(packageName);
    if (packageReport == null) {
      packageReport = new PackageReport(packageName, grades);
      packageReports.put(packageName, packageReport);
    }
    packageReport
        .addClass(classCost.getClassName(), classCost.getOverallCost());
  }

  public void write(String templateName, SummaryGraphReport<?> report) {
    File file = new File(directory, report.getName() + ".html");
    write(templateName, report, file);
  }

  public void write(String templateName, SummaryGraphReport<?> report, File file) {
    try {
      Template template = cfg.getTemplate(templateName);
      FileOutputStream os = new FileOutputStream(file);
      OutputStreamWriter out = new OutputStreamWriter(os);
      template.process(report, out);
      out.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    }
  }

  ClassReport createClassReport(ClassCost classCost) {
    Source source = sourceLoader.load(classCost.getClassName());
    ClassReport classReport = new ClassReport(classCost.getClassName(), source,
        grades);
    for (MethodCost method : classCost.getMethods()) {
      classReport.addMethod(method.getMethodName(), method
          .getMethodLineNumber(), method.getTotalCost(), method.getCost());
      Line line = source.getLine(method.getMethodLineNumber());
      line.addMethodCost(method);
      for (ViolationCost violation : method.getViolationCosts()) {
        line = source.getLine(violation.getLineNumber());
        line.addCost(violation.getCost());
      }
    }
    classReport.setOverallCost(classCost.getOverallCost());
    return classReport;
  }
}
