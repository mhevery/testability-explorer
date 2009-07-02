// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import junit.framework.TestCase;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.CostUtil;
import com.google.test.metric.JavaClassRepository;

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
    ClassInfo aClass = javaClassRepository.getClass(StaticInit.class.getCanonicalName());
    ClassInfo mungedClass = classMunger.getClassWithoutMethod(aClass.getName(), aClass.getMethod("<static init>()"));
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
    ClassInfo mungedClass = classMunger.getClassWithoutMethod(aClass.getName(), aClass.getMethod("void setInt(int)"));
    assertEquals(0, mungedClass.getMethod("void setInt(int)").getOperations().size());
  }

  static class SharedNonInjectableVariable {
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
