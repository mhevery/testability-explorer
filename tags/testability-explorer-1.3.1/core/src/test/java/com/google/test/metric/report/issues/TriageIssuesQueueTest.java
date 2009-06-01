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

import junit.framework.TestCase;

import java.util.Iterator;
import java.util.List;

import com.google.test.metric.report.StubSourceElement;

/**
 * Tests for {@link TriageIssuesQueue}
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TriageIssuesQueueTest extends TestCase {
  TriageIssuesQueue<ClassIssues> queue;
  private final int maxExcellentCost = 50;
  private final int maxOffenders = 10;
  private Issue issue;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    queue = new TriageIssuesQueue(maxExcellentCost, maxOffenders,
        new ClassIssues.TotalCostComparator());
    issue = new Issue(1, new StubSourceElement(null), 1f);
  }

  public void testEmptyClassIssuesAreDiscarded() throws Exception {
    queue.offer(new ClassIssues("foo", 0));
    assertTrue(queue.isEmpty());
  }

  public void testNonEmptyClassIssuesAreAdded() throws Exception {
    ClassIssues classIssues = new ClassIssues("FooClass", 100);
    classIssues.add(issue);
    queue.offer(classIssues);
    assertEquals(classIssues, queue.peek());
  }

  public void testAsListMethodGivesOrderedIterator() throws Exception {
    ClassIssues class1Issues = new ClassIssues("FooClass", 100);
    ClassIssues class2Issues = new ClassIssues("FooClass", 200);
    ClassIssues class3Issues = new ClassIssues("FooClass", 300);
    class1Issues.add(issue);
    class2Issues.add(issue);
    class3Issues.add(issue);
    queue.offer(class1Issues);
    queue.offer(class3Issues);
    queue.offer(class2Issues);
    Iterator<ClassIssues> iter = queue.asList().iterator();
    assertEquals(class3Issues, iter.next());
    assertEquals(class2Issues, iter.next());
    assertEquals(class1Issues, iter.next());
  }

  public void testOnlyMaxOffendersAreRetained() throws Exception {
    for (int count = 0; count <= maxOffenders; count++) {
      ClassIssues class1Issues = new ClassIssues("FooClass", 100);
      class1Issues.add(issue);
      queue.offer(class1Issues);
    }
    assertEquals(maxOffenders, queue.size());
  }

  public void testOnlyNonExcellentClassesAreRetained() throws Exception {
    ClassIssues class1Issues = new ClassIssues("FooClass", maxExcellentCost - 1);
    class1Issues.add(issue);
    queue.offer(class1Issues);
    assertTrue(queue.isEmpty());
  }

  public void testQueueWorksWithIssuesAlso() throws Exception {
    Issue issue = new Issue(1, new StubSourceElement(null), 1f);
    TriageIssuesQueue<Issue> issueQueue = new TriageIssuesQueue<Issue>(.5f, 20,
        new Issue.TotalCostComparator());
    issueQueue.offer(issue);
    assertFalse(issueQueue.isEmpty());
    List<Issue> list = issueQueue.asList();
    assertEquals(issue, list.get(0));
  }

  public void testLastItemAddedMustHaveHighPriority() throws Exception {
    TriageIssuesQueue<ClassIssues> smallQueue = new TriageIssuesQueue<ClassIssues>(100, 1,
        new ClassIssues.TotalCostComparator());
    ClassIssues class1Issues = new ClassIssues("BadClass", 500);
    class1Issues.add(issue);
    smallQueue.offer(class1Issues);
    ClassIssues class2Issues = new ClassIssues("PrettyGoodClass", 101);
    class2Issues.add(issue);
    smallQueue.offer(class2Issues);

    assertEquals("BadClass", smallQueue.asList().get(0).getClassName());
  }

  public void testLastItemDoesPushOutLowerPriority() throws Exception {
    TriageIssuesQueue<ClassIssues> smallQueue = new TriageIssuesQueue<ClassIssues>(100, 2,
        new ClassIssues.TotalCostComparator());
    ClassIssues class1Issues = new ClassIssues("BadClass", 500);
    ClassIssues class2Issues = new ClassIssues("PrettyGoodClass", 200);
    ClassIssues class3Issues = new ClassIssues("NotGreatClass", 300);
    class1Issues.add(issue);
    class2Issues.add(issue);
    class3Issues.add(issue);
    smallQueue.offer(class1Issues);
    smallQueue.offer(class2Issues);
    smallQueue.offer(class3Issues);

    assertEquals("BadClass", smallQueue.asList().get(0).getClassName());
    assertEquals("NotGreatClass", smallQueue.asList().get(1).getClassName());
  }
}
