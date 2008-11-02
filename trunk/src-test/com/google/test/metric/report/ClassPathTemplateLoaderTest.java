/*
 * Copyright 2007 Google Inc.
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
package com.google.test.metric.report;

import java.io.File;
import java.io.StringWriter;

import junit.framework.TestCase;

import com.google.classpath.ClassPath;
import com.google.classpath.DirectoryClassPath;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;

public class ClassPathTemplateLoaderTest extends TestCase {

  private final ClassPath classPath = new DirectoryClassPath(new File("src-test"));
  private final String prefix = "com/google/test/metric/report/";

  public void testHelloWorld() throws Exception {
    Configuration cfg = new Configuration();
    cfg.setTemplateLoader(new ClassPathTemplateLoader(classPath, prefix));
    cfg.setObjectWrapper(new DefaultObjectWrapper());
    Template template = cfg.getTemplate("ClassPathTemplateLoaderTest.ftl");
    StringWriter writer = new StringWriter();
    template.process(this, writer);
    assertEquals("Hello World!", writer.toString());
  }

}
