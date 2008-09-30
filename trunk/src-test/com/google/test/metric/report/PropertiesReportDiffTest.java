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
package com.google.test.metric.report;

import junit.framework.TestCase;

import org.apache.tools.ant.filters.StringInputStream;

import java.io.InputStream;

public class PropertiesReportDiffTest extends TestCase {

  private static final String CLASS = "com.google.test.metric.asm.FieldVisitorBuilder";
  private static final String ONE_CLASS = CLASS + "=18\n";
  private static final String CHANGED_CLASS = CLASS + "=17\n";

  public void testExample() throws Exception {
    InputStream in1 = new StringInputStream("");
    InputStream in2 = new StringInputStream("");
    Diff diff = new PropertiesReportDiffer(in1, in2).diff();
    assertEquals(0, diff.getAddedClasses().size());
    assertEquals(0, diff.getRemovedClasses().size());
  }

  public void testAddedClass() throws Exception {
    InputStream in1 = new StringInputStream("");
    InputStream in2 = new StringInputStream(ONE_CLASS);
    Diff diff = new PropertiesReportDiffer(in1, in2).diff();
    assertEquals(1, diff.getAddedClasses().size());
    assertEquals(CLASS, diff.getAddedClasses().get(0).getClassName());
    assertTrue(diff.getRemovedClasses().isEmpty());
    assertTrue(diff.getChangedClasses().isEmpty());
  }

  public void testRemovedClass() throws Exception {
    InputStream in1 = new StringInputStream(ONE_CLASS);
    InputStream in2 = new StringInputStream("");
    Diff diff = new PropertiesReportDiffer(in1, in2).diff();
    assertEquals(1, diff.getRemovedClasses().size());
    assertEquals(CLASS, diff.getRemovedClasses().get(0).getClassName());
    assertTrue(diff.getAddedClasses().isEmpty());
    assertTrue(diff.getChangedClasses().isEmpty());
  }

  public void testChangedClass() throws Exception {
    InputStream in1 = new StringInputStream(ONE_CLASS);
    InputStream in2 = new StringInputStream(CHANGED_CLASS);
    Diff diff = new PropertiesReportDiffer(in1, in2).diff();
    assertEquals(1, diff.getChangedClasses().size());
    Diff.Change change = diff.getChangedClasses().get(0);
    assertEquals(CLASS, change.getClassName());
    assertEquals(18, change.getOldMetric());
    assertEquals(17, change.getNewMetric());
    assertTrue(diff.getAddedClasses().isEmpty());
    assertTrue(diff.getRemovedClasses().isEmpty());
  }

  public void testUnchanged() throws Exception {
    InputStream in1 = new StringInputStream(ONE_CLASS);
    InputStream in2 = new StringInputStream(ONE_CLASS);
    Diff diff = new PropertiesReportDiffer(in1, in2).diff();
    assertTrue(diff.getAddedClasses().isEmpty());
    assertTrue(diff.getChangedClasses().isEmpty());
    assertTrue(diff.getRemovedClasses().isEmpty());
  }
}
