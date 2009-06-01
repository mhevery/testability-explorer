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

/**
 * Integration tests for javascript testability cost assessment
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JavascriptTestabilityTest extends TestCase {

  public void testExample() throws Exception {
    FileRepository repository = new FileRepository();
    repository.addSourceFile("one.js", "");
    repository.addSourceFile("two.js", "");
    int overallCost = new JavascriptTestability(repository).calculateCost();
    assertEquals(0, overallCost);
  }

  public void testFunctionCost() throws Exception {
    FileRepository repository = new FileRepository();
    repository.addSourceFile("one.js", "function a() {}");
    int overallCost = new JavascriptTestability(repository).calculateCost();
    assertEquals(0, overallCost);
  }

  public void testFunctionCost1() throws Exception {
    FileRepository repository = new FileRepository();
    repository.addSourceFile("one.js", "function a() { var h = 'hello'; }");
    int overallCost = new JavascriptTestability(repository).calculateCost();
    assertEquals(0, overallCost);
  }

  public void testFunctionCost2() throws Exception {
    FileRepository repository = new FileRepository();
    repository.addSourceFile("one.js", "function a() { return 'hello'; }");
    int overallCost = new JavascriptTestability(repository).calculateCost();
    assertEquals(0, overallCost);
  }

  public void testFunctionCost3() throws Exception {
    FileRepository repository = new FileRepository();
    repository.addSourceFile("one.js", "h = 'hello'; function a() { return function() { h = '1'}}");
    int overallCost = new JavascriptTestability(repository).calculateCost();
    assertEquals(1, overallCost);
  }


}
