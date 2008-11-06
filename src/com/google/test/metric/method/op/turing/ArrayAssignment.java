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
package com.google.test.metric.method.op.turing;

import com.google.test.metric.TestabilityVisitor;
import com.google.test.metric.Variable;

public class ArrayAssignment extends Operation {

  private final Variable array;
  private final Variable index;
  private final Variable value;

  public ArrayAssignment(int lineNumber, Variable array, Variable index,
      Variable value) {
    super(lineNumber);
    this.array = array;
    this.index = index;
    this.value = value;
  }

  @Override
  public void visit(TestabilityVisitor.Frame visistor) {
    visistor.assignArray(array, index, value, getLineNumber());
  }

  @Override
  public String toString() {
    return array + "[" + index + "] <- " + value;
  }

}
