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
package com.google.test.metric.ast;

import junit.framework.TestCase;

import java.util.List;

/**
 * Tests interaction with the S-AST, using modules.
 */
public class ModuleTest extends TestCase {

  public void testSimpleModule() {
    AbstractSyntaxTree ast = new AbstractSyntaxTree();
    ast.createModule(Language.JAVA, "TestModule");

    ModuleVisitor v = new ModuleVisitor();
    ast.accept(v);
    List<ModuleInfo> modules = v.getModules();
    assertEquals("TestModule", modules.get(0).getName());
  }
}
