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

import java.io.CharArrayReader;
import java.io.Reader;

import junit.framework.TestCase;

import com.google.test.metric.cpp.dom.ClassDeclaration;
import com.google.test.metric.cpp.dom.Node;

public class CppParserTest extends TestCase {

  private Node parse(String source) throws Exception {
    ClassBuilder builder = new ClassBuilder();
    Reader reader = new CharArrayReader(source.toCharArray());
    CPPLexer lexer = new CPPLexer(reader);
    CPPParser parser = new CPPParser(lexer);
    parser.translation_unit(builder);
    return builder.getNode();
  }

  public void testEmptyClass() throws Exception {
    Node node = parse("class A{};");
    assertNotNull(node);
    assertTrue(node instanceof ClassDeclaration);
    ClassDeclaration classDeclaration = (ClassDeclaration) node;
    assertEquals("A", classDeclaration.getName());
  }

  public void testClassLoadCppVariables() throws Exception {
    assertEquals(64, CPPvariables.QI_TYPE.size());
  }
}
