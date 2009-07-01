// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.MethodInfo;

/**
 * Modifies class info's
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassMunger {

  private final ClassRepository repo;

  public ClassMunger(ClassRepository repo) {
    this.repo = repo;
  }

  public ClassInfo getClassWithoutMethod(String className, MethodInfo method) {
    ClassInfo munged = repo.getClass(className).copy();
    munged.addMethod(method.copyWithNoOperations(munged));
    return munged;
  }
}
