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
import com.google.test.metric.example.*;
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

  public void testCostToConstructIssue() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(Cost2ToConstruct.class));
    List<Issue> issues = classIssues.getConstructionIssues().getComplexityIssues();
    assertEquals(1, issues.size());
    assertEquals(25, issues.get(0).getLineNumber());
    assertEquals("com.google.test.metric.example.Cost2ToConstruct()", issues.get(0).getElementName());
    assertEquals(1.0f, issues.get(0).getContributionToClassCost());
  }

  public void testStaticWorkInConstructorIssue() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(StaticWorkInTheConstructor.class));
    List<Issue> issues = classIssues.getConstructionIssues().getStaticMethodIssues();
    assertEquals(1, issues.size());
    assertEquals(25, issues.get(0).getLineNumber());
    assertEquals("com.google.test.metric.example.StaticWorkInTheConstructor()", issues.get(0).getElementName());
    assertEquals(1.0f, issues.get(0).getContributionToClassCost());
  }

  public void testCollaboratorWorkInConstructorIssue() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(ObjectInstantiationWorkInTheConstructor.class));
    List<Issue> issues = classIssues.getConstructionIssues().getNewOperatorIssues();
    assertEquals(1, issues.size());
    assertEquals(27, issues.get(0).getLineNumber());
    assertEquals("com.google.test.metric.example.ObjectInstantiationWorkInTheConstructor()",
        issues.get(0).getElementName());
    assertEquals(1.0f, issues.get(0).getContributionToClassCost());
  }

  public void testContributionFromMultipleConstructorIssues() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(SeveralConstructionIssues.class));
    Issue complexity = classIssues.getConstructionIssues().getComplexityIssues().get(0);
    Issue staticCall = classIssues.getConstructionIssues().getStaticMethodIssues().get(0);
    Issue collaborator = classIssues.getConstructionIssues().getNewOperatorIssues().get(0);
    assertEquals(2/9f, complexity.getContributionToClassCost());
    // should be 3/9
    assertEquals(2/9f, staticCall.getContributionToClassCost());
    // should be 4/9
    assertEquals(2/9f, collaborator.getContributionToClassCost());
  }

  public void testNonMockableMethodCalled() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(SumOfPrimes1.class));
    List<Issue> issues = classIssues.getCollaboratorIssues().getNewOperatorIssues();
    assertEquals(1, issues.size());
    //TODO(alexeagle): these don't seem right.
    assertEquals(23, issues.get(0).getLineNumber());
    assertEquals("int sum(int)", issues.get(0).getElementName());
    assertEquals(0.5f, issues.get(0).getContributionToClassCost());
  }

  public void testStaticMethodCalled() throws Exception {
    ClassIssues classIssues = issuesReporter.determineIssues(
        decoratedComputer.compute(StaticMethodCalled.class));
    List<Issue> issues = classIssues.getCollaboratorIssues().getStaticMethodIssues();

    assertEquals(1, issues.size());
    assertEquals(46, issues.get(0).getLineNumber());
    // assertEquals("HasStaticMethod.isGreat()", issues.get(0).getElementName());
    assertEquals(1.0f, issues.get(0).getContributionToClassCost());
    assertTrue(classIssues.getConstructionIssues().isEmpty());
  }

  public void testContributionFromMultipleCollaboratorIssues() throws Exception {
    // TODO(alexeagle)
  }

}
