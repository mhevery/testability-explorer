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

import junit.framework.TestCase;

public class QualifierTest extends TestCase {

  private Qualifier qualifier = new Qualifier();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    qualifier.setPackage("pkg");
    qualifier.addImport("a.B");
  }

  public void testFullyQualified() throws Exception {
    assertEquals("a.A", qualifier.qualify("", "a.A"));
  }

  public void testImortQualified() throws Exception {
    assertEquals("a.B", qualifier.qualify("", "B"));
  }

  public void testPackageQualified() throws Exception {
    assertEquals("pkg.C", qualifier.qualify("", "C"));
  }

  public void testJavaQualified() throws Exception {
    assertEquals(String.class.getCanonicalName(), qualifier.qualify("", "String"));
  }

  public void testInnerClassResolution() throws Exception {
    qualifier.addAlias("A$My$String", "com.A$My$String");
    assertEquals("com.A$My$String", qualifier.qualify("com.A", "My.String"));
  }

  public void testInnerClassResolution2() throws Exception {
    qualifier.addAlias("My$String", "com.My$String");
    assertEquals("com.My$String", qualifier.qualify("com.A", "My.String"));
  }

  public void testInnerClassResolution3() throws Exception {
    qualifier.addAlias("My$String", "com.My$String");
    assertEquals("com.My$String", qualifier.qualify("com.A", "com.My.String"));
  }

}
