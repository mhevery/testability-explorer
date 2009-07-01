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


import static java.util.Collections.EMPTY_LIST;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import com.google.test.metric.TestabilityVisitor.CostRecordingFrame;
import com.google.test.metric.TestabilityVisitor.ParentFrame;

public class TestabilityVisitorTest extends TestCase {

  final List<Integer> cost1 = Arrays.asList(0);
  Variable instance = new Variable("instance", null, false, false);
  Variable finalInstance = new Variable("instance", null, true, false);
  FieldInfo field = new FieldInfo(null, "field", null, false, false, false);
  FieldInfo finalField = new FieldInfo(null, "field", null, true, false, false);
  FieldInfo finalStaticField = new FieldInfo(null, "field", null, true, true, false);
  LocalField localField = new LocalField(instance, field);
  LocalField localFinalField = new LocalField(instance, finalField);
  LocalField localStaticFinalField = new LocalField(null, finalStaticField);
  Variable dst = new Variable("dst", null, false, false);
  Variable dstField = new FieldInfo(null, "dstField", null, false, false, false);
  @SuppressWarnings("unchecked")
  ClassInfo classInfo = new ClassInfo("c.g.t.A", false, null, EMPTY_LIST, null);
  MethodInfo method =
      new MethodInfo(classInfo, "method", 0, "()V", null, null, null, null, cost1, null, false);

  private final JavaClassRepository repo = new JavaClassRepository();
  private final VariableState globalVariables = new VariableState();

  TestabilityVisitor visitor =
    new TestabilityVisitor(repo, globalVariables, null, new RegExpWhiteList());
  TestabilityVisitor.CostRecordingFrame frame = visitor.createFrame(method, 1);
  ParentFrame parentFrame = frame.getParentFrame();

  private String method(String string, Class<?> clazz) {
    return "execute(L"+clazz.getName().replace(".", "/")+";)V";
  }

  public void testIsInjectable() throws Exception {
    Variable var = new Variable("", JavaType.fromJava("X"), false, false);
    assertFalse(globalVariables.isInjectable(var));
    globalVariables.setInjectable(var);
    assertTrue(globalVariables.isInjectable(var));
  }

  public void testNoop() throws Exception {
    frame.assignParameter(1, dst, parentFrame, instance);
    assertFalse(globalVariables.isGlobal(dst));
    assertFalse(globalVariables.isInjectable(dst));
    assertEquals(0, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  public void testInjectability() throws Exception {
    globalVariables.setInjectable(instance);
    frame.assignParameter(1, dst, parentFrame, instance);
    assertFalse(globalVariables.isGlobal(dst));
    assertTrue(frame.getVariableState().isInjectable(dst));
    assertEquals(0, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  public void testFieldReadNoop() throws Exception {
    frame.assignParameter(1, dst, parentFrame, localField);
    assertFalse(globalVariables.isGlobal(dst));
    assertFalse(globalVariables.isInjectable(dst));
    assertEquals(0, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  public void testFieldReadInjectableInstance() throws Exception {
    globalVariables.setInjectable(instance);
    frame.assignParameter(1, dst, parentFrame, localField);
    assertFalse(globalVariables.isGlobal(dst));
    assertFalse(globalVariables.isInjectable(dst));
    assertEquals(0, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  public void testFieldReadInjectableField() throws Exception {
    globalVariables.setInjectable(field);
    frame.assignParameter(1, dst, parentFrame, localField);
    assertFalse(globalVariables.isGlobal(dst));
    assertTrue(frame.getVariableState().isInjectable(dst));
    assertEquals(0, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  public void testFieldReadGlobalInstance() throws Exception {
    globalVariables.setGlobal(instance);
    frame.assignParameter(1, dstField, parentFrame, localField);
    assertTrue(globalVariables.isGlobal(dstField));
    assertFalse(globalVariables.isInjectable(dstField));
    assertEquals(1, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  public void testFinalFieldReadGlobalInstance() throws Exception {
    globalVariables.setGlobal(instance);
    frame.assignParameter(1, dstField, parentFrame, localFinalField);
    assertTrue(globalVariables.isGlobal(dstField));
    assertFalse(globalVariables.isInjectable(dstField));
    assertEquals(0, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  public void testReadFinalStaticField() throws Exception {
    frame.assignParameter(1, dstField, parentFrame, localStaticFinalField);
    assertTrue(globalVariables.isGlobal(dstField));
    assertFalse(globalVariables.isInjectable(dstField));
    assertEquals(0, frame.getMethodCost().getTotalCost().getGlobalCost());
  }

  private static class LoDExample {

    Object conforming;
    Object violator;
    Object transitiveViolator;

    public void assign(Object in) {
      conforming = in;
      violator = in.getClass();
      transitiveViolator = violator;
    }
  }

  public void testLoDExample() throws Exception {
    ClassInfo clazz = repo.getClass(LoDExample.class.getName());
    MethodInfo methodInfo = clazz.getMethod("assign(Ljava/lang/Object;)V");
    CostRecordingFrame frame = visitor.createFrame(methodInfo, 1);
    frame.applyMethodOperations();
    assertEquals(0, frame.getLoDCount(clazz.getField("conforming")));
    assertEquals(1, frame.getLoDCount(clazz.getField("violator")));
    assertEquals(1, frame.getLoDCount(clazz.getField("transitiveViolator")));
  }

  private static class LoDMultipleSameInvocations {
    Obj plus2;
    public void execute(Obj plus0) {
      Obj plus1 = plus0.getValueA();
      plus2 = plus1.getValueA();
      plus2 = plus1;
    }
  }

  public void testLoDMultipleSameInvocations() throws Exception {
    ClassInfo clazz = repo.getClass(LoDMultipleSameInvocations.class.getName());
    MethodInfo methodInfo = clazz.getMethod(method("execute", Obj.class));
    CostRecordingFrame frame = visitor.createFrame(methodInfo, 1);
    frame.applyMethodOperations();
    assertEquals(2, frame.getLoDCount(clazz.getField("plus2")));
  }

  private static class LoDMultipleDifferentInvocations {
    Obj plus2;
    public void execute(Obj plus0) {
      Obj plus1 = plus0.getValueA();
      plus2 = plus1.getValueB();
      plus2 = plus1;
    }
  }

  public void testLoDMultipleDifferentInvocations() throws Exception {
    ClassInfo clazz = repo.getClass(LoDMultipleDifferentInvocations.class.getName());
    MethodInfo methodInfo = clazz.getMethod(method("execute", Obj.class));
    CostRecordingFrame frame = visitor.createFrame(methodInfo, 1);
    frame.applyMethodOperations();
    assertEquals(2, frame.getLoDCount(clazz.getField("plus2")));
    MethodCost methodCost = frame.getMethodCost();
    List<LoDViolation> costSources = filterLoD(methodCost.getViolationCosts());
    assertEquals(1, costSources.size());
    LoDViolation violation = costSources.get(0);
    assertTrue(violation.getDescription().contains("getValueB()"));
    assertTrue(violation.getDescription().contains("[distance=2]"));
  }

  private List<LoDViolation> filterLoD(List<? extends ViolationCost> violations) {
    List<LoDViolation> lods = new ArrayList<LoDViolation>();
    for (ViolationCost violation : violations) {
      if (violation instanceof LoDViolation) {
        lods.add((LoDViolation) violation);
      }
    }
    return lods;
  }

  private static class MultipleInjectability {
    private Obj injectable1;
    private Obj injectable2;
    public void execute(Obj injectable) {
      injectable1 = injectable.getValueA();
      injectable2 = injectable.getValueA();
    }
    public boolean useFields() {
      return injectable1 == injectable2;
    }
  }

  public void testMultipleInjectability() throws Exception {
    ClassInfo clazz = repo.getClass(MultipleInjectability.class.getName());
    MethodInfo methodInfo = clazz.getMethod(method("execute", Obj.class));
    globalVariables.setInjectable(methodInfo.getParameters().get(0));
    visitor.createFrame(methodInfo, 1).applyMethodOperations();
    assertTrue(frame.getVariableState().isInjectable(clazz.getField("injectable1")));
    assertTrue(frame.getVariableState().isInjectable(clazz.getField("injectable2")));
  }

  private static class LoDStaticCall {
    Obj plus1;
    public void execute() {
      plus1 = Obj.getStaticValueA();
    }
  }

  public void testLoDStaticCall() throws Exception {
    ClassInfo clazz = repo.getClass(LoDStaticCall.class.getName());
    MethodInfo methodInfo = clazz.getMethod("execute()V");
    CostRecordingFrame frame = visitor.createFrame(methodInfo, 1);
    frame.applyMethodOperations();
    assertEquals(1, frame.getLoDCount(clazz.getField("plus1")));
  }

}

