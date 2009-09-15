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

public class VariableTest extends TestCase {

  private TranslationUnit parse(String source) throws Exception {
    return new Parser().parse(source);
  }

  public void testSimple() {
    Variable v = new Variable("foo::bar::a");
    assertEquals("a", v.getName());
    assertEquals("foo::bar::a", v.getQualifiedName());
  }

  public void testVariableWithContext() throws Exception {
    TranslationUnit unit = parse("namespace A { namespace B { int a; }}");
    Namespace namespaceA = unit.getChild(0);
    Namespace namespaceB = namespaceA.getChild(0);
    VariableDeclaration variableA = namespaceB.getChild(0);
    Variable v = new Variable("C::a", variableA);
    assertEquals("a", v.getName());
    assertEquals("A::B::C::a", v.getQualifiedName());
  }
}
