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
package com.google.test.metric.report;

import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import com.google.test.metric.ClassCost;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

/**
 * The base class for ReportGenerator's that use Freemarker for rendering.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class FreemarkerReportGenerator implements ReportGenerator {
  public static final String HTML_REPORT_TEMPLATE = "html/Report.html";
  private final ReportModel model;
  private final PrintStream out;
  private final Configuration cfg;
  private final String templateFile;

  public FreemarkerReportGenerator(ReportModel model, PrintStream out,
                                   String templateFile, Configuration cfg) {
    this.model = model;
    this.out = out;
    this.cfg = cfg;
    this.templateFile = templateFile;
  }

  public void renderReport(PrintStream out) throws IOException {
    try {
      cfg.getTemplate(templateFile).process(model, new PrintWriter(out));
    } catch (TemplateException e) {
      throw new RuntimeException(e);
    }
  }

  public void printHeader() throws IOException {
    // do nothing
  }

  public void addClassCost(ClassCost classCost) {
    model.addClassCost(classCost);
  }

  public void printFooter() throws IOException {
    renderReport(out);
  }
}
