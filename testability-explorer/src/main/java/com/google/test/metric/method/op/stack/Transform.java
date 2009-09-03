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
import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.Constant;

public class Transform extends StackOperation {

  private final String operation;
  private final Type op1;
  private final Type op2;
  private final Type result;

  public Transform(int lineNumber, String operation, Type op1, Type op2,
      Type result) {
    super(lineNumber);
    this.operation = operation;
    this.op1 = op1;
    this.op2 = op2;
    this.result = result;
  }

  @Override
  public int getOperatorCount() {
    return size(op1) + size(op2);
  }

  private int size(Type op) {
    return op == null ? 0 : JavaType.isDoubleSlot(op) ? 2 : 1;
  }

  @Override
  public List<Variable> apply(List<Variable> input) {
    if (result == null) {
      return super.apply(input);
    } else {
      return list(new Constant("?", result));
    }
  }

  @Override
  public String toString() {
    String sep = " ";
    String buf = operation;
    if (op1 != null) {
      buf += sep + op1;
      sep = ", ";
    }
    if (op2 != null) {
      buf += sep + op2;
    }
    if (result != null) {
      buf += " -> " + result;
    }
    return buf;
  }

}
