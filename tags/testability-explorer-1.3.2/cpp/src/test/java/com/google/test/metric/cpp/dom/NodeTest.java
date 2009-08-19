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

import com.google.test.metric.cpp.Parser;

import junit.framework.TestCase;

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

  public void testUnnamedNamespaceVariableLookup() throws Exception {
    TranslationUnit unit = parse("namespace { int a = 0; } void foo() { int b = a; }");
    FunctionDefinition functionFoo = unit.getChild(1);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNotNull(variableA);
    Namespace namespace = unit.getChild(0);
    assertEquals(namespace.getChild(0), variableA);
  }

  public void testNamedNamespaceVariableLookup() throws Exception {
    TranslationUnit unit = parse("namespace A { int a = 0; } void foo() { int b = A::a; }");
    FunctionDefinition functionFoo = unit.getChild(1);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("A::a");
    assertNotNull(variableA);
    Namespace namespaceA = unit.getChild(0);
    assertEquals(namespaceA.getChild(0), variableA);
  }

  public void testComplexNamespaceVariableLookup() throws Exception {
    TranslationUnit unit = parse("namespace A { namespace B { int a = 0; } } void foo() { int b = A::B::a; }");
    FunctionDefinition functionFoo = unit.getChild(1);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("A::B::a");
    assertNotNull(variableA);
    Namespace namespaceA = unit.getChild(0);
    Namespace namespaceB = namespaceA.getChild(0);
    assertEquals(namespaceB.getChild(0), variableA);
  }

  public void testNamespaceVariableLookup() throws Exception {
    TranslationUnit unit = parse("namespace A { namespace B { int a = 0; } } namespace C { void foo() { int b = A::B::a; } }");
    Namespace namespaceC = unit.getChild(1);
    FunctionDefinition functionFoo = namespaceC.getChild(0);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("A::B::a");
    assertNotNull(variableA);
    Namespace namespaceA = unit.getChild(0);
    Namespace namespaceB = namespaceA.getChild(0);
    assertEquals(namespaceB.getChild(0), variableA);
  }

  public void testLateVariableLookup() throws Exception {
    TranslationUnit unit = parse("void foo() { int b = a; } int a = 0;");
    FunctionDefinition functionFoo = unit.getChild(0);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNull(variableA);
  }

  public void testFieldLookup() throws Exception {
    TranslationUnit unit = parse("class A{ int a; void foo() { int b = a; } };");
    ClassDeclaration classA = unit.getChild(0);
    FunctionDefinition functionFoo = classA.getChild(1);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNotNull(variableA);
    assertEquals("a", variableA.getName());
  }

  public void testFieldLookupPostDeclaration() throws Exception {
    TranslationUnit unit = parse("class A{ void foo() { int b = a; } int a; };");
    ClassDeclaration classA = unit.getChild(0);
    FunctionDefinition functionFoo = classA.getChild(0);
    VariableDeclaration variableB = functionFoo.getChild(0);
    // lookup variable a in the context of declaration of variable b
    VariableDeclaration variableA = variableB.lookupVariable("a");
    assertNotNull(variableA);
    assertEquals("a", variableA.getName());
  }
}
