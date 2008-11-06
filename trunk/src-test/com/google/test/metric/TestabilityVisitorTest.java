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

import com.google.test.metric.TestabilityVisitor.Frame;

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
  @SuppressWarnings("unchecked")
  ClassInfo classInfo = new ClassInfo("c.g.t.A", false, null, EMPTY_LIST);
  MethodInfo method =
      new MethodInfo(classInfo, "method", 0, "()V", null, null, null, null, cost1, null, false);

  private final JavaClassRepository repo = new JavaClassRepository();

  TestabilityVisitor visitor =
    new TestabilityVisitor(repo, null, new RegExpWhiteList(), new CostModel());

  private String method(String string, Class<?> clazz) {
    return "execute(L"+clazz.getName().replace(".", "/")+";)V";
  }

  public void testIsInjectable() throws Exception {
    Variable var = new Variable("", Type.fromJava("X"), false, false);
    assertFalse(visitor.isInjectable(var));
    visitor.setInjectable(var);
    assertTrue(visitor.isInjectable(var));
  }

  public void testNoop() throws Exception {
    visitor.assignParameter(method, 1, dst, instance);
    assertFalse(visitor.isGlobal(dst));
    assertFalse(visitor.isInjectable(dst));
    assertEquals(0, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
  }

  public void testInjectability() throws Exception {
    visitor.setInjectable(instance);
    visitor.assignParameter(method, 1, dst, instance);
    assertFalse(visitor.isGlobal(dst));
    assertTrue(visitor.isInjectable(dst));
    assertEquals(0, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
  }

  public void testFieldReadNoop() throws Exception {
    visitor.assignParameter(method, 1, dst, localField);
    assertFalse(visitor.isGlobal(dst));
    assertFalse(visitor.isInjectable(dst));
    assertEquals(0, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
  }

  public void testFieldReadInjectableInstance() throws Exception {
    visitor.setInjectable(instance);
    visitor.assignParameter(method, 1, dst, localField);
    assertFalse(visitor.isGlobal(dst));
    assertFalse(visitor.isInjectable(dst));
    assertEquals(0, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
  }

  public void testFieldReadInjectableField() throws Exception {
    visitor.setInjectable(field);
    visitor.assignParameter(method, 1, dst, localField);
    assertFalse(visitor.isGlobal(dst));
    assertTrue(visitor.isInjectable(dst));
    assertEquals(0, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
  }

  public void testFieldReadGlobalInstance() throws Exception {
    visitor.setGlobal(instance);
    visitor.assignParameter(method, 1, dst, localField);
    assertTrue(visitor.isGlobal(dst));
    assertFalse(visitor.isInjectable(dst));
    assertEquals(1, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
  }

  public void testFinalFieldReadGlobalInstance() throws Exception {
    visitor.setGlobal(instance);
    visitor.assignParameter(method, 1, dst, localFinalField);
    assertTrue(visitor.isGlobal(dst));
    assertFalse(visitor.isInjectable(dst));
    assertEquals(0, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
  }

  public void testReadFinalStaticField() throws Exception {
    visitor.assignParameter(method, 1, dst, localStaticFinalField);
    assertTrue(visitor.isGlobal(dst));
    assertFalse(visitor.isInjectable(dst));
    assertEquals(0, visitor.getLinkedMethodCost(method).getTotalCost().getGlobalCost());
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
    Frame frame = visitor.applyMethodOperations(methodInfo);
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
    Frame frame = visitor.applyMethodOperations(methodInfo);
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
    Frame frame = visitor.applyMethodOperations(methodInfo);
    assertEquals(2, frame.getLoDCount(clazz.getField("plus2")));
    MethodCost methodCost = visitor.getLinkedMethodCost(methodInfo);
    List<LoDViolation> costSources = filterLoD(methodCost.getViolationCosts());
    assertEquals(1, costSources.size());
    LoDViolation violation = costSources.get(0);
    assertTrue(violation.getDescription().contains("getValueB()"));
    assertTrue(violation.getDescription().contains("[distance=2]"));
  }

  private List<LoDViolation> filterLoD(List<ViolationCost> violations) {
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
    visitor.setInjectable(methodInfo.getParameters().get(0));
    visitor.applyMethodOperations(methodInfo);
    assertTrue(visitor.isInjectable(clazz.getField("injectable1")));
    assertTrue(visitor.isInjectable(clazz.getField("injectable2")));
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
    Frame frame = visitor.applyMethodOperations(methodInfo);
    assertEquals(1, frame.getLoDCount(clazz.getField("plus1")));
  }

}

