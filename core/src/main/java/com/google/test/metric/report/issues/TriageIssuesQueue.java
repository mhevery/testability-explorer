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

import com.google.common.collect.ForwardingQueue;
import com.google.test.metric.report.ReportOptions;

import java.util.*;

/**
 * A queue of ClassIssues that only keeps issues that are important enough to show, and sorts
 * them in order of importance.
 *
 * <b>Important:</b> the semantics of this class are only preserved if elements are enqueued
 * using the {@code offer()} method.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TriageIssuesQueue extends ForwardingQueue<ClassIssues> {
  /**
   * The delegated queue, in which issues are stored with the lowest priority first.
   */
  private final Queue<ClassIssues> delegate = new PriorityQueue<ClassIssues>(10,
      new ClassIssues.TotalCostComparator());

  private final ReportOptions reportOptions;

  public TriageIssuesQueue(ReportOptions reportOptions) {
    this.reportOptions = reportOptions;
  }

  protected Queue<ClassIssues> delegate() {
    return delegate;
  }

  @Override
  public boolean offer(ClassIssues classIssues) {
    if (classIssues.isEmpty()) {
      return false;
    }
    if (classIssues.getTotalCost() <= reportOptions.getMaxExcellentCost()) {
      return false;
    }
    if (size() == reportOptions.getWorstOffenderCount()) {
      poll();
    }
    return super.offer(classIssues);
  }

  /**
   * Empties this queue into an ordered list, with the opposite ordering as the elements
   * had in the queue. This will put the most important issues at the beginning of the list.
   * @return The list of elements in this queue
   */
  public List<ClassIssues> asList() {
    int index = size();
    ClassIssues[] array = new ClassIssues[index];
    while (!isEmpty()) {
      index--;
      array[index] = poll();
    }
    return Arrays.asList(array);
  }
}
