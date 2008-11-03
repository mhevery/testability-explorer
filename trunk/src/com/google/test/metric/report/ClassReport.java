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

import com.google.test.metric.Cost;
import com.google.test.metric.WeightedAverage;


public class ClassReport extends SummaryGraphReport<ClassReport.MethodUnit> {

  public static class MethodUnit extends SummaryGraphReport.Unit {

    private final int lineNumber;
    private final Cost totalCost;
    private final Cost directCost;

    public MethodUnit(String methodName, int lineNumber, Cost totalCost,
        Cost directCost) {
      super(methodName, totalCost.getOverall());
      this.lineNumber = lineNumber;
      this.totalCost = totalCost;
      this.directCost = directCost;
    }

    public int getLineNumber() {
      return lineNumber;
    }

    public Cost getTotalCost() {
      return totalCost;
    }

    public Cost getDirectCost() {
      return directCost;
    }
  }

  private final Source source;

  public ClassReport(String name, Source source, GradeCategories grades, WeightedAverage average) {
    super(name, grades, average);
    this.source = source;
  }

  public Source getSource() {
    return source;
  }

  public void addMethod(String methodName, int lineNumber,
      Cost totalCost, Cost directCost) {
    addUnit(new MethodUnit(methodName, lineNumber, totalCost, directCost));
  }

}
