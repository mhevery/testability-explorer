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
package com.google.test.metric.report.html;

import com.google.test.metric.ClassCost;
import com.google.test.metric.report.FreemarkerReportGenerator;
import com.google.test.metric.report.SourceLinker;
import com.google.test.metric.report.issues.IssuesReporter;

import java.io.PrintStream;

/**
 * Does the extra work of assessing issues for the HTML report.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HtmlReportGenerator extends FreemarkerReportGenerator {
  private static final String TEMPLATE = "html/Report.html";

  private final IssuesReporter issuesReporter;

  public HtmlReportGenerator(HtmlReport model, PrintStream out, IssuesReporter issuesReporter, SourceLinker linker) {
    super(model, out, linker, TEMPLATE);
    this.issuesReporter = issuesReporter;
  }

  @Override
  public void addClassCost(ClassCost classCost) {
    super.addClassCost(classCost);
    issuesReporter.inspectClass(classCost);
  }
}
