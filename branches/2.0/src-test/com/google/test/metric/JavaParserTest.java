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
package com.google.test.metric;

import com.google.test.metric.ast.AbstractSyntaxTree;

import junit.framework.TestCase;

/**
 * Tests the parsing of Java byte code.
 */
public class JavaParserTest extends TestCase {

  public void testEmptyClass() {
    AbstractSyntaxTree ast = new AbstractSyntaxTree();
    JavaParser parser = new JavaParser(ast);
    parser.getClass(ClassInfoTest.EmptyClass.class);
    assertTrue(ast.containsClass(
        ClassInfoTest.EmptyClass.class.getName().replace(".", "/")));
  }
}
