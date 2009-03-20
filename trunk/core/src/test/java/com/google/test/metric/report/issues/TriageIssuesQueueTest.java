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
import static com.google.test.metric.report.issues.ConstructionIssues.ConstructionType.*;
import com.google.test.metric.report.ReportOptions;
import static com.google.common.collect.ImmutableMap.*;

import static java.util.Arrays.*;
import java.util.Iterator;

/**
 * Tests for {@link TriageIssuesQueue}
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TriageIssuesQueueTest extends TestCase {
  TriageIssuesQueue queue;
  private final int maxExcellentCost = 50;
  private ConstructionIssues constructionIssues;
  private final int maxOffenders = 10;


  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ReportOptions reportOptions = new ReportOptions();
    reportOptions.setMaxExcellentCost(maxExcellentCost);
    reportOptions.setWorstOffenderCount(maxOffenders);
    queue = new TriageIssuesQueue(reportOptions);
    constructionIssues = new ConstructionIssues(of(NEW_OPERATOR, asList(new Issue(1, "", 1f))));
  }

  public void testEmptyClassIssuesAreDiscarded() throws Exception {
    queue.offer(new ClassIssues("foo", 0));
    assertTrue(queue.isEmpty());
  }

  public void testNonEmptyClassIssuesAreAdded() throws Exception {
    ClassIssues classIssues = new ClassIssues("FooClass", 100,
        constructionIssues, new DirectCostIssues(), new CollaboratorIssues());
    queue.offer(classIssues);
    assertEquals(classIssues, queue.peek());
  }

  public void testAsListMethodGivesOrderedIterator() throws Exception {
    ClassIssues class1Issues = new ClassIssues("FooClass", 100,
        constructionIssues, new DirectCostIssues(), new CollaboratorIssues());
    ClassIssues class2Issues = new ClassIssues("FooClass", 200,
        constructionIssues, new DirectCostIssues(), new CollaboratorIssues());
    ClassIssues class3Issues = new ClassIssues("FooClass", 300,
        constructionIssues, new DirectCostIssues(), new CollaboratorIssues());
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
      ClassIssues class1Issues = new ClassIssues("FooClass", 100,
          constructionIssues, new DirectCostIssues(), new CollaboratorIssues());
      queue.offer(class1Issues);
    }
    assertEquals(maxOffenders, queue.size());
  }

  public void testOnlyNonExcellentClassesAreRetained() throws Exception {
    ClassIssues class1Issues = new ClassIssues("FooClass", maxExcellentCost - 1,
        constructionIssues, new DirectCostIssues(), new CollaboratorIssues());
    queue.offer(class1Issues);
    assertTrue(queue.isEmpty());
  }
}
