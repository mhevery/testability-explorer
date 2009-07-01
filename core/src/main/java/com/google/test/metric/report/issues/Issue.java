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

import com.google.common.base.Nullable;
import com.google.common.base.Predicate;
import com.google.test.metric.SourceLocation;

import java.util.Comparator;

/**
 * A model of a single reportable issue with the class under analysis.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class Issue implements IssueHolder {
  private final String element;
  private float contributionToClassCost;
  private boolean isLineNumberApproximate;
  private IssueType type;
  private IssueSubType subType;
  private SourceLocation location;

  public Issue(SourceLocation location, String element, float contributionToClassCost,
               IssueType type, IssueSubType subType) {
    this.location = location;
    this.element = element;
    this.contributionToClassCost = contributionToClassCost;
    this.type = type;
    this.subType = subType;
  }

  public void setContributionToClassCost(float contributionToClassCost) {
    this.contributionToClassCost = contributionToClassCost;
  }

  public String getElement() {
    return element;
  }

  public float getContributionToClassCost() {
    return contributionToClassCost;
  }

  public boolean isLineNumberApproximate() {
    return isLineNumberApproximate;
  }

  public void setLineNumberIsApproximate(boolean isApprox) {
    this.isLineNumberApproximate = isApprox;
  }

  @Override
  public String toString() {
    return String.format("On line %d, element %s with contribution %f (%s/%s)",
        location.getLineNumber(), element, contributionToClassCost, type, subType);
  }

  public void setType(IssueType type) {
    this.type = type;
  }

  public IssueType getType() {
    return type;
  }

  public IssueSubType getSubType() {
    return subType;
  }

  public void setSubType(IssueSubType subType) {
    this.subType = subType;
  }

  public boolean isEmpty() {
    return 0f == contributionToClassCost;
  }

  public float getTotalCost() {
    return contributionToClassCost;
  }

  public SourceLocation getLocation() {
    return location;
  }

  public static Predicate<? super Issue> isType(final IssueType issueType,
                                                final IssueSubType subType) {
    return new Predicate<Issue>() {
      public boolean apply(@Nullable Issue issue) {
        if (issue.getType() == null || issue.getSubType() == null) {
          return false;
        }
        return issue.getType() == issueType && issue.getSubType() == subType;
      }
    };
  }

  public static class TotalCostComparator implements Comparator<Issue> {
    public int compare(Issue issue, Issue issue1) {
      return Float.compare(issue.getContributionToClassCost(), issue1.getContributionToClassCost());
    }
  }
}
