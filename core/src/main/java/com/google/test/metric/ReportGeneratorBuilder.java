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
package com.google.test.metric;

import com.google.classpath.ClassPath;
import com.google.test.metric.report.*;
import com.google.test.metric.report.about.AboutTestabilityReport;
import com.google.test.metric.report.html.HtmlReportModel;
import com.google.test.metric.report.html.SourceLinkerModel;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;
import com.google.test.metric.report.issues.TriageIssuesQueue;

import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;
import static java.util.ResourceBundle.getBundle;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ResourceBundleModel;

/**
 * Builds a Report, using various formats, and given all of the needed options.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ReportGeneratorBuilder {
  private final ClassPath classPath;
  private final ReportOptions options;
  private final ReportFormat printer;
  private final PrintStream out;
  private final List<String> entryList;
  public static final String PREFIX = "com/google/test/metric/report/";

  public ReportGeneratorBuilder(ClassPath classPath, ReportOptions options, ReportFormat printer,
                              PrintStream out, List<String> entryList) {
    this.classPath = classPath;
    this.options = options;
    this.printer = printer;
    this.out = out;
    this.entryList = entryList;
  }

  public enum ReportFormat {
    summary,
    detail,
    xml,
    props,
    source,
    html,
    about
  }
  
  /**
   * Method to allow retaining a handle on preconfigured model objects.
   * 
   * @param costModel Cost Model for the {@link ReportGenerator}
   * @param reportModel Can be {@code null} if {@link ReportFormat} is not
   *    {@link ReportFormat#html} or {@link ReportFormat#about} 
   * @param sourceLoader Source Loader used by {@link ReportFormat#source} reports.
   * @return a ready to use {@link ReportGenerator}
   */
  public ReportGenerator build(CostModel costModel, ReportModel reportModel,
      SourceLoader sourceLoader) {
    SourceLinker linker = new SourceLinker(
        options.getSrcFileLineUrl(), options.getSrcFileUrl());
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(new ClassPathTemplateLoader(PREFIX));
    BeansWrapper objectWrapper = new DefaultObjectWrapper();
    cfg.setObjectWrapper(objectWrapper);
    ResourceBundleModel bundleModel = new ResourceBundleModel(getBundle("messages"),
        objectWrapper);

    ReportGenerator report;
    switch (printer) {
      case summary:
        report = new TextReportGenerator(out, costModel, options);
        break;
      case html:
        reportModel.setMessageBundle(bundleModel);
        reportModel.setSourceLinker(new SourceLinkerModel(linker));
        report = new FreemarkerReportGenerator(reportModel, out,
            FreemarkerReportGenerator.HTML_REPORT_TEMPLATE, cfg);
        break;
      case detail:
        report = new DrillDownReportGenerator(out, costModel, entryList,
            options.getPrintDepth(), options.getMinCost());
        break;
      case props:
        report = new PropertiesReportGenerator(out, costModel);
        break;
      case source:
        GradeCategories gradeCategories = new GradeCategories(options.getMaxExcellentCost(),
            options.getMaxAcceptableCost());
        report = new SourceReportGenerator(gradeCategories, sourceLoader,
            new File("te-report"), costModel, new Date(), options.getWorstOffenderCount(), cfg);
        break;
      case xml:
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setOutputByteStream(out);
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        xmlSerializer.setOutputFormat(format);
        report = new XMLReportGenerator(xmlSerializer, costModel, options);
        break;
      case about:
        reportModel.setMessageBundle(bundleModel);
        reportModel.setSourceLinker(new SourceLinkerModel(linker));
        report = new FreemarkerReportGenerator(reportModel, out, "about/Report.html", cfg);
        break;
      default:
        throw new IllegalStateException("Unknown report format " + printer);
    }
    return report;
  }

  public ReportGenerator build() {
    CostModel costModel = new CostModel(options.getCyclomaticMultiplier(),
        options.getGlobalMultiplier());
    TriageIssuesQueue<ClassIssues> mostImportantIssues =
        new TriageIssuesQueue<ClassIssues>(options.getMaxExcellentCost(),
            options.getWorstOffenderCount(), new ClassIssues.TotalCostComparator());
    SourceLoader sourceLoader = new SourceLoader(classPath);

    IssuesReporter issuesReporter = new IssuesReporter(mostImportantIssues, costModel);
    AnalysisModel analysisModel = new AnalysisModel(issuesReporter);    
    ReportModel reportModel;

    switch (printer) {
      case html:
        reportModel = new HtmlReportModel(costModel, analysisModel, options);
        break;
        
      case about:
        reportModel = new AboutTestabilityReport(issuesReporter, sourceLoader);
        break;

      default:
        reportModel = null;
    }
    return build(costModel, reportModel, sourceLoader);
  }
}
