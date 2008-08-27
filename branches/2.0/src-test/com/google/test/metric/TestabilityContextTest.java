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


import com.google.test.metric.ast.Field;
import com.google.test.metric.ast.VariableImpl;

import junit.framework.TestCase;

import java.util.ArrayList;

public class TestabilityContextTest extends TestCase {

  TestabilityContext context =
      new TestabilityContext(null, null, new RegExpWhiteList(), new CostModel());

  Variable instance = new VariableImpl("instance", null, false, false);
  Variable finalInstance = new VariableImpl("instance", null, true, false);
  //TODO: used to be FieldInfo
  Field field = new Field(null, "field", null, null, false);
  Field finalField = new Field(null, "field", null, null, true);
  Field finalStaticField = new Field(null, "field", null, null, true);
  LocalField localField = new LocalField(instance, field);
  LocalField localFinalField = new LocalField(instance, finalField);
  LocalField localStaticFinalField = new LocalField(null, finalStaticField);
  Variable dst = new VariableImpl("dst", null, false, false);
  @SuppressWarnings("unchecked")
  ClassInfo classInfo = new ClassInfo("c.g.t.A", false, null, new ArrayList<ClassInfo>());
  MethodInfo method =
      new MethodInfo(classInfo, "method", 0, "()V", null, null, null, null, 1, null);

  public void testIsInjectable() throws Exception {
    Variable var = new VariableImpl("", Type.fromJava("X"), false, false);
    assertFalse(context.isInjectable(var));
    context.setInjectable(var);
    assertTrue(context.isInjectable(var));
  }

  public void testNoop() throws Exception {
    context.localAssignment(method, 1, dst, instance);
    assertFalse(context.isGlobal(dst));
    assertFalse(context.isInjectable(dst));
    assertEquals(0, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

  public void testInjectability() throws Exception {
    context.setInjectable(instance);
    context.localAssignment(method, 1, dst, instance);
    assertFalse(context.isGlobal(dst));
    assertTrue(context.isInjectable(dst));
    assertEquals(0, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

  public void testFieldReadNoop() throws Exception {
    context.localAssignment(method, 1, dst, localField);
    assertFalse(context.isGlobal(dst));
    assertFalse(context.isInjectable(dst));
    assertEquals(0, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

  public void testFieldReadInjectableInstance() throws Exception {
    context.setInjectable(instance);
    context.localAssignment(method, 1, dst, localField);
    assertFalse(context.isGlobal(dst));
    assertFalse(context.isInjectable(dst));
    assertEquals(0, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

  public void testFieldReadInjectableField() throws Exception {
    context.setInjectable(field);
    context.localAssignment(method, 1, dst, localField);
    assertFalse(context.isGlobal(dst));
    assertTrue(context.isInjectable(dst));
    assertEquals(0, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

  public void testFieldReadGlobalInstance() throws Exception {
    context.setGlobal(instance);
    context.localAssignment(method, 1, dst, localField);
    assertTrue(context.isGlobal(dst));
    assertFalse(context.isInjectable(dst));
    assertEquals(1, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

  public void testFinalFieldReadGlobalInstance() throws Exception {
    context.setGlobal(instance);
    context.localAssignment(method, 1, dst, localFinalField);
    assertTrue(context.isGlobal(dst));
    assertFalse(context.isInjectable(dst));
    assertEquals(0, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

  public void testReadFinalStaticField() throws Exception {
    context.localAssignment(method, 1, dst, localStaticFinalField);
    //TODO: this fails because the usage of the Field class and related classes is wrong.
    assertTrue(context.isGlobal(dst));
    assertFalse(context.isInjectable(dst));
    assertEquals(0, context.getLinkedMethodCost(method).getTotalGlobalCost());
  }

//  public void testGrayListCostOverridesActualCost() throws Exception {
//    ClassRepository repo = null;
//    PrintStream err = null;
//    GrayList graylist = new GrayList();
//    graylist.addCost("c.g.t.A", 20);
//    CostModel costModel = null;
//    TestabilityContext context = new TestabilityContext(repo, err, graylist, costModel);
//    MethodCost cost = context.getMethodCost(method);
//    assertEquals(0, method.getTestCost());
//    assertEquals(20, cost.getCyclomaticCost());
//    assertTrue(context.methodAlreadyVisited(method));
//      fail();
//  }

}
