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

import junit.framework.TestCase;

import com.google.test.metric.ClassRepository;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.Type;

public class JavaClassInfoBuilderTest extends TestCase {

  ClassRepository parent = new JavaClassRepository();
  JavaSrcRepository repository = new JavaSrcRepository(parent, null);
  Qualifier qualifier = new Qualifier();
  CompilationUnitBuilder builder = new CompilationUnitBuilder(repository, qualifier, "");

  public void testClassNameIsConcatinationOfPackageAndType() throws Exception {
    qualifier.setPackage("pkg");
    builder.startType(0, "A", null, new ArrayList<Type>());
    builder.endType();
    assertNull(builder.type);
    assertEquals("pkg.A", repository.getClass("pkg.A").getName());
  }

  public void testInnerClass() throws Exception {
    qualifier.setPackage("pkg");
    qualifier.addAlias("B", "pkg.A$B");
    builder.startType(0, "A", null, new ArrayList<Type>());
    builder.startType(0, "B", null, new ArrayList<Type>());
    builder.endType();
    builder.endType();
    assertNull(builder.type);
    assertEquals("pkg.A$B", repository.getClass("pkg.A$B").getName());
  }

}
