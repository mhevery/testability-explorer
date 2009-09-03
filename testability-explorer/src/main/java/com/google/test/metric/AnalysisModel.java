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
package com.google.test.metric;

import java.util.ArrayList;
import java.util.List;

import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.IssuesReporter;

/**
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class AnalysisModel {
  private List<ClassCost> classCosts = new ArrayList<ClassCost>();
  private final IssuesReporter issuesReporter;
  
  /**
   * @param issuesReporter Can be {@code null}. 
   */
  public AnalysisModel(IssuesReporter issuesReporter) {
    this.issuesReporter = issuesReporter;
  }

  public void addClassCost(ClassCost classCost) {
    classCosts.add(classCost);
    if (issuesReporter != null) {
      issuesReporter.inspectClass(classCost);
    }
  }

  public Iterable<ClassCost> getClassCosts() {
    return classCosts;
  }
  
  /**
   * Can only be called if a non {@code null} {@link IssuesReporter} was passed
   * to the constructor
   */
  public List<ClassIssues> getWorstOffenders() {
    return issuesReporter.getMostImportantIssues();
  }
}
