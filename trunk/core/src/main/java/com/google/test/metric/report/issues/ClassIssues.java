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

import java.util.Comparator;

/**
 * Data model for the issues we want to explain in a class.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassIssues {
  private final ConstructionIssues constructionIssues;
  private final DirectCostIssues directCostIssues;
  private final CollaboratorIssues collaboratorIssues;
  private final String className;
  private final Integer totalCost;

  public ClassIssues(String className, Integer totalCost) {
    this(className, totalCost,
        new ConstructionIssues(), new DirectCostIssues(), new CollaboratorIssues());
  }

  public Integer getTotalCost() {
    return totalCost;
  }

  public ClassIssues(String className, Integer totalCost,
                     ConstructionIssues constructionIssues, DirectCostIssues directCostIssues,
                     CollaboratorIssues collaboratorIssues) {
    this.className = className;
    this.totalCost = totalCost;
    this.constructionIssues = constructionIssues;
    this.directCostIssues = directCostIssues;
    this.collaboratorIssues = collaboratorIssues;
  }


  public String getClassName() {
    return className;
  }

  public String getPath() {
    return className.replaceAll("\\.", "/");
  }

  public ConstructionIssues getConstructionIssues() {
    return constructionIssues;
  }

  public DirectCostIssues getDirectCostIssues() {
    return directCostIssues;
  }

  public CollaboratorIssues getCollaboratorIssues() {
    return collaboratorIssues;
  }

  public boolean isEmpty() {
    return getCollaboratorIssues().isEmpty() &&
        getConstructionIssues().isEmpty() &&
        getDirectCostIssues().isEmpty();
  }

  public static class TotalCostComparator implements Comparator<ClassIssues> {
    public int compare(ClassIssues class1Issues, ClassIssues class2Issues) {
      return class1Issues.getTotalCost().compareTo(class2Issues.getTotalCost());
    }
  }
}
