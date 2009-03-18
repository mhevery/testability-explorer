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
import com.google.test.metric.report.ReportModel;
import com.google.test.metric.report.Source;
import com.google.test.metric.report.SourceLoader;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;

import java.util.LinkedList;
import java.util.List;

/**
 * A report which shows classes issues, their name, and their source code.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 */
public class AboutTestabilityReport extends ReportModel {
  private List<ClassModel> classes = new LinkedList<ClassModel>();
  private final IssuesReporter issuesReporter;
  private final SourceLoader sourceLoader;

  public AboutTestabilityReport(IssuesReporter issuesReporter, SourceLoader sourceLoader) {
    this.issuesReporter = issuesReporter;
    this.sourceLoader = sourceLoader;
  }

  public void addClassCost(ClassCost classCost) {
    String className = classCost.getClassName();

    if (isInnerClass(className)) {
      return;
    }
    
    Source source = sourceLoader.load(className);
    if (source.getLines().isEmpty()) {
      throw new IllegalStateException("Failed to load source for class " + className);
    }
    ClassIssues issues = issuesReporter.determineIssues(classCost);
    String displayName = className.substring(className.lastIndexOf(".") + 1);
    classes.add(new ClassModel(issues, source, displayName));
  }

  private boolean isInnerClass(String className) {
    return className.contains("$");
  }

  public List<ClassModel> getClasses() {
    return classes;
  }

  public class ClassModel {
    private final ClassIssues classIssues;
    private final Source source;
    private final String className;

    public ClassModel(ClassIssues classCost, Source source, String className) {
      this.classIssues = classCost;
      this.source = source;
      this.className = className;
    }

    public ClassIssues getIssues() {
      return classIssues;
    }

    public Source getSource() {
      return source;
    }

    public String getName() {
      return className;
    }
  }
}
