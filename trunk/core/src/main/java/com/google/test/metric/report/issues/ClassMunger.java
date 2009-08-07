// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.MethodInfo;

import java.util.Collection;
import java.util.List;

/**
 * Modifies class info's
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassMunger {

  private final ClassRepository repo;

  @Inject
  public ClassMunger(ClassRepository repo) {
    this.repo = repo;
  }

  /**
   * Get a representation of the class, with an empty method in place of this method.
   * @param className class to load
   * @param methodName the method to remove operations from
   * @return the munged class
   */
  public ClassInfo getClassWithoutMethod(String className, String methodName) {
    ClassInfo munged = repo.getClass(className).copy();
    MethodInfo method = munged.getMethod(methodName);
    munged.addMethod(method.copyWithNoOperations(munged));
    return munged;
  }

  /**
   * Get a representation of the class, with no cyclomatic complexity in this method.
   * @param className class to load
   * @param methodName method to make zero complexity
   * @return the munged class
   */
  public ClassInfo getClassWithZeroDirectCostInMethod(String className, String methodName) {
    ClassInfo munged = repo.getClass(className).copy();
    MethodInfo method = munged.getMethod(methodName);
    munged.addMethod(method.copyWithNoDirectCost(munged));
    return munged;
  }

  /**
   * Get a representation of the class, with a method modified to make no invocations of some other method.
   * @param callingClassName class to load
   * @param callingMethodName method to modify
   * @param invokedClassName class containing the method that will no longer be called
   * @param invokedMethodName method that will no longer be called
   * @return the munged class
   */
  public ClassInfo getClassWithoutInvocation(String callingClassName, String callingMethodName,
                                             String invokedClassName, final String invokedMethodName) {
    ClassInfo original = repo.getClass(callingClassName);
    ClassInfo munged = new ClassInfo(original.getName(), original.isInterface(), original.getSuperClass(), original.getInterfaces(), original.getFileName()) {
      @Override
      public Collection<MethodInfo> getSetters() {
        List<MethodInfo> result = Lists.newLinkedList();
        for (MethodInfo setter : super.getSetters()) {
          if (!setter.getName().equals(invokedMethodName)) {
            result.add(setter);
          }
        }
        return result;
      }
    };
    for (MethodInfo methodInfo : original.getMethods()) {
      munged.addMethod(methodInfo);
    }
    MethodInfo method = munged.getMethod(callingMethodName);
    munged.addMethod(method.copyWithoutInvocation(munged, invokedClassName, invokedMethodName));
    return munged;
  }
}
