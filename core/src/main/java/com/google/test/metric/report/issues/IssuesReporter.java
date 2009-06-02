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

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.GlobalCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvokationCost;
import com.google.test.metric.ViolationCost;
import static com.google.test.metric.report.issues.IssueSubType.COMPLEXITY;
import static com.google.test.metric.report.issues.IssueSubType.NON_MOCKABLE;
import static com.google.test.metric.report.issues.IssueSubType.SINGLETON;
import static com.google.test.metric.report.issues.IssueSubType.STATIC_METHOD;
import static com.google.test.metric.report.issues.IssueType.COLLABORATOR;
import static com.google.test.metric.report.issues.IssueType.CONSTRUCTION;
import static com.google.test.metric.report.issues.IssueType.DIRECT_COST;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Logger;

/**
 * Looks at ClassCost models and infers the coding issue that incurred the cost.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IssuesReporter {

  private static final Logger logger = Logger.getLogger(IssuesReporter.class.getCanonicalName());
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
    int cost = costModel.computeClass(classCost);
    ClassIssues classIssues = new ClassIssues(classCost.getClassName(), cost);
    for (MethodCost methodCost : classCost.getMethods()) {
      addIssuesInMethod(classIssues, methodCost, classCost);
    }
    if (classIssues.isEmpty() && cost > 100) {
      logger.warning(String.format("No issues found in class %s which has a cost of %d",
          classCost.getClassName(), cost));
    }
    return classIssues;
  }

  void addIssuesInMethod(ClassIssues classIssues, MethodCost methodCost, ClassCost classCost) {
    if (methodCost.getViolationCosts() == null || methodCost.getViolationCosts().isEmpty()) {
      // no issues to add
      return;
    }
    boolean issuesFound = false;

    for (ViolationCost violationCost : methodCost.getViolationCosts()) {
      if (violationCost instanceof MethodInvokationCost) {
        MethodInvokationCost invokationCost = (MethodInvokationCost) violationCost;
        issuesFound = addMethodInvocationIssues(classIssues, methodCost, classCost, issuesFound,
                invokationCost);
      }
    }
    if (!issuesFound) {
      if (methodCost.getDirectCost().getCyclomaticComplexityCost() > 0) {
        addDirectCostIssue(classIssues, methodCost, classCost);
      }
    }
  }

  private void addDirectCostIssue(ClassIssues classIssues, MethodCost methodCost,
                                  ClassCost classCost) {
    Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost);
    float contributionToClassCost = costModel.computeDirectCostContributionFromMethod(classCost, methodCost);
    if (methodCost.isConstructor()) {
      issue.setContributionToClassCost(contributionToClassCost);
      issue.setType(CONSTRUCTION);
      issue.setSubType(COMPLEXITY);
    } else {
      issue.setLineNumberIsApproximate(true);
      issue.setContributionToClassCost(contributionToClassCost);
      issue.setType(DIRECT_COST);
      issue.setSubType(COMPLEXITY);
    }
    classIssues.add(issue);
  }

  private boolean addMethodInvocationIssues(ClassIssues classIssues, MethodCost methodCost,
                                            ClassCost classCost, boolean issuesFound,
                                            MethodInvokationCost invokationCost) {
    Issue issue = new Issue(invokationCost.getLineNumber(),
        invokationCost.getMethodCost());
    boolean isStatic = invokationCost.getMethodCost().isStatic();
    boolean isGlobal = hasGlobalSource(invokationCost.getMethodCost());

    issue.setContributionToClassCost(costModel.computeContributionFromIssue(classCost, methodCost, invokationCost));
    if (methodCost.isConstructor()) {
      issue.setType(CONSTRUCTION);
    } else {
      issue.setType(COLLABORATOR);
      issuesFound = true;
    }
    if (isStatic) {
      issue.setSubType(STATIC_METHOD);
    } else {
      if (isGlobal) {
        issue.setSubType(SINGLETON);
      } else {
        issue.setSubType(NON_MOCKABLE);
      }
    }
    classIssues.add(issue);
    return issuesFound;
  }

  private boolean hasGlobalSource(MethodCost methodCost) {
    for (ViolationCost cost : methodCost.getViolationCosts()) {
      if (cost instanceof GlobalCost) {
        return true;
      }
    }
    return false;
  }

  public List<ClassIssues> getMostImportantIssues() {
    if (mostImportantIssues instanceof TriageIssuesQueue) {
      return ((TriageIssuesQueue<ClassIssues>) mostImportantIssues).asList();
    }
    return new ArrayList<ClassIssues>(mostImportantIssues);
  }
}
