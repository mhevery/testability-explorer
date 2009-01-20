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
package com.google.test.metric.cpp.dom;

import junit.framework.TestCase;

import com.google.test.metric.cpp.Parser;

public class NodeTest extends TestCase {

  private TranslationUnit parse(String source) throws Exception {
    return new Parser().parse(source);
  }

  public void testSimpleVariableLookup() throws Exception {
    TranslationUnit unit = parse("void foo() { int a = 0; int b = a; };");
    FunctionDefinition functionFoo = unit.getChild(0);
    VariableDeclaration variableB = functionFoo.getChild(1);
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNotNull(variableA);
    assertEquals(functionFoo.getChild(0), variableA);
  }

  public void testVariableLookup() throws Exception {
    TranslationUnit unit = parse("void foo() { int a = 0; if (a == 0) { int b = a; } }");
    FunctionDefinition functionFoo = unit.getChild(0);
    IfStatement ifStatement = functionFoo.getChild(1);
    VariableDeclaration variableB = ifStatement.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNotNull(variableA);
    assertEquals(functionFoo.getChild(0), variableA);
  }

  public void testGlobalVariableLookup() throws Exception {
    TranslationUnit unit = parse("int a = 0; void foo() { int b = a; }");
    FunctionDefinition functionFoo = unit.getChild(1);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNotNull(variableA);
    assertEquals(unit.getChild(0), variableA);
  }

  public void testVariableLookupFailure() throws Exception {
    TranslationUnit unit = parse("void foo() { if (true) { int a = 0; } int b = 1; }");
    FunctionDefinition functionFoo = unit.getChild(0);
    VariableDeclaration variableB = functionFoo.getChild(1);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNull(variableA);
  }
}
