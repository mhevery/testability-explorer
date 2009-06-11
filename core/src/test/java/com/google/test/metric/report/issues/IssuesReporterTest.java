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
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.CostModel;
import com.google.test.metric.CostUtil;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MetricComputer;
import static com.google.test.metric.report.issues.IssueSubType.COMPLEXITY;
import static com.google.test.metric.report.issues.IssueSubType.NON_MOCKABLE;
import static com.google.test.metric.report.issues.IssueSubType.SETTER;
import static com.google.test.metric.report.issues.IssueSubType.STATIC_INIT;
import static com.google.test.metric.report.issues.IssueSubType.STATIC_METHOD;
import static com.google.test.metric.report.issues.IssueType.CONSTRUCTION;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Tests for {@link com.google.test.metric.report.issues.IssuesReporterTest}
 * These are integration tests which start from an actual class, and assert
 * what issues are reported on that class.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IssuesReporterTest extends TestCase {
  private IssuesReporter issuesReporter;
  private MetricComputerJavaDecorator decoratedComputer;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    ClassRepository repo = new JavaClassRepository();
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
    issuesReporter = new IssuesReporter(new LinkedList<ClassIssues>(), new CostModel());
  }

  private static class SeveralConstructionIssues {
    public SeveralConstructionIssues() {
      // Contributes 3
      CostUtil.staticCost3();
      // Contributes 2
      int a = 0;
      @SuppressWarnings("unused")
      int b = a > 5 ? 3 : 5;
      b = a < 4 ? 4 : 3;
      // Contributes 4
      new CostUtil().instanceCost4();
    }
  }

  public void testSeveralConstructionIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(SeveralConstructionIssues.class));
    assertEquals(classIssues.toString(), 3, classIssues.getSize());
    Map<String,List<Issue>> constructionIssues = classIssues.getConstructionIssues();
    assertTrue(classIssues.toString(), constructionIssues.containsKey(COMPLEXITY.toString()));
    Issue complexity = constructionIssues.get(COMPLEXITY.toString()).get(0);
    assertEquals(2/9f, complexity.getContributionToClassCost(), 0.001f);
    assertTrue(classIssues.toString(), constructionIssues.containsKey(STATIC_METHOD.toString()));
    Issue staticCall = constructionIssues.get(STATIC_METHOD.toString()).get(0);
    assertEquals(3/9f, staticCall.getContributionToClassCost(), 0.001f);
    assertTrue(classIssues.toString(), constructionIssues.containsKey(NON_MOCKABLE.toString()));
    Issue collaborator = constructionIssues.get(NON_MOCKABLE.toString()).get(0);
    assertEquals(4/9f, collaborator.getContributionToClassCost(), 0.001f);
  }

  private static class SeveralNonMockableMethodIssues {
    public void foo() {
      CostUtil.staticCost2();
      CostUtil.staticCost4();
    }
  }

  public void testSeveralNonMockableMethodIssues() throws Exception {
    ClassCost cost = decoratedComputer.compute(SeveralNonMockableMethodIssues.class);
    ClassIssues classIssues = issuesReporter.determineIssues(
        cost);
    assertEquals(2, classIssues.getSize());
    List<Issue> issues = classIssues.getCollaboratorIssues().get(STATIC_METHOD.toString());
    Issue issue0 = issues.get(0);
    Issue issue1 = issues.get(1);
    assertEquals(6, cost.getTotalComplexityCost() + 10 * cost.getTotalGlobalCost());
    assertEquals(4/6f, issue0.getContributionToClassCost(), 0.001f);
    assertEquals(2/6f, issue1.getContributionToClassCost(), 0.001f);

  }

  public void testMultipleMethodInvokationSourcesDoesntBlowUp() throws Exception {
    // Threw an exception at one time
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(ClassInfo.class));
  }

  private static class StaticInit {
    private static int a = 1;
  }

  public void testStaticInitializationInClass() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(decoratedComputer.compute(StaticInit.class));
    assertEquals(1, classIssues.getSize());
    assertEquals(1, classIssues.getIssues(CONSTRUCTION, STATIC_INIT).size());
  }

  private static class Setters {
    private String foo;

    public void setFoo(String foo) {
      int a = 0;
      @SuppressWarnings("unused")
      int b = a > 5 ? 3 : 5;
      this.foo = foo;
    }

    public String doFoo() {
      return foo;
    }
  }

  public void testSetterCountsAsConstructionIssue() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(decoratedComputer.compute(Setters.class));
    assertEquals(classIssues.toString(), 2, classIssues.getSize());
    assertEquals(classIssues.toString(), 1, classIssues.getIssues(CONSTRUCTION, SETTER).size());
  }
}
