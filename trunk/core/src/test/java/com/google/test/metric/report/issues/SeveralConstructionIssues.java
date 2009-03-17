package com.google.test.metric.report.issues;

import com.google.test.metric.example.Cost2ToConstruct;
import com.google.test.metric.CostUtil;

/**
 * @author alexeagle@google.com (Alex Eagle)
*/
class SeveralConstructionIssues {
  @SuppressWarnings("unused")
  private final Cost2ToConstruct nonInjectable1;

  public SeveralConstructionIssues() {
    // Contributes 3
    CostUtil.staticCost3();
    // Contributes 2
    int a = 0;
    @SuppressWarnings("unused")
    int b = a > 5 ? 3 : 5;
    b = a < 4 ? 4 : 3;
    // Contributes 2
    nonInjectable1 = new Cost2ToConstruct();
  }
}
