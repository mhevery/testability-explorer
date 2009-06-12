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
package com.google.test.metric.report.html;

import com.google.common.collect.Lists;
import com.google.test.metric.ReportGeneratorBuilder;
import com.google.test.metric.report.ClassPathTemplateLoader;
import com.google.test.metric.report.SourceLinker;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.Issue;
import com.google.test.metric.report.issues.IssueSubType;
import com.google.test.metric.report.issues.IssueType;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

import junit.framework.TestCase;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;

/**
 * Test that all the messages we might want to show are defined.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class MessagesForCostDetailBoxTest extends TestCase {
  private final PrintWriter devNull = new PrintWriter(new OutputStream() {
    @Override
    public void write(int i) throws IOException {
      // throw away
    }
  });
  private Template template;
  private Map<String, Object> model;
  private Queue<Issue> issueQueue;
  private String foo = "foo()";

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(new ClassPathTemplateLoader(ReportGeneratorBuilder.PREFIX));
    BeansWrapper objectWrapper = new DefaultObjectWrapper();
    cfg.setObjectWrapper(objectWrapper);
    ResourceBundleModel messageBundleModel =
        new ResourceBundleModel(ResourceBundle.getBundle("messages"), objectWrapper);
    issueQueue = Lists.newLinkedList();
    template = cfg.getTemplate("costDetailBoxTest.ftl");
    model = new HashMap<String, Object>();
    model.put("message", messageBundleModel);
    model.put("sourceLink", new SourceLinkerModel(new SourceLinker("", "")));
  }

  public void testWorkInConstructorMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.CONSTRUCTION, IssueSubType.COMPLEXITY));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "construction");
    template.process(model, devNull);
  }

  public void testStaticMethodCalledInConstructorMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.CONSTRUCTION, IssueSubType.STATIC_METHOD));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "construction");
    template.process(model, devNull);
  }

  public void testCollaboratorInConstructorMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.CONSTRUCTION, IssueSubType.NON_MOCKABLE));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "construction");
    template.process(model, devNull);
  }

  public void testStaticInitializationMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.CONSTRUCTION, IssueSubType.STATIC_INIT));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "construction");
    template.process(model, devNull);
  }

  public void testComplexSetterMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.CONSTRUCTION, IssueSubType.SETTER));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "construction");
    template.process(model, devNull);
  }

  public void testCollaboratorNewOperatorMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.COLLABORATOR, IssueSubType.NON_MOCKABLE));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "collaborator");
    template.process(model, devNull);
  }

  public void testCollaboratorStaticMethodCallMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.COLLABORATOR, IssueSubType.STATIC_METHOD));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "collaborator");
    template.process(model, devNull);
  }

  public void testDirectCostMessages() throws Exception {
    issueQueue.offer(new Issue(12, foo, 1.0f, IssueType.DIRECT_COST, IssueSubType.COMPLEXITY));
    ClassIssues issues = new ClassIssues("Foo", 100, issueQueue);
    model.put("issues", issues.getConstructionIssues());
    model.put("issueType", "direct_cost");
    template.process(model, devNull);
  }
}
