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

import static com.google.test.metric.report.issues.IssueType.*;
import static com.google.test.metric.report.issues.IssueSubType.*;
import com.google.test.metric.report.StubSourceElement;
import junit.framework.TestCase;

import java.util.List;
import java.util.Map;
import java.util.LinkedList;

/**
 * Tests for {@link com.google.test.metric.report.issues.ClassIssues}
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassIssuesTest extends TestCase {
  private SourceElement foo = new StubSourceElement("foo()");

  public void testIssuesAreSortedByContribution() throws Exception {
    ClassIssues classIssues = new ClassIssues("Foo", 100);
    classIssues.add(new Issue(1, foo, 0.1f, COLLABORATOR, COMPLEXITY));
    classIssues.add(new Issue(2, foo, 0.9f, COLLABORATOR, COMPLEXITY));
    classIssues.add(new Issue(3, foo, 0.5f, COLLABORATOR, COMPLEXITY));

    List<Issue> list = classIssues.getCollaboratorIssues().get(COMPLEXITY.toString());
    assertEquals(3, list.size());
    assertEquals(0.9f, list.get(0).getContributionToClassCost());
    assertEquals(0.5f, list.get(1).getContributionToClassCost());
    assertEquals(0.1f, list.get(2).getContributionToClassCost());

  }

  public void testBucketizationOfIssuesIntoSubTypes() throws Exception {
    ClassIssues classIssues = new ClassIssues("Foo", 100);
    classIssues.add(new Issue(1, foo, 0.1f, COLLABORATOR, COMPLEXITY));
    classIssues.add(new Issue(2, foo, 0.9f, DIRECT_COST, COMPLEXITY));
    classIssues.add(new Issue(3, foo, 0.5f, DIRECT_COST, COMPLEXITY));
    classIssues.add(new Issue(4, foo, 0.5f, DIRECT_COST, STATIC_METHOD));

    Map<String, List<Issue>> issueMap = classIssues.bucketize(IssueType.DIRECT_COST);
    assertEquals(2, issueMap.keySet().size());
    assertEquals(2, issueMap.get(COMPLEXITY.toString()).size());
    assertEquals(1, issueMap.get(STATIC_METHOD.toString()).size());


  }

  public void testPathInnerClassIsStripped() throws Exception {
    ClassIssues classIssues = new ClassIssues("com.google.Foo$1", 100);
    assertEquals("com/google/Foo", classIssues.getPath());
  }
}
