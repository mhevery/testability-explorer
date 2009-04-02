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

import junit.framework.TestCase;

public class SourceLinkerTest extends TestCase {

  String lineTemplate = "pre{path}#{line}";
  String classTemplate = "pre{path}";

  private final SourceLinker linker = new SourceLinker(lineTemplate, classTemplate);

  public void testBuildClassLink() {
    assertEquals("<a href=\"pre//a.java\" target=\"source\">fin.FinUI</a>",
        linker.buildClassLink("//a.java", "fin.FinUI"));

    String generatedLink = linker.buildClassLink("//class.java", "class$Conv");
    assertEquals(generatedLink, "<a href=\"pre//class.java\" target=\"source\">class$Conv</a>");

  }

  public void testBuildLineLink() {
    assertEquals(
        "<a href=\"pre//a.java#1234\" target=\"source\">fin.FinUI</a>",
        linker.buildLineLink("//a.java", 1234, "fin.FinUI"));


    String generatedLink = linker.buildLineLink("//class.java", 1234, " void methodA()");
    assertEquals(
        "<a href=\"pre//class.java#1234\" target=\"source\"> void methodA()</a>",
        generatedLink);
  }

  public void testGetOriginalFilePath() {
    assertEquals("java/lang/String.java", linker.getOriginalFilePath("java.lang.String"));
  }


  SourceLinker emptyLinker = new SourceLinker("", "");

  public void testEmptyLinks() throws Exception {
    assertEquals("anchor", emptyLinker.buildClassLink("", "anchor"));

    assertEquals("anchor", emptyLinker.buildLineLink("", 1000, "anchor"));
  }
}
