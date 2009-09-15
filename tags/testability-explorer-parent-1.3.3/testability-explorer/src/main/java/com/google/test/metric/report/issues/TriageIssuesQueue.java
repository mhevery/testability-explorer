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
import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * A queue of IssueHolders that only keeps issues that are important enough to show, and sorts
 * them in order of importance.
 *
 * <b>Important:</b> the semantics of this class are only preserved if elements are enqueued
 * using the {@code offer()} method.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TriageIssuesQueue<I extends IssueHolder> extends ForwardingQueue<I> {
  /**
   * The delegated queue, in which issues are stored with the lowest priority first.
   */
  private final Queue<I> delegate;
  private final float minCost;
  private final int maxSize;
  private final Comparator<I> comparator;

  public TriageIssuesQueue(float minCost, int maxSize, Comparator<I> comparator) {
    this.minCost = minCost;
    this.maxSize = maxSize;
    this.comparator = comparator;
    delegate = new PriorityQueue<I>(maxSize, comparator);
  }

  @Override
  protected Queue<I> delegate() {
    return delegate;
  }

  @Override
  public boolean offer(I issue) {
    if (issue.getTotalCost() <= minCost) {
      return false;
    }
    if (size() == maxSize) {
      if (comparator.compare(issue, peek()) < 0) {
        return false;
      }
      poll();
    }
    return super.offer(issue);
  }

  /**
   * Copies this queue into an ordered list, with the opposite ordering as the elements
   * had in the queue. This will put the most important issues at the beginning of the list.
   * @return The list of elements in this queue
   */
  public List<I> asList() {
    // Stupid PriorityQueue doesn't provide an ordered iterator, and it only guarantees that the
    // topmost element is in the right order
    PriorityQueue<I> copy = new PriorityQueue<I>(delegate);
    List<I> asList = Lists.newLinkedList();
    while (!copy.isEmpty()) {
      asList.add(0, copy.poll());
    }
    return asList;
  }
}
