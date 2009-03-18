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
package com.google.test.metric.report.issues;

import com.google.test.metric.MethodCost;
import static com.google.test.metric.collection.LazyHashMap.newLazyHashMap;
import com.google.test.metric.report.issues.Issue.CollaboratorType;
import static com.google.test.metric.report.issues.Issue.CollaboratorType.NEW_OPERATOR;
import static com.google.test.metric.report.issues.Issue.CollaboratorType.STATIC_METHOD;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Issues that arise from non-mockable use of collaborators.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class CollaboratorIssues implements IssuesCategory {
  final Map<CollaboratorType, List<Issue>> issues;

  public CollaboratorIssues() {
    this(newLazyHashMap(new HashMap<CollaboratorType, List<Issue>>(), new IssuesListFactory()));
  }

  public CollaboratorIssues(Map<CollaboratorType, List<Issue>> issues) {
    this.issues = issues;
  }

  public boolean isEmpty() {
    return (!issues.containsKey(NEW_OPERATOR) || issues.get(NEW_OPERATOR).isEmpty()) &&
        (!issues.containsKey(STATIC_METHOD) || issues.get(STATIC_METHOD).isEmpty());
  }

  public Enum[] getTypes() {
    return CollaboratorType.values();
  }

  public List<Issue> getIssuesOfType(String type) {
    CollaboratorType key = CollaboratorType.valueOf(type);
    if (issues.containsKey(key)) {
      return issues.get(key);
    }
    return Collections.emptyList();
  }

  public String getName() {
    return "Collaborators";
  }

  public List<Issue> getNewOperatorIssues() {
    return issues.get(NEW_OPERATOR);
  }

  public List<Issue> getStaticMethodIssues() {
    return issues.get(STATIC_METHOD);
  }

  public void nonMockableMethodCalled(MethodCost methodCost, Issue issue, long totalComplexityCost, long totalGlobalCost) {
    issue.setContributionToClassCost(
            methodCost.getDependantCost().getCyclomaticComplexityCost() / (float) totalComplexityCost);
    issues.get(NEW_OPERATOR).add(issue);
  }

  public void staticMethodCalled(MethodCost methodCost, Issue issue, long totalComplexityCost, long totalGlobalCost) {
    issue.setContributionToClassCost(
            methodCost.getDependantCost().getCyclomaticComplexityCost() / (float) totalComplexityCost);
    issues.get(STATIC_METHOD).add(issue);
  }
}
