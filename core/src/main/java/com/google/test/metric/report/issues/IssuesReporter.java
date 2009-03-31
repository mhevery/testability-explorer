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

import com.google.test.metric.*;

import java.util.*;

/**
 * Looks at ClassCost models and infers the coding issue that incurred the cost.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IssuesReporter {

  private final Queue<ClassIssues> mostImportantIssues;
  private final CostModel costModel;

  public IssuesReporter(Queue<ClassIssues> mostImportantIssues, CostModel costModel) {
    this.mostImportantIssues = mostImportantIssues;
    this.costModel = costModel;
  }

  public void inspectClass(ClassCost classCost) {
    // TODO: no need to determine issues for a class that doesn't get added to the important issues queue
    mostImportantIssues.offer(determineIssues(classCost));
  }

  public ClassIssues determineIssues(ClassCost classCost) {
    ClassIssues classIssues = new ClassIssues(classCost.getClassName(),
        costModel.computeClass(classCost));
    for (MethodCost methodCost : classCost.getMethods()) {
      addIssuesInMethod(classIssues, methodCost, classCost);
    }
    return classIssues;
  }

  void addIssuesInMethod(ClassIssues classIssues, MethodCost methodCost, ClassCost classCost) {
    if (methodCost.getViolationCosts() == null || methodCost.getViolationCosts().isEmpty() ||
        methodCost.isMainMethod()) {
      // no issues to add
      return;
    }
    boolean issuesFound = false;

    for (ViolationCost violationCost : methodCost.getViolationCosts()) {
      if (violationCost instanceof MethodInvokationCost) {
        MethodInvokationCost invokationCost = (MethodInvokationCost) violationCost;
        Issue issue = new Issue(invokationCost.getLineNumber(), invokationCost.getDescription());
        boolean isStatic = invokationCost.getMethodCost().isStatic();
        float contributionToCost =
            invokationCost.getMethodCost().getTotalCost().getCyclomaticComplexityCost() /
            (float) classCost.getTotalComplexityCost();
        issue.setContributionToClassCost(contributionToCost);
        if (methodCost.isConstructor()) {
          issue.setType(IssueType.CONSTRUCTION);
        } else {
          issue.setType(IssueType.COLLABORATOR);
          issuesFound = true;
        }
        if (isStatic) {
          issue.setSubType(IssueSubType.STATIC_METHOD);
        } else {
          issue.setSubType(IssueSubType.NEW_OPERATOR);
        }
        classIssues.add(issue);
      }
    }
    if (!issuesFound) {
      if (methodCost.getDirectCost().getCyclomaticComplexityCost() > 0) {
        Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName());
        float contributionToClassCost = methodCost.getDirectCost().getCyclomaticComplexityCost() /
            (float) classCost.getTotalComplexityCost();
        if (methodCost.isConstructor()) {
          issue.setContributionToClassCost(contributionToClassCost);
          issue.setType(IssueType.CONSTRUCTION);
          issue.setSubType(IssueSubType.COMPLEXITY);
        } else {
          issue.setLineNumberIsApproximate(true);
          issue.setContributionToClassCost(contributionToClassCost);
          issue.setType(IssueType.DIRECT_COST);
          issue.setSubType(IssueSubType.COMPLEXITY);
        }
        classIssues.add(issue);
      }
    }
  }

  /**
   * Is this methodCost a result of calling a static method?
   */
  boolean hasStaticMethodSource(MethodCost methodCost) {
    if (methodCost.getViolationCosts() == null || methodCost.getViolationCosts().isEmpty()) {
      return false;
    }
    if (methodCost.getViolationCosts().get(0) instanceof MethodInvokationCost) {
      MethodInvokationCost invokationCost =
          (MethodInvokationCost) methodCost.getViolationCosts().get(0);
      return invokationCost.getMethodCost().isStatic();
    }
    return false;
  }


  public List<ClassIssues> getMostImportantIssues() {
    if (mostImportantIssues instanceof TriageIssuesQueue) {
      return ((TriageIssuesQueue)mostImportantIssues).asList();
    }
    return new ArrayList<ClassIssues>(mostImportantIssues);
  }
}
