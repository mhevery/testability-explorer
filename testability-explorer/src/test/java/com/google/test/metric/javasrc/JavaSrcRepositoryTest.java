/*
 * Copyright 2009 Google Inc.
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
package com.google.test.metric.javasrc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;

import com.google.classpath.ClassPathFactory;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassNotFoundException;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.FieldInfo;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.JavaType;
import com.google.test.metric.javasrc.JavaSrcRepositoryTest.InnerClass.InnerInnerClass;

public class JavaSrcRepositoryTest extends TestCase {

  ClassRepository parent = new JavaClassRepository();
  JavaSrcRepository repo = new JavaSrcRepository(parent,
      new ClassPathFactory().createFromPaths("core/src/test/java", "src/test/java"));

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testClassDoesNotExist() throws Exception {
    try {
      repo.getClass("IDontExist");
      fail();
    } catch (ClassNotFoundException e) {
      assertTrue(e.getMessage().contains("IDontExist"));
    }
  }

  public void testRecognizeClasses() throws Exception {
    ClassInfo info = repo.getClass(getClass().getName());
    assertEquals(getClass().getName(), info.getName());
  }

  static class InnerClass {
    static class InnerInnerClass {
    }
  }

  public void testCanParseInnerClass() throws Exception {
    ClassInfo info = repo.getClass(InnerClass.class.getCanonicalName());
    assertEquals(InnerClass.class.getCanonicalName(), info.getName());

    ClassInfo info2 = repo.getClass(InnerInnerClass.class.getCanonicalName());
    assertEquals(InnerInnerClass.class.getCanonicalName(), info2.getName());
  }

  static class ClassExtendsAndImplements extends ArrayList<Object> implements
      Set<Object>, List<Object> {
    private static final long serialVersionUID = 1L;
  }

  public void testClassExtendsAndImplements() throws Exception {
    ClassInfo info = repo.getClass(ClassExtendsAndImplements.class.getCanonicalName());
    assertEquals(ArrayList.class.getCanonicalName(), info.getSuperClass().getName());
    assertEquals(2, info.getInterfaces().size());
    assertEquals(Set.class.getCanonicalName(), info.getInterfaces().get(0).getName());
    assertEquals(List.class.getCanonicalName(), info.getInterfaces().get(1).getName());
  }

  static class FieldDeclaration {
    static final String field1 = "";
    public int field2;
  }

  public void testFieldDeclareation() throws Exception {
    ClassInfo info = repo.getClass(FieldDeclaration.class.getCanonicalName());
    Iterator<FieldInfo> iterator = info.getFields().iterator();
    FieldInfo field1 = iterator.next();
    FieldInfo field2 = iterator.next();
    assertFalse(iterator.hasNext());

    assertEquals("field1", field1.getName());
    assertEquals(String.class.getCanonicalName(), field1.getType().toString());
    assertEquals(false, field1.isPrivate());
    assertEquals(true, field1.isFinal());
    assertEquals(true, field1.isGlobal());

    assertEquals("field2", field2.getName());
    assertEquals(JavaType.INT, field2.getType());
    assertEquals(false, field2.isPrivate());
    assertEquals(false, field2.isFinal());
    assertEquals(false, field2.isGlobal());
  }

  static class TypeQualifications {
    String field0;
    My.String field1;
    JavaSrcRepositoryTest.My.String field2;
    com.google.test.metric.javasrc.JavaSrcRepositoryTest.My.String field3;
  }

  static class My {
    String field;
    static class String {
    }
  }

  public void testTypeDeclaration() throws Exception {
    repo.getClass(TypeQualifications.class.getName());
    ClassInfo info = repo.getCachedClass(TypeQualifications.class.getName());
    Iterator<FieldInfo> iterator = info.getFields().iterator();
    FieldInfo field0 = iterator.next();
    FieldInfo field1 = iterator.next();
    FieldInfo field2 = iterator.next();
    FieldInfo field3 = iterator.next();
    assertFalse(iterator.hasNext());

    String expected = "com.google.test.metric.javasrc.JavaSrcRepositoryTest$My$String";
    assertEquals("java.lang.String", field0.getType().toString());
    assertEquals(expected, field1.getType().toString());
    assertEquals(expected, field2.getType().toString());
    assertEquals(expected, field3.getType().toString());

    ClassInfo myInfo = repo.getClass(My.class.getName());
    FieldInfo myField0 = myInfo.getFields().iterator().next();
    assertEquals(expected, myField0.getType().toString());

  }
}
