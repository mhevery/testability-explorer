// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report.issues;

import com.google.test.metric.CostUtil;

/**
 * a simple class with cost in a setter
 * @author alexeagle@google.com (Alex Eagle)
 */
public class HasSetterCost {
  public void setFoo(int foo) {
    new CostUtil().instanceCost4();
  }
}
