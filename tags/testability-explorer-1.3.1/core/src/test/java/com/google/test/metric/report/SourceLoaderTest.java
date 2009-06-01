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

import junit.framework.TestCase;

import com.google.classpath.DirectoryClassPath;

public class SourceLoaderTest extends TestCase {

  private final DirectoryClassPath classPath = new DirectoryClassPath(new File("src/test/java"));

  public void testReadSourceFile() throws Exception {
    SourceLoader loader = new SourceLoader(classPath);
    Source source = loader.load(getClass().getName());
    assertEquals(" * Copyright 2007 Google Inc.", source.getLine(2).getText());
  }

  private static class InnerClass {
  }

  public void testReadInnerSourceFile() throws Exception {
    SourceLoader loader = new SourceLoader(classPath);
    Source source = loader.load(InnerClass.class.getName());
    assertEquals(" * Copyright 2007 Google Inc.", source.getLine(2).getText());
  }

  public void testNonExistantSource() throws Exception {
    SourceLoader loader = new SourceLoader(classPath);
    Source source = loader.load("X-I don't exist-X");
    assertNotNull(source);
  }
}
