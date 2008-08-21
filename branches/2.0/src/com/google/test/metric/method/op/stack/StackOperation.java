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

import com.google.test.metric.Variable;
import com.google.test.metric.method.op.turing.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class StackOperation {

  protected final int lineNumber;

  public StackOperation(int lineNumber) {
    this.lineNumber = lineNumber;
  }

  /**
   * Stack transformation operation.
   * @param input list of operands which this operations consumes on stack
   * @return a list of operands which get pushed back on stack.
   */
  public List<Variable> apply(List<Variable> input) {
    return Collections.emptyList();
  }

  public int getOperatorCount() {
    return 0;
  }

  /**
   * High level Turing Operations which get produced from the stack operations
   *
   * @param input
   * @return null if no operations; Turing Operation otherwise
   */
  public Operation toOperation(List<Variable> input) {
    return null;
  }

  protected List<Variable> list(Variable... vars) {
    ArrayList<Variable> list = new ArrayList<Variable>(vars.length);
    for (Variable variable : vars) {
      list.add(variable);
      if (variable.getType().isDoubleSlot()) {
        list.add(variable);
      }
    }
    return list;
  }
}