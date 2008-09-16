/*
 * Copyright 2008 Google Inc.
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
package com.google.test.metric.cpp;

import junit.framework.TestCase;

import com.google.test.metric.cpp.dom.FunctionDefinition;
import com.google.test.metric.cpp.dom.TranslationUnit;

public class CyclomaticComplexityTest extends TestCase {

  private TranslationUnit parse(String source) throws Exception {
    return new Parser().parse(source);
  }

  public void testEmptyFunction() throws Exception {
    TranslationUnit unit = parse(
        "void foo() {                  " +
        "}"
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(1, analyzer.getScore());
  }

  public void testSimpleFunction() throws Exception {
    TranslationUnit unit = parse(
        "void foo() {                  " +
        "  int i = 0;                  " +
        "  i += 1;                     " +
        "}                             "
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(1, analyzer.getScore());
  }

  public void testIfFunction() throws Exception {
    TranslationUnit unit = parse(
        "void foo() {                  " +
        "  int a = 0;                  " +
        "  if (a < 0) {                " +
        "    a++;                      " +
        "  } else {                    " +
        "    a--;                      " +
        "  }                           " +
        "}                             "
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(2, analyzer.getScore());
  }

  public void testIfFunctionNoElse() throws Exception {
    TranslationUnit unit = parse(
        "void foo() {                  " +
        "  int a = 0;                  " +
        "  if (a < 0) {                " +
        "    a++;                      " +
        "  }                           " +
        "}                             "
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(2, analyzer.getScore());
  }

  public void testEmptySwitch() throws Exception {
    TranslationUnit unit = parse(
        "void foo() {                  " +
        "  int a = 0;                  " +
        "  switch(a) {                 " +
        "  case 0:                     " +
        "    a = 0;                    " +
        "  }                           " +
        "}                             "
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(2, analyzer.getScore());
  }

  public void testLongerSwitch() throws Exception {
    TranslationUnit unit = parse(
        "void foo() {                  " +
        "  int a = 0;                  " +
        "  switch(a) {                 " +
        "  case 0:                     " +
        "    a = 0;                    " +
        "    break;                    " +
        "  case 1:                     " +
        "    a = 1;                    " +
        "    break;                    " +
        "  }                           " +
        "}                             "
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(3, analyzer.getScore());
  }

  public void testSwitchWithDefault() throws Exception {
    TranslationUnit unit = parse(
        "void foo() {                  " +
        "  int a = 0;                  " +
        "  switch(a) {                 " +
        "  case 0:                     " +
        "    a = 0;                    " +
        "    break;                    " +
        "  default:                    " +
        "    a = 3;                    " +
        "  }                           " +
        "}                             "
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(2, analyzer.getScore());
  }

  public void testLoopFunction() throws Exception {
    TranslationUnit unit = parse(
        "int foo() {                   " +
        "  int a = 0;                  " +
        "  while(true) {               " +
        "    ++a;                      " +
        "  }                           " +
        "  return a;                   " +
        "}                             "
    );
    FunctionDefinition functionFoo = unit.getChild(0);
    CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
    functionFoo.accept(analyzer);
    assertEquals(2, analyzer.getScore());
  }
}
