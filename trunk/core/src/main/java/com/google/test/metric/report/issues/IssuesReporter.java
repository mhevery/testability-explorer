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
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvokationCost;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
        Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName());
        classIssues.getConstructionIssues().workInConstructor(methodCost, issue,
            classCost.getTotalComplexityCost(), classCost.getTotalGlobalCost());
      } else if (hasStaticMethodSource(methodCost) ) {
        Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName());
        classIssues.getCollaboratorIssues().staticMethodCalled(methodCost, issue,
            classCost.getTotalComplexityCost(), classCost.getTotalGlobalCost());
      } else {
        Issue issue = new Issue(methodCost.getMethodLineNumber(), methodCost.getMethodName());
        classIssues.getCollaboratorIssues().nonMockableMethodCalled(methodCost, issue,
            classCost.getTotalComplexityCost(), classCost.getTotalGlobalCost());
      }
    }
    return classIssues;
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
