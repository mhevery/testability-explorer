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

import com.google.test.metric.report.issues.Issue.ConstructionType;
import static com.google.test.metric.report.issues.Issue.ConstructionType.*;
import static com.google.test.metric.collection.LazyHashMap.newLazyHashMap;
import com.google.test.metric.MethodCost;
import com.google.test.metric.ViolationCost;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.MethodInvokationCost;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Issues that arise from expensive constructors.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ConstructionIssues implements IssuesCategory {
  private final Map<ConstructionType, List<Issue>> issues;

  public ConstructionIssues() {
    this(newLazyHashMap(new HashMap<ConstructionType, List<Issue>>(), new IssuesListFactory()));
  }

  /* For Testing */
  public ConstructionIssues(Map<ConstructionType, List<Issue>> issues) {
    this.issues = issues;
  }

  public void workInConstructor(MethodCost methodCost, long totalComplexityCost, long totalGlobalCost) {
    long complexityCost = 0, staticCost = 0, nonMockableCost = 0;
    for (ViolationCost costSource : methodCost.getViolationCosts()) {
      if (costSource instanceof CyclomaticCost) {
        complexityCost += costSource.getCost().getCyclomaticComplexityCost();
      }
      if (costSource instanceof MethodInvokationCost) {
        if (((MethodInvokationCost)costSource).getMethodCost().isStatic()) {
          staticCost += costSource.getCost().getCyclomaticComplexityCost();
        } else {
          nonMockableCost += costSource.getCost().getCyclomaticComplexityCost();
        }
      }
    }
    if (staticCost > 0) {
      float contribution = staticCost / (float)totalComplexityCost;
      Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName(), contribution);
      issues.get(STATIC_METHOD).add(issue);
    }
    if (nonMockableCost > 0) {
      float contribution = nonMockableCost / (float)totalComplexityCost;
      Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName(), contribution);
      issues.get(NEW_OPERATOR).add(issue);
    }
    if (complexityCost > 0) {
      float contribution = complexityCost / (float)totalComplexityCost;
      Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName(), contribution);
      issues.get(COMPLEXITY).add(issue);
    }
  }

  public Enum[] getTypes() {
    return ConstructionType.values();
  }

  public List<Issue> getIssuesOfType(String type) {
    ConstructionType key = ConstructionType.valueOf(type);
    if (issues.containsKey(key)) {
      return issues.get(key);
    }
    return Collections.emptyList();
  }

  public String getName() {
    return "Construction";
  }

  public boolean isEmpty() {
    return (!issues.containsKey(COMPLEXITY) || issues.get(COMPLEXITY).isEmpty()) &&
        (!issues.containsKey(STATIC_METHOD) || issues.get(STATIC_METHOD).isEmpty()) &&
        (!issues.containsKey(NEW_OPERATOR) || issues.get(NEW_OPERATOR).isEmpty());

  }

  public List<Issue> getComplexityIssues() {
    return issues.get(COMPLEXITY);
  }

  public List<Issue> getStaticMethodIssues() {
    return issues.get(STATIC_METHOD);
  }

  public List<Issue> getNewOperatorIssues() {
    return issues.get(NEW_OPERATOR);
  }
}
