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

import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.op.turing.Operation;
import com.google.test.metric.method.op.turing.ReturnOperation;

import java.util.List;

public class Return extends Pop {

  private final Type type;

  public Return(int lineNumber, Type type) {
    super(lineNumber, type == Type.VOID ? 0 : (type.isDoubleSlot() ? 2 : 1));
    this.type = type;
  }

  @Override
  public String toString() {
    return "return " + type;
  }

  @Override
  public Operation toOperation(List<Variable> input) {
    if (type == Type.VOID) {
      return super.toOperation(input);
    } else {
      return new ReturnOperation(lineNumber, input.get(0));
    }
  }
}
