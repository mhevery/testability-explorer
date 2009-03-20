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

import com.google.test.metric.report.ClassPathTemplateLoader;
import com.google.test.metric.report.SourceLinker;
import com.google.test.metric.report.FreemarkerReportGenerator;
import com.google.test.metric.report.issues.ConstructionIssues;
import com.google.test.metric.report.issues.Issue;
import com.google.test.metric.report.issues.CollaboratorIssues;
import com.google.test.metric.report.issues.ConstructionIssues.ConstructionType;
import com.google.test.metric.report.issues.CollaboratorIssues.CollaboratorType;
import static com.google.test.metric.report.issues.ConstructionIssues.ConstructionType.*;

import static com.google.common.collect.ImmutableMap.of;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import junit.framework.TestCase;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(new ClassPathTemplateLoader(FreemarkerReportGenerator.PREFIX));
    BeansWrapper objectWrapper = new DefaultObjectWrapper();
    cfg.setObjectWrapper(objectWrapper);
    ResourceBundleModel messageBundleModel =
        new ResourceBundleModel(ResourceBundle.getBundle("messages"), objectWrapper);
    template = cfg.getTemplate("costDetailBoxTest.ftl");
    model = new HashMap<String, Object>();
    model.put("message", messageBundleModel);
    model.put("sourceLink", new SourceLinkerModel(new SourceLinker("", "")));
  }

  public void testWorkInConstructorMessages() throws Exception {
    Map<ConstructionType, List<Issue>> issues =
        of(COMPLEXITY, asList(new Issue(12, "foo()", 1.0f)));
    ConstructionIssues constructionIssues = new ConstructionIssues(issues);
    model.put("issues", constructionIssues);

    template.process(model, devNull);
  }

  public void testStaticMethodCalledInConstructorMessages() throws Exception {
    Map<ConstructionType, List<Issue>> issues =
        of(STATIC_METHOD, asList(new Issue(12, "foo()", 1.0f)));
    ConstructionIssues constructionIssues = new ConstructionIssues(issues);
    model.put("issues", constructionIssues);

    template.process(model, devNull);
  }

  public void testCollaboratorInConstructorMessages() throws Exception {
    Map<ConstructionType, List<Issue>> issues =
        of(NEW_OPERATOR, asList(new Issue(12, "foo()", 1.0f)));
    ConstructionIssues constructionIssues = new ConstructionIssues(issues);
    model.put("issues", constructionIssues);

    template.process(model, devNull);
  }

  public void testStaticInitializationMessages() throws Exception {
    Map<ConstructionType, List<Issue>> issues =
        of(STATIC_INIT, asList(new Issue(12, "foo()", 1.0f)));
    ConstructionIssues constructionIssues = new ConstructionIssues(issues);
    model.put("issues", constructionIssues);

    template.process(model, devNull);
  }

  public void testComplexSetterMessages() throws Exception {
    Map<ConstructionType, List<Issue>> issues =
        of(SETTER, asList(new Issue(12, "foo()", 1.0f)));
    ConstructionIssues constructionIssues = new ConstructionIssues(issues);
    model.put("issues", constructionIssues);

    template.process(model, devNull);
  }

  public void testCollaboratorNewOperatorMessages() throws Exception {
    Map<CollaboratorType, List<Issue>> issues =
        of(CollaboratorIssues.CollaboratorType.NEW_OPERATOR, asList(new Issue(12, "new Foo()", 1.0f)));
    CollaboratorIssues collaboratorIssues = new CollaboratorIssues(issues);
    model.put("issues", collaboratorIssues);

    template.process(model, devNull);
  }

  public void testCollaboratorStaticMethodCallMessages() throws Exception {
    Map<CollaboratorType, List<Issue>> issues =
        of(CollaboratorIssues.CollaboratorType.STATIC_METHOD, asList(new Issue(12, "new Foo()", 1.0f)));
    CollaboratorIssues collaboratorIssues = new CollaboratorIssues(issues);
    model.put("issues", collaboratorIssues);

    template.process(model, devNull);
  }
}
