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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.ViolationCost;
import com.google.test.metric.WeightedAverage;
import com.google.test.metric.report.Source.Line;

import freemarker.ext.beans.BeanModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.SimpleNumber;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class SourceReportGenerator implements ReportGenerator {

  private class PrintCostMethod implements TemplateMethodModelEx {
    @SuppressWarnings("unchecked")
    public Object exec(List arguments) throws TemplateModelException {
      TemplateModel model = (TemplateModel) arguments.get(0);
      if (model instanceof SimpleNumber) {
        SimpleNumber number = (SimpleNumber) model;
        return "" + number;
      } else if (model instanceof BeanModel) {
        BeanModel arg0 = (BeanModel) model;
        Cost cost = (Cost) arg0.getAdaptedObject(Cost.class);
        return "Cost: " + costModel.computeOverall(cost) + " [" + cost + "]";
      } else {
        throw new IllegalStateException();
      }
    }
  }

  private class OverallCostMethod implements TemplateMethodModelEx {
    @SuppressWarnings("unchecked")
    public Object exec(List arguments) throws TemplateModelException {
      TemplateModel model = (TemplateModel) arguments.get(0);
      if (model instanceof SimpleNumber) {
        SimpleNumber number = (SimpleNumber) model;
        return number;
      } else if (model instanceof BeanModel) {
        BeanModel arg0 = (BeanModel) model;
        Cost cost = (Cost) arg0.getAdaptedObject(Cost.class);
        return costModel.computeOverall(cost);
      } else {
        throw new IllegalStateException();
      }
    }
  }

  private final String PREFIX = "com/google/test/metric/report/source/";
  private final SourceLoader sourceLoader;
  private final GradeCategories grades;
  private final File directory;
  private final Configuration cfg;
  private final Map<String, PackageReport> packageReports = new HashMap<String, PackageReport>();
  private final ProjectReport projectByClassReport;
  private final ProjectReport projectByPackageReport;
  private final CostModel costModel;

  public SourceReportGenerator(GradeCategories grades, SourceLoader sourceLoader,
      File outputDirectory, CostModel costModel, Date currentTime,
      int worstCount, Configuration cfg) {
    this.grades = grades;
    this.sourceLoader = sourceLoader;
    this.directory = outputDirectory;
    this.costModel = costModel;
    this.cfg = cfg;
    cfg.setTemplateLoader(new ClassPathTemplateLoader(PREFIX));
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    try {
      cfg.setSharedVariable("maxExcellentCost", grades.getMaxExcellentCost());
      cfg.setSharedVariable("maxAcceptableCost", grades.getMaxAcceptableCost());
      cfg.setSharedVariable("currentTime", currentTime);
      cfg.setSharedVariable("computeOverallCost", new OverallCostMethod());
      cfg.setSharedVariable("printCost", new PrintCostMethod());
    } catch (TemplateModelException e) {
      throw new RuntimeException(e);
    }
    projectByClassReport = new ProjectReport("index", grades,
        new WeightedAverage());
    projectByClassReport.setMaxUnitCosts(worstCount);
    projectByPackageReport = new ProjectReport("index", grades,
        new WeightedAverage());
  }

  public void printHeader() {
    directory.mkdirs();
    writeCSS();
  }

  private void writeCSS() {
    try {
      InputStream is = getClass().getResourceAsStream("source/te.css");
      OutputStream os = new FileOutputStream(new File(directory, "te.css"));
      int size;
      byte[] buf = new byte[2048];
      while ((size = is.read(buf)) > 0) {
        os.write(buf, 0, size);
      }
      os.close();
      is.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void printFooter() {
    for (PackageReport packageReport : packageReports.values()) {
      projectByPackageReport.addPackage(packageReport.getName(), packageReport
          .getOverallCost());
      write("Package.html", packageReport, "package_");
    }
    write("Project.html", new ProjectSummaryReport(projectByClassReport,
        projectByPackageReport), new File(directory, "index.html"));
  }

  public void addClassCost(ClassCost classCost) {
    ClassReport classReport = createClassReport(classCost);
    write("Class.html", classReport, "class_");
    String packageName = classCost.getPackageName();
    PackageReport packageReport = packageReports.get(packageName);
    if (packageReport == null) {
      packageReport = new PackageReport(packageName, grades,
          new WeightedAverage());
      packageReports.put(packageName, packageReport);
    }
    packageReport.addClass(classCost.getClassName(), costModel
        .computeClass(classCost));
  }

  public void write(String templateName, SummaryGraphReport<?> report,
      String prefix) {
    File file = new File(directory, prefix + report.getName() + ".html");
    write(templateName, report, file);
  }

  public void write(String templateName, Object report, File file) {
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
        grades, new WeightedAverage(
            CostModel.WEIGHT_TO_EMPHASIZE_EXPENSIVE_METHODS));
    for (MethodCost method : classCost.getMethods()) {
      int overallCost = costModel.computeOverall(method.getTotalCost());
      classReport.addMethod(method.getMethodName(), method
          .getMethodLineNumber(), overallCost, method.getTotalCost(), method
          .getCost());
      Line line = source.getLine(method.getMethodLineNumber());
      line.addMethodCost(method);
      for (ViolationCost violation : method.getExplicitViolationCosts()) {
        line = source.getLine(violation.getLocation().getLineNumber());
        line.addCost(violation.getCost());
      }
    }
    projectByClassReport.addClass(classCost.getClassName(), classReport
        .getOverallCost());
    return classReport;
  }
}
