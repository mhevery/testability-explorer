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
package com.google.test.metric.javascript;

import junit.framework.TestCase;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.FunctionNode;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Basic tests that the Javascript parser produces an Abstract Syntax Tree
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JavascriptParsingTest extends TestCase {

  public void testThatParsingIsPossible() throws Exception {
    String js = "function a() {};";
    Parser parser = new Parser();
    AstRoot ast = parser.parse(js, "source.js", 1);
    assertEquals("a", ((FunctionNode)ast.getFirstChild()).getFunctionName().getIdentifier());
  }

  public void testParseComplexStuff() throws Exception {
    Reader source = new InputStreamReader(
        this.getClass().getResourceAsStream("browser_debug.js"));
    Parser parser = new Parser();
    AstRoot ast = parser.parse(source, "browser_debug.js", 1);
    String debugString = ast.debugPrint();
    assertTrue(debugString.contains("getRequiresAndProvides"));
    assertTrue(debugString.contains("ARRAYLIT"));
  }
}
