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
package com.google.test.metric.method.op.stack;

import java.util.List;

import com.google.test.metric.JavaType;
import com.google.test.metric.Variable;
import com.google.test.metric.method.op.turing.LocalAssignment;
import com.google.test.metric.method.op.turing.Operation;

public class Store extends StackOperation {

  private final Variable variable;

  public Store(int lineNumber, Variable variable) {
    super(lineNumber);
    this.variable = variable;
  }

  @Override
  public String toString() {
    return "store " + variable;
  }

  @Override
  public int getOperatorCount() {
    return JavaType.isDoubleSlot(variable.getType()) ? 2 : 1;
  }

  @Override
  public Operation toOperation(List<Variable> input) {
    return new LocalAssignment(lineNumber, variable, input.get(0));
  }

}
