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

import com.google.test.metric.ClassRepository;
import com.google.test.metric.CostModel;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.example.ExpensiveConstructor.ObjectInstantiationWorkInTheConstructor;
import com.google.test.metric.example.ExpensiveConstructor.StaticWorkInTheConstructor;
import com.google.test.metric.example.ExpensiveConstructor.Cost2ToConstruct;
import com.google.test.metric.example.Lessons.SumOfPrimes1;
import com.google.test.metric.example.Lessons.Primeness;
import com.google.test.metric.example.NonMockableCollaborator.StaticMethodCalled;
import com.google.test.metric.example.NonMockableCollaborator.FinalMethodCantBeOverridden;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;
import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

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

  public void testCost2ToConstructIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(Cost2ToConstruct.class));
    List<Issue> issues = classIssues.getConstructionIssues().get(IssueSubType.COMPLEXITY.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(22, issue.getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("com.google.test.metric.example.ExpensiveConstructor.Cost2ToConstruct()", issue.getElementName());
    assertEquals(1.0f, issue.getContributionToClassCost());
  }

  public void testStaticWorkInConstructorIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(StaticWorkInTheConstructor.class));
    List<Issue> issues = classIssues.getConstructionIssues().get(IssueSubType.STATIC_METHOD.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(31, issue.getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("boolean staticCost2()", issue.getElementName());
    assertEquals(1.0f, issue.getContributionToClassCost());
  }

  public void testObjectInstantiationWorkInTheConstructorIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(ObjectInstantiationWorkInTheConstructor.class));
    // TODO
    // assertEquals(2, classIssues.getCollaboratorIssues().);
    List<Issue> issues = classIssues.getConstructionIssues().get(IssueSubType.NEW_OPERATOR.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(25, issue.getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("com.google.test.metric.example.ExpensiveConstructor.Cost2ToConstruct()", issue.getElementName());
    assertEquals(1f, issue.getContributionToClassCost());

  }

  public void testSeveralConstructionIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(SeveralConstructionIssues.class));
    assertEquals(classIssues.toString(), 3, classIssues.getSize());
    Issue complexity = classIssues.getConstructionIssues().get(IssueSubType.COMPLEXITY.toString()).get(0);
    Issue staticCall = classIssues.getConstructionIssues().get(IssueSubType.STATIC_METHOD.toString()).get(0);
    Issue collaborator = classIssues.getConstructionIssues().get(IssueSubType.NEW_OPERATOR.toString()).get(0);
    assertEquals(2/9f, complexity.getContributionToClassCost());
    assertEquals(3/9f, staticCall.getContributionToClassCost());
    assertEquals(4/9f, collaborator.getContributionToClassCost());
  }

  public void testFinalMethodCantBeOverriddenIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(FinalMethodCantBeOverridden.class));
    assertTrue(classIssues.getConstructionIssues().isEmpty());
    assertTrue(classIssues.getDirectCostIssues().isEmpty());
    List<Issue> issues = classIssues.getCollaboratorIssues().get(IssueSubType.FINAL_METHOD.toString());
    //TODO
    //assertEquals(1, issues.size());
  }

  public void testPrimenessIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(decoratedComputer.compute(Primeness.class));
    assertEquals(1, classIssues.getSize());
    Issue issue = classIssues.getDirectCostIssues().get(IssueSubType.COMPLEXITY.toString()).get(0);
    // FIXME(alexeagle): the method really starts on line 20, but it's not available in the bytecode.
    // run this: javap -classpath target/core-1.3.1-SNAPSHOT.jar -c -l com.google.test.metric.example.Lessons.Primeness
    // Only answer is to look at the source... :(
    assertEquals(21, issue.getLineNumber());
    assertTrue(issue.isLineNumberApproximate());
    assertEquals(1.0f, issue.getContributionToClassCost());
    assertEquals("boolean isPrime(int)", issue.getElementName());
  }

  public void testSumOfPrimes1Issues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(SumOfPrimes1.class));
    List<Issue> issues = classIssues.getCollaboratorIssues().get(IssueSubType.NEW_OPERATOR.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(25, issue.getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("boolean isPrime(int)", issue.getElementName());
    assertEquals(0.5f, issue.getContributionToClassCost());
  }

  public void testStaticMethodCalledIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(StaticMethodCalled.class));
    List<Issue> issues = classIssues.getCollaboratorIssues().get(IssueSubType.STATIC_METHOD.toString());

    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(46, issue.getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("boolean isGreat()", issue.getElementName());
    assertEquals(1.0f, issue.getContributionToClassCost());
    assertTrue(classIssues.getConstructionIssues().isEmpty());
  }


  public void testSeveralNonMockableMethodIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(SeveralNonMockableMethodIssues.class));
    assertEquals(2, classIssues.getSize());
    List<Issue> issues = classIssues.getCollaboratorIssues().get(IssueSubType.STATIC_METHOD.toString());
    Issue issue0 = issues.get(0);
    Issue issue1 = issues.get(1);

    assertEquals(4/6f, issue0.getContributionToClassCost());
    assertEquals(2/6f, issue1.getContributionToClassCost());

  }

  public void testNoIssuesForMainMethod() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(decoratedComputer.compute(Main.class));
    assertEquals(0, classIssues.getSize());
  }
}
