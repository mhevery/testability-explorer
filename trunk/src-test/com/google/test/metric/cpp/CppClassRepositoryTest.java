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

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.tools.ant.filters.StringInputStream;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.MethodInfo;

public class CppClassRepositoryTest extends TestCase {

  public void testSimpleClass() throws Exception {
    CppClassRepository repository = new CppClassRepository();
    repository.parse(new StringInputStream("class A{};"));
    ClassInfo classInfo = repository.getClass("A");
    assertNotNull(classInfo);
    assertFalse(classInfo.isInterface());
    assertTrue(classInfo.getMethods().isEmpty());
  }

  public void testRepositoryAddClass() throws Exception {
    CppClassRepository repository = new CppClassRepository();
    ClassInfo classInfo = new ClassInfo("A", false, null,
        new ArrayList<ClassInfo>());
    repository.addClass(classInfo);
    assertNotNull(repository.getClass("A"));
  }

  public void testClassWithMethod() throws Exception {
    CppClassRepository repository = new CppClassRepository();
    repository.parse(new StringInputStream("class A{ void foo() {} };"));
    ClassInfo classInfo = repository.getClass("A");
    assertNotNull(classInfo);
    assertFalse(classInfo.isInterface());
    assertEquals(1, classInfo.getMethods().size());
    Iterator<MethodInfo> it = classInfo.getMethods().iterator();
    assertEquals("foo", it.next().getName());
  }
}
