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

import static com.google.test.metric.report.issues.IssueSubType.COMPLEXITY;
import static com.google.test.metric.report.issues.IssueSubType.STATIC_METHOD;
import static com.google.test.metric.report.issues.IssueType.COLLABORATOR;
import static com.google.test.metric.report.issues.IssueType.DIRECT_COST;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import com.google.test.metric.SourceLocation;

/**
 * Tests for {@link com.google.test.metric.report.issues.ClassIssues}
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassIssuesTest extends TestCase {
  private String foo = "foo()";
  private SourceLocation location = new SourceLocation("foo", 1);

  public void testIssuesAreSortedByContribution() throws Exception {
    ClassIssues classIssues = new ClassIssues("Foo", 100);
    classIssues.add(new Issue(location, foo, 0.1f, COLLABORATOR, COMPLEXITY));
    classIssues.add(new Issue(location, foo, 0.9f, COLLABORATOR, COMPLEXITY));
    classIssues.add(new Issue(location, foo, 0.5f, COLLABORATOR, COMPLEXITY));

    List<Issue> list = classIssues.getCollaboratorIssues().get(COMPLEXITY.toString());
    assertEquals(3, list.size());
    assertEquals(0.9f, list.get(0).getContributionToClassCost());
    assertEquals(0.5f, list.get(1).getContributionToClassCost());
    assertEquals(0.1f, list.get(2).getContributionToClassCost());

  }

  public void testBucketizationOfIssuesIntoSubTypes() throws Exception {
    ClassIssues classIssues = new ClassIssues("Foo", 100);
    classIssues.add(new Issue(location, foo, 0.1f, COLLABORATOR, COMPLEXITY));
    classIssues.add(new Issue(location, foo, 0.9f, DIRECT_COST, COMPLEXITY));
    classIssues.add(new Issue(location, foo, 0.5f, DIRECT_COST, COMPLEXITY));
    classIssues.add(new Issue(location, foo, 0.5f, DIRECT_COST, STATIC_METHOD));

    Map<String, List<Issue>> issueMap = classIssues.bucketize(IssueType.DIRECT_COST);
    assertEquals(2, issueMap.keySet().size());
    assertEquals(2, issueMap.get(COMPLEXITY.toString()).size());
    assertEquals(1, issueMap.get(STATIC_METHOD.toString()).size());


  }
}
