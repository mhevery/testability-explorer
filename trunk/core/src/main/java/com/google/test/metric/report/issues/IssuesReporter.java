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
      if (methodCost.isConstructor()) {
        for (Issue issue : getUnderlyingIssues(methodCost)) {
          classIssues.getConstructionIssues().workInConstructor(methodCost, issue,
              classCost.getTotalComplexityCost(), classCost.getTotalGlobalCost());
        }
      } else if (hasStaticMethodSource(methodCost) ) {
        for (Issue issue : getUnderlyingIssues(methodCost)) {
          classIssues.getCollaboratorIssues().staticMethodCalled(methodCost, issue,
              classCost.getTotalComplexityCost(), classCost.getTotalGlobalCost());
        }
      } else {
        Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName());
        classIssues.getCollaboratorIssues().nonMockableMethodCalled(methodCost, issue,
            classCost.getTotalComplexityCost(), classCost.getTotalGlobalCost());
      }
    }
    return classIssues;
  }

  private List<Issue> getUnderlyingIssues(MethodCost methodCost) {
    List<Issue> issues = new LinkedList<Issue>();
    if (methodCost.getViolationCosts() == null || methodCost.getViolationCosts().isEmpty()) {
      return Collections.emptyList();
    }
    for (ViolationCost violationCost : methodCost.getViolationCosts()) {
      if (violationCost instanceof MethodInvokationCost) {
        MethodInvokationCost invokationCost = (MethodInvokationCost) violationCost;
        issues.add(new Issue(invokationCost.getLineNumber(), invokationCost.getDescription()));
      }
    }
    if (issues.isEmpty()) {
      if (methodCost.getDirectCost().getCyclomaticComplexityCost() > 0) {
        issues.add(new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName()));
      }
    }
    return issues;
  }

  /**
   * Is this methodCost a result of calling a static method?
   */
  boolean hasStaticMethodSource(MethodCost methodCost) {
    if (methodCost.getViolationCosts() == null || methodCost.getViolationCosts().isEmpty()) {
      return false;
    }
    if (methodCost.getViolationCosts().get(0) instanceof MethodInvokationCost) {
      MethodInvokationCost invokationCost = (MethodInvokationCost) methodCost.getViolationCosts().get(0);
      return invokationCost.getMethodCost().isStatic();
    }
    return false;
  }


  public List<ClassIssues> getMostImportantIssues() {
    if (mostImportantIssues instanceof TriageIssuesQueue) {
      return ((TriageIssuesQueue)mostImportantIssues).asList();
    }
    return new ArrayList(mostImportantIssues);
  }
}
