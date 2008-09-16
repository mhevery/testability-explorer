/*
 * Copyright 2008 Google Inc.
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
package com.google.test.metric.cpp;

import com.google.test.metric.cpp.dom.CaseStatement;
import com.google.test.metric.cpp.dom.FunctionDefinition;
import com.google.test.metric.cpp.dom.IfStatement;
import com.google.test.metric.cpp.dom.LoopStatement;
import com.google.test.metric.cpp.dom.Visitor;

public class CyclomaticComplexityAnalyzer extends Visitor {
  private int score;

  public int getScore() {
    return score;
  }

  @Override
  public void beginVisit(FunctionDefinition functionDefinition) {
    score = 1;
  }

  @Override
  public void beginVisit(IfStatement ifStatement) {
    ++score;
  }

  @Override
  public void beginVisit(CaseStatement caseStatement) {
    ++score;
  }

  @Override
  public void beginVisit(LoopStatement loopStatement) {
    ++score;
  }
}
