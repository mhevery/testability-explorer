// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric.report;

import com.google.test.metric.CostUtil;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassSourceReportTestExample {
  @SuppressWarnings("unused")
private Integer foo = 1;

  static {
    CostUtil.staticCost1();
  }

  {
    CostUtil.staticCost2();
  }

  public ClassSourceReportTestExample() {
    CostUtil.staticCost3();
  }

  public void doSomething() {
    CostUtil.staticCost4();
  }
}
