// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.CostUtil;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.method.op.turing.MethodInvocation;
import com.google.test.metric.method.op.turing.Operation;

import junit.framework.TestCase;

import java.text.DateFormat;
import java.util.List;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassMungerTest extends TestCase {

  private JavaClassRepository javaClassRepository;
  private ClassMunger classMunger;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    javaClassRepository = new JavaClassRepository();
    classMunger = new ClassMunger(javaClassRepository);
  }

  private static class StaticInit {
    static {
      CostUtil.staticCost3();
    }
  }

  public void testClassMungerCanRemoveAMethodCall() throws Exception {
    ClassInfo aClass = javaClassRepository.getClass(StaticInit.class.getCanonicalName());
    ClassInfo mungedClass = classMunger.getClassWithoutMethod(aClass.getName(), "<static init>()");
    assertEquals(0, mungedClass.getMethod("<static init>()").getOperations().size());
  }

  static class HasSetterCost {
    public void setInt(int int1) {
      new CostUtil().instanceCost4();
    }
  }

  static class SuperClassHasSetterCost extends HasSetterCost {

  }

  public void testMungeSuperclassSetterEliminatesSetterCost() throws Exception {
    ClassInfo aClass = javaClassRepository.getClass(SuperClassHasSetterCost.class.getCanonicalName());
    assertEquals(2, aClass.getMethod("void setInt(int)").getOperations().size());
    ClassInfo mungedClass = classMunger.getClassWithoutMethod(aClass.getName(), "void setInt(int)");
    assertEquals(0, mungedClass.getMethod("void setInt(int)").getOperations().size());
  }

  static class HasInvocation {
    public void method() {
      new CostUtil().instanceCost0();
      new HasSetterCost().setInt(1);
    }
  }

  public void testClassMungerCanRemoveIndividualInvocation() throws Exception {
    ClassInfo aClass = javaClassRepository.getClass(HasInvocation.class.getCanonicalName());
    assertEquals(4, aClass.getMethod("void method()").getOperations().size());
    List<Operation> list = aClass.getMethod("void method()").getOperations();
    MethodInvocation invocation = (MethodInvocation) list.get(3);
    ClassInfo mungedClass = classMunger.getClassWithoutInvocation(aClass.getName(), "void method()",
        invocation.getClazzName(), invocation.getName());
    assertEquals(3, mungedClass.getMethod("void method()").getOperations().size());
  }

  static class HasInvocationReturningValue {
    public void method() {
      new CostUtil().instanceCost0();
      DateFormat.getDateInstance().toString();
    }
  }

  public void testClassMungerCanRemoveAnInvocationThatReturnsAValue() throws Exception {
    ClassInfo aClass = javaClassRepository.getClass(HasInvocationReturningValue.class.getCanonicalName());
    assertEquals(4, aClass.getMethod("void method()").getOperations().size());
    List<Operation> list = aClass.getMethod("void method()").getOperations();
    MethodInvocation invocation = (MethodInvocation) list.get(2);
    assertEquals("java.text.DateFormat getDateInstance()", invocation.getName());
    MetricComputer computer =
        new MetricComputer(new JavaClassRepository(), null, new RegExpWhiteList(), 1);
    long originalCost = computer.compute(aClass).getTotalComplexityCost();
    ClassInfo mungedClass = classMunger.getClassWithoutInvocation(aClass.getName(), "void method()",
        invocation.getClazzName(), invocation.getName());
    assertEquals(3, mungedClass.getMethod("void method()").getOperations().size());
    long mungedCost = computer.compute(mungedClass).getTotalComplexityCost();
    assertTrue(mungedCost + "<" +  originalCost, mungedCost < originalCost);

  }
}
