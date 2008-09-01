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

import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.ClassInfo;
import com.google.test.metric.ast.CppModuleInfo;
import com.google.test.metric.ast.FieldInfo;
import com.google.test.metric.ast.MethodInfo;
import com.google.test.metric.ast.MockVisitor;
import com.google.test.metric.ast.ModuleInfo;
import com.google.test.metric.ast.ParameterInfo;

import junit.framework.TestCase;

import java.io.CharArrayReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class CPPParserTest extends TestCase {

  private static MockVisitor parse(String source) throws Exception {
    AbstractSyntaxTree ast = new AbstractSyntaxTree();
    ASTNamespaceBuilder builder = new ASTNamespaceBuilder(ast);
    ASTBuilderLink link = new ASTBuilderLink(builder);
    builder.setLink(link);
    Reader reader = new CharArrayReader(source.toCharArray());
    CPPLexer lexer = new CPPLexer(reader);
    CPPParser parser = new CPPParser(lexer);
    parser.translation_unit(link);
    MockVisitor v = new MockVisitor();
    ast.accept(v);
    return v;
  }

  public void testEmptyClass() throws Exception {
    MockVisitor v = parse("class A {};");
    assertEquals(1, v.classes.size());
    Iterator<ClassInfo> it = v.classes.iterator();
    ClassInfo clazz = it.next();
    assertEquals("A", clazz.getName());
  }

  public void testEmptyMethod() throws Exception {
    MockVisitor v = parse("void foo() {}");
    assertEquals(1, v.methods.size());
    Iterator<MethodInfo> it = v.methods.iterator();
    MethodInfo method = it.next();
    assertEquals("foo", method.getName());
  }

  public void testEmptyNamespace() throws Exception {
    MockVisitor v = parse("namespace foo {}");
    assertEquals(1, v.modules.size());
    Iterator<ModuleInfo> it = v.modules.iterator();
    ModuleInfo module = it.next();
    assertTrue(module instanceof CppModuleInfo);
    CppModuleInfo cppModule = (CppModuleInfo) module;
    assertEquals("default", cppModule.getName());
    List<CppModuleInfo> children = cppModule.getChildren();
    assertEquals(1, children.size());
    assertEquals("foo", children.get(0).getName());
  }

  public void testNestedNamespace() throws Exception {
    MockVisitor v = parse("namespace foo { namespace bar {} }");
    assertEquals(1, v.modules.size());
    Iterator<ModuleInfo> it = v.modules.iterator();
    ModuleInfo module = it.next();
    assertTrue(module instanceof CppModuleInfo);
    CppModuleInfo cppModule = (CppModuleInfo) module;
    assertEquals("default", cppModule.getName());
    List<CppModuleInfo> children = cppModule.getChildren();
    assertEquals(1, children.size());
    assertEquals("foo", children.get(0).getName());
    children = children.get(0).getChildren();
    assertEquals(1, children.size());
    assertEquals("bar", children.get(0).getName());
  }

  public void testEmptyMethodInNamespace() throws Exception {
    MockVisitor v = parse("namespace foo { void bar() {} }");
    assertEquals(1, v.modules.size());
    Iterator<ModuleInfo> it = v.modules.iterator();
    ModuleInfo module = it.next();
    assertTrue(module instanceof CppModuleInfo);
    CppModuleInfo cppModule = (CppModuleInfo) module;
    assertEquals("default", module.getName());
    List<CppModuleInfo> children = cppModule.getChildren();
    assertEquals(1, children.size());
    assertEquals("foo", children.get(0).getName());
    cppModule = children.get(0);
    assertEquals(1, cppModule.getMethods().size());
    MethodInfo methodInfo = cppModule.getMethods().get(0);
    assertEquals("bar", methodInfo.getName());
  }

  public void testClassWithIntField() throws Exception {
    MockVisitor v = parse("class foo { int bar; };");
    assertEquals(1, v.classes.size());
    Iterator<ClassInfo> it = v.classes.iterator();
    ClassInfo clazz = it.next();
    assertEquals("foo", clazz.getName());
    Collection<FieldInfo> fields = clazz.getFields();
    assertEquals(1, fields.size());
    Iterator<FieldInfo> fieldIt = fields.iterator();
    FieldInfo field = fieldIt.next();
    assertEquals("bar", field.getName());
    assertEquals("int", field.getType().toString());
  }

  public void testClassWithPointerField() throws Exception {
    MockVisitor v = parse("class foo { int* bar; };");
    assertEquals(1, v.classes.size());
    Iterator<ClassInfo> it = v.classes.iterator();
    ClassInfo clazz = it.next();
    assertEquals("foo", clazz.getName());
    Collection<FieldInfo> fields = clazz.getFields();
    assertEquals(1, fields.size());
    Iterator<FieldInfo> fieldIt = fields.iterator();
    FieldInfo field = fieldIt.next();
    assertEquals("bar", field.getName());
// How do we represent pointers in our AST?
//    assertEquals("int*", field.getType().toString());
  }

  public void testMethodWithParameter() throws Exception {
    MockVisitor v = parse("void foo(int a) {}");
    assertEquals(1, v.methods.size());
    Iterator<MethodInfo> it = v.methods.iterator();
    MethodInfo method = it.next();
    assertEquals("foo", method.getName());
    List<ParameterInfo> parameters = method.getParameters();
    assertEquals(1, parameters.size());
    ParameterInfo parameterInfo = parameters.get(0);
    assertEquals("a", parameterInfo.getName());
  }

  public void testTemplateClass() throws Exception {
    MockVisitor v = parse("template<typename T> class foo {};");
    assertEquals(1, v.classes.size());
    Iterator<ClassInfo> it = v.classes.iterator();
    ClassInfo clazz = it.next();
    assertEquals("foo", clazz.getName());
  }
}
