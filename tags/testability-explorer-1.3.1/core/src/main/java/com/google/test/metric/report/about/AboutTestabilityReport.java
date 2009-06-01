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
import com.google.test.metric.collection.LazyHashMap;
import com.google.test.metric.report.ReportModel;
import com.google.test.metric.report.Source;
import com.google.test.metric.report.SourceLoader;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;
import com.google.common.base.Supplier;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A report which shows classes issues, their name, and their source code.
 * 
 * @author alexeagle@google.com (Alex Eagle)
 */
public class AboutTestabilityReport extends ReportModel {
  private final IssuesReporter issuesReporter;
  private final SourceLoader sourceLoader;
  private final String PACKAGE_PREFIX = "com.google.test.metric.example.";
  private final Map<String, List<ClassModel>> classesByPackage =
      LazyHashMap.newLazyHashMap(new Supplier<List<ClassModel>>() {
        public List<ClassModel> get() {
          return new LinkedList<ClassModel>();
        }
      });

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
    String displayName = className;
    if (displayName.startsWith(PACKAGE_PREFIX)) {
      displayName = displayName.substring(PACKAGE_PREFIX.length());
    }
    int indexOfLastPackageSeparator = displayName.lastIndexOf(".");
    String packageName = displayName.substring(0, indexOfLastPackageSeparator);
    String displayClassName = displayName.substring(indexOfLastPackageSeparator + 1);
    classesByPackage.get(packageName).add(new ClassModel(issues, source, displayClassName));
  }

  private boolean isInnerClass(String className) {
    return className.contains("$");
  }

  public Map<String, List<ClassModel>> getClassesByPackage() {
    return classesByPackage;
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
