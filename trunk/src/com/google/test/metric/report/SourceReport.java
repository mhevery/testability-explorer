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
package com.google.test.metric.report;

import com.google.test.metric.ClassCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.ViolationCost;
import com.google.test.metric.report.Source.Line;

public class SourceReport extends SummaryReport {

  private final SourceLoader sourceLoader;
  private final GradeCategories grades;

  // TODO: Refactor this constructor
  public SourceReport(int maxExcellentCost, int maxAcceptableCost,
      int worstOffenderCount, SourceLoader sourceLoader) {
    super(maxExcellentCost, maxAcceptableCost, worstOffenderCount);
    this.sourceLoader = sourceLoader;
    grades = new GradeCategories(maxExcellentCost, maxAcceptableCost);
  }

  public void printFooter() {
    throw new UnsupportedOperationException();
  }

  public void printHeader() {
    throw new UnsupportedOperationException();
  }

  ClassReport createClassReport(ClassCost classCost) {
    Source source = sourceLoader.load(classCost.getClassName());
    for (MethodCost methodCost : classCost.getMethods()) {
      Line line = source.getLine(methodCost.getMethodLineNumber());
      line.addMethodCost(methodCost);
      for (ViolationCost violation : methodCost.getViolationCosts()) {
        line = source.getLine(violation.getLineNumber());
        line.addCost(violation.getCost());
      }
    }
    return new ClassReport(source, classCost, grades);
  }

}
