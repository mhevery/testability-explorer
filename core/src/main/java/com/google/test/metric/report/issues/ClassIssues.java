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

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Iterables.filter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import static com.google.test.metric.report.issues.Issue.isType;

import java.util.*;

/**
 * Data model for the issues we want to explain in a class.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassIssues implements IssueHolder {
  private final Queue<Issue> issues;
  private final String className;
  private final Integer totalCost;
  private static final float MIN_PERCENT_TO_DISPLAY = 0.05f;
  private static final int MAX_ISSUES_TO_DISPLAY_PER_CLASS = 20;

  public ClassIssues(String className, Integer totalCost) {
    this(className, totalCost,
        new TriageIssuesQueue<Issue>(MIN_PERCENT_TO_DISPLAY, MAX_ISSUES_TO_DISPLAY_PER_CLASS,
            new Issue.TotalCostComparator()));
  }

  public ClassIssues(String className, Integer totalCost, Queue<Issue> issues) {
    this.className = className;
    this.totalCost = totalCost;
    this.issues = issues;
  }

  public float getTotalCost() {
    return totalCost;
  }

  public String getClassName() {
    return className;
  }

  public String getPath() {
    String outerClass = className;
    if (outerClass.contains("$")) {
      outerClass = outerClass.substring(0, outerClass.indexOf("$"));
    }
    return outerClass.replaceAll("\\.", "/");
  }

  Map<String, List<Issue>> bucketize(IssueType issueType) {
    List<Issue> mostImportantIssues = getMostImportantIssues();
    Map<String, List<Issue>> theseIssues = Maps.newHashMap();
    for (IssueSubType subType : IssueSubType.values()) {
      ArrayList<Issue> issuesOfType =
          newArrayList(filter(mostImportantIssues, isType(issueType, subType)));
      if (!issuesOfType.isEmpty()) {
        theseIssues.put(subType.toString(), issuesOfType);
      }
    }
    return theseIssues;
  }

  public Map<String, List<Issue>> getConstructionIssues() {
    return bucketize(IssueType.CONSTRUCTION);
  }

  public Map<String, List<Issue>> getDirectCostIssues() {
    return bucketize(IssueType.DIRECT_COST);
  }

  public Map<String, List<Issue>> getCollaboratorIssues() {
    return bucketize(IssueType.COLLABORATOR);
  }

  public boolean isEmpty() {
    return issues.isEmpty();
  }

  public int getSize() {
    return issues.size();
  }

  /**
   * Add this issue to the collection of issues related to the class. If the issue shares a root
   * cause with an issue added earlier, those issues are collaped to a common issue with
   * multiple implications.
   *
   * @param issue to add
   */
  public void add(Issue issue) {
    for (Issue anIssue : issues) {
      if (issue.hasSameRootCause(anIssue)) {
        anIssue.getImplications().addAll(issue.getImplications());
        return;
      }
    }
    issues.offer(issue);
  }

  public static class TotalCostComparator implements Comparator<ClassIssues> {
    public int compare(ClassIssues class1Issues, ClassIssues class2Issues) {
      return Float.compare(class1Issues.getTotalCost(), class2Issues.getTotalCost());
    }
  }

  @Override
  public String toString() {
    return String.format("ClassIssues for %s: %s  Total Cost: %d\n",
        className, issues, totalCost);
  }

  /**
   * For freemarker to call (it doesn't understand enum keys in a hash
   * @return
   */
  public List<String> getTypes() {
    List<String> types = Lists.newArrayList();
    for (Enum enumVal : IssueType.values()) {
      types.add(enumVal.toString());
    }
    return types;
  }

  public List<String> getSubTypes() {
    List<String> types = Lists.newArrayList();
    for (Enum enumVal : IssueSubType.values()) {
      types.add(enumVal.toString());
    }
    return types;
  }

  public List<Issue> getMostImportantIssues() {
    if (issues instanceof TriageIssuesQueue) {
      return ((TriageIssuesQueue<Issue>)issues).asList();
    }
    return new ArrayList<Issue>(issues);
  }
}
