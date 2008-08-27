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
package com.google.test.metric.cpp;

import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.ClassInfo;
import com.google.test.metric.ast.CppModuleInfo;
import com.google.test.metric.ast.MethodInfo;
import com.google.test.metric.ast.ModuleInfo;
import com.google.test.metric.ast.Visitor;

import junit.framework.TestCase;

import java.io.CharArrayReader;
import java.io.Reader;
import java.util.List;

public class CPPParserTest extends TestCase {

  class TestVisitor implements Visitor {
    public ClassInfo classInfo;
    public MethodInfo methodInfo;
    public ModuleInfo moduleInfo;

    public void visitClass(ClassInfo classInfo) {
      this.classInfo = classInfo;
    }

    public void visitMethod(MethodInfo methodInfo) {
      this.methodInfo = methodInfo;
    }

    public void visitModule(ModuleInfo moduleInfo) {
      this.moduleInfo = moduleInfo;
    }
  }

  public void testEmptyClass() throws Exception {
    String source = "class A {};";
    AbstractSyntaxTree ast = parse(source);
    TestVisitor v = new TestVisitor();
    ast.accept(v);
    assertNotNull(v.classInfo);
    assertEquals("A", v.classInfo.getName());
  }

  public void testEmptyMethod() throws Exception {
    String source = "void foo() {}";
    AbstractSyntaxTree ast = parse(source);
    TestVisitor v = new TestVisitor();
    ast.accept(v);
    assertNull(v.classInfo);
    assertNotNull(v.methodInfo);
    assertEquals("foo", v.methodInfo.getName());
  }

  public void testEmptyNamespace() throws Exception {
    String source = "namespace foo {}";
    AbstractSyntaxTree ast = parse(source);
    TestVisitor v = new TestVisitor();
    ast.accept(v);
    assertNull(v.classInfo);
    assertNull(v.methodInfo);
    assertNotNull(v.moduleInfo);
    assertTrue(v.moduleInfo instanceof CppModuleInfo);
    CppModuleInfo cppModuleInfo = (CppModuleInfo) v.moduleInfo;
    assertEquals("default", cppModuleInfo.getName());
    List<CppModuleInfo> children = cppModuleInfo.getChildren();
    assertEquals(1, children.size());
    assertEquals("foo", children.get(0).getName());
  }

  public void testNestedNamespace() throws Exception {
    String source = "namespace foo { namespace bar {} }";
    AbstractSyntaxTree ast = parse(source);
    TestVisitor v = new TestVisitor();
    ast.accept(v);
    assertNotNull(v.moduleInfo);
    assertTrue(v.moduleInfo instanceof CppModuleInfo);
    CppModuleInfo cppModuleInfo = (CppModuleInfo) v.moduleInfo;
    assertEquals("default", cppModuleInfo.getName());
    List<CppModuleInfo> children = cppModuleInfo.getChildren();
    assertEquals(1, children.size());
    assertEquals("foo", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals("bar", children.get(0).getName());
  }

  public void testEmptyMethodInNamespace() throws Exception {
    String source = "namespace foo { void bar() {} }";
    AbstractSyntaxTree ast = parse(source);
    TestVisitor v = new TestVisitor();
    ast.accept(v);
    assertNotNull(v.moduleInfo);
    assertEquals("default", v.moduleInfo.getName());
    CppModuleInfo cppModuleInfo = (CppModuleInfo) v.moduleInfo;
    List<CppModuleInfo> children = cppModuleInfo.getChildren();
    assertEquals(1, children.size());
    assertEquals("foo", children.get(0).getName());
    cppModuleInfo = children.get(0);
    assertEquals(1, cppModuleInfo.getMethods().size());
    MethodInfo methodInfo = cppModuleInfo.getMethods().get(0);
    assertEquals("bar", methodInfo.getName());
  }

  private AbstractSyntaxTree parse(String source) throws Exception {
    AbstractSyntaxTree ast = new AbstractSyntaxTree();
    ASTBuilder builder = new ASTBuilder(ast);
    Reader reader = new CharArrayReader(source.toCharArray());
    CPPLexer lexer = new CPPLexer(reader);
    CPPParser parser = new CPPParser(lexer);
    parser.translation_unit(builder);
    return ast;
  }
}
