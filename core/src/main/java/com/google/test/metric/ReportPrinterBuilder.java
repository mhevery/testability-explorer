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
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

import java.io.File;
import java.io.PrintStream;
import java.util.Date;
import java.util.List;

/**
 * Builds a Report, using various formats, and given all of the needed options.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ReportPrinterBuilder {
  private final ClassPath classPath;
  private final ReportOptions options;
  private final ReportFormat printer;
  private final PrintStream out;
  private final List<String> entryList;

  public ReportPrinterBuilder(ClassPath classPath, ReportOptions options, ReportFormat printer,
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
    html
  }

  public Report build() {
    Report report;
    CostModel costModel = new CostModel(options.getCyclomaticMultiplier(),
        options.getGlobalMultiplier());

    switch (printer) {
      case summary:
        report = new TextReport(out, costModel, options);
        break;
      case html:
        SourceLinker linker = new SourceLinker(
            options.getSrcFileLineUrl(), options.getSrcFileUrl());
        DetailHtmlReport detailHtmlReport = new DetailHtmlReport(out, costModel, linker,
            options.getMaxMethodCount(), options.getMaxLineCount());
        report = new HtmlReport(out, costModel, options, detailHtmlReport);
        break;
      case detail:
        report = new DrillDownReport(out, costModel, entryList,
            options.getPrintDepth(), options.getMinCost());
        break;
      case props:
        report = new PropertiesReport(out, costModel);
        break;
      case source:
        GradeCategories gradeCategories = new GradeCategories(options.getMaxExcellentCost(),
            options.getMaxAcceptableCost());
        SourceLoader sourceLoader = new SourceLoader(classPath);
        report = new SourceReport(gradeCategories, sourceLoader, new File("te-report"), costModel,
            new Date(), options.getWorstOffenderCount());
        break;
      case xml:
        XMLSerializer xmlSerializer = new XMLSerializer();
        xmlSerializer.setOutputByteStream(out);
        OutputFormat format = new OutputFormat();
        format.setIndenting(true);
        xmlSerializer.setOutputFormat(format);
        report = new XMLReport(xmlSerializer, costModel, options);
        break;
      default:
        throw new IllegalStateException("Unknown report format " + printer);
    }
    return report;
  }
}
