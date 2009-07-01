// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.test.metric.ClassRepository;
import com.google.test.metric.CostModel;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.example.ExpensiveConstructor.Cost2ToConstruct;
import com.google.test.metric.example.ExpensiveConstructor.ObjectInstantiationWorkInTheConstructor;
import com.google.test.metric.example.ExpensiveConstructor.StaticWorkInTheConstructor;
import com.google.test.metric.example.Lessons.Primeness;
import com.google.test.metric.example.Lessons.SumOfPrimes1;
import com.google.test.metric.example.MutableGlobalState.FinalGlobalExample;
import com.google.test.metric.example.MutableGlobalState.MutableGlobalExample;
import com.google.test.metric.example.NonMockableCollaborator.FinalMethodCantBeOverridden;
import com.google.test.metric.example.NonMockableCollaborator.StaticMethodCalled;
import static com.google.test.metric.report.issues.IssueSubType.COMPLEXITY;
import static com.google.test.metric.report.issues.IssueSubType.FINAL_METHOD;
import static com.google.test.metric.report.issues.IssueSubType.NON_MOCKABLE;
import static com.google.test.metric.report.issues.IssueSubType.SINGLETON;
import static com.google.test.metric.report.issues.IssueSubType.STATIC_METHOD;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

import junit.framework.TestCase;

import java.util.LinkedList;
import java.util.List;

/**
 * Tests the issues that are reported for the testability example code.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IssuesReportingIntegrationTest extends TestCase {
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
    List<Issue> issues = classIssues.getConstructionIssues().get(COMPLEXITY.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(22, issue.getLocation().getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("Cost2ToConstruct()", issue.getElement());
    assertEquals(1.0f, issue.getContributionToClassCost());
  }

  public void testStaticWorkInConstructorIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(StaticWorkInTheConstructor.class));
    List<Issue> issues = classIssues.getConstructionIssues().get(STATIC_METHOD.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(31, issue.getLocation().getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("boolean staticCost2()", issue.getElement());
    assertEquals(1.0f, issue.getContributionToClassCost());
  }

  public void testObjectInstantiationWorkInTheConstructorIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(ObjectInstantiationWorkInTheConstructor.class));
    List<Issue> issues = classIssues.getConstructionIssues().get(NON_MOCKABLE.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(25, issue.getLocation().getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("Cost2ToConstruct()", issue.getElement());
    assertEquals(1f, issue.getContributionToClassCost());

  }

    public void testFinalMethodCantBeOverriddenIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(FinalMethodCantBeOverridden.class));
    assertTrue(classIssues.getConstructionIssues().isEmpty());
    assertTrue(classIssues.getDirectCostIssues().isEmpty());
    List<Issue> issues = classIssues.getCollaboratorIssues().get(FINAL_METHOD.toString());
    //TODO
    //assertEquals(1, issues.size());
  }

  public void testPrimenessIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(decoratedComputer.compute(Primeness.class));
    assertEquals(1, classIssues.getSize());
    Issue issue = classIssues.getDirectCostIssues().get(COMPLEXITY.toString()).get(0);
    // FIXME(alexeagle): the method really starts on line 20, but it's not available in the bytecode.
    // run this:
    // javap -classpath target/core-1.3.1-SNAPSHOT.jar -c -l com.google.test.metric.example.Lessons.Primeness
    // Only answer is to look at the source... :(
    assertEquals(21, issue.getLocation().getLineNumber());
    assertTrue(issue.isLineNumberApproximate());
    assertEquals(1.0f, issue.getContributionToClassCost());
    assertEquals("boolean isPrime(int)", issue.getElement());
  }

  public void testSumOfPrimes1Issues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(SumOfPrimes1.class));
    List<Issue> issues = classIssues.getCollaboratorIssues().get(NON_MOCKABLE.toString());
    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(25, issue.getLocation().getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    // TODO: we'd rather see "Primeness primeness" on line 20 as the root issue here
    assertEquals("boolean isPrime(int)", issue.getElement());
    assertEquals(0.5f, issue.getContributionToClassCost());
  }

  public void testStaticMethodCalledIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(StaticMethodCalled.class));
    List<Issue> issues = classIssues.getCollaboratorIssues().get(STATIC_METHOD.toString());

    assertEquals(1, issues.size());
    Issue issue = issues.get(0);
    assertEquals(46, issue.getLocation().getLineNumber());
    assertFalse(issue.isLineNumberApproximate());
    assertEquals("boolean isGreat()", issue.getElement());
    assertEquals(1.0f, issue.getContributionToClassCost());
    assertTrue(classIssues.getConstructionIssues().isEmpty());
  }

  public void testFinalGlobalExampleIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(FinalGlobalExample.class));
    assertEquals(2, classIssues.getSize());
    assertTrue(classIssues.toString(), classIssues.getCollaboratorIssues().containsKey(SINGLETON.toString()));
    List<Issue> issues = classIssues.getCollaboratorIssues().get(SINGLETON.toString());
    Issue issue1 = issues.get(0);
    Issue issue2 = issues.get(1);
    //TODO: we'd rather see "FinalGlobalExample$Gadget finalInstance" on line 68 as the root issue
    assertEquals("int increment()", issue1.getElement());
    assertEquals(88, issue1.getLocation().getLineNumber());
    assertEquals(0.5f, issue1.getContributionToClassCost());
    assertEquals("int getCount()", issue2.getElement());
    assertEquals(84, issue2.getLocation().getLineNumber());
    assertEquals(0.5f, issue2.getContributionToClassCost());
  }

  public void testMutableGlobalExampleIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(MutableGlobalExample.class));
    assertEquals(classIssues.toString(), 3, classIssues.getSize());
  }
}
