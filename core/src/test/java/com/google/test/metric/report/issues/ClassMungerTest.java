// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.CostUtil;
import com.google.test.metric.JavaClassRepository;

import junit.framework.TestCase;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassMungerTest extends TestCase {

  private JavaClassRepository javaClassRepository;
  private ClassMunger classMunger;

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
    ClassInfo aClass = javaClassRepository.getClass(StaticInit.class.getName());
    ClassInfo mungedClass = classMunger.getClassWithoutMethod(aClass.getName(), aClass.getMethod("<clinit>()V"));
    assertEquals(0, mungedClass.getMethod("<clinit>()V").getOperations().size());
  }

  static class HasSetterCost {
    public void setInt(int int1) {
      new CostUtil().instanceCost4();
    }
  }

  static class SuperClassHasSetterCost extends HasSetterCost {

  }

  public void testMungeSuperclassSetterEliminatesSetterCost() throws Exception {
    ClassInfo aClass = javaClassRepository.getClass(SuperClassHasSetterCost.class.getName());
    assertEquals(2, aClass.getMethod("setInt(I)V").getOperations().size());
    ClassInfo mungedClass = classMunger.getClassWithoutMethod(aClass.getName(), aClass.getMethod("setInt(I)V"));
    assertEquals(0, mungedClass.getMethod("setInt(I)V").getOperations().size());
  }

  private static class SharedNonInjectableVariable {
    private CostUtil costUtil;

    public SharedNonInjectableVariable() {
      costUtil = new CostUtil();
      costUtil.instanceCost4();
    }

    public int doThing() {
      costUtil.instanceCost3();
      return 1;
    }
  }

}
