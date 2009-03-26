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
  /**
   * Ugliness:
   * When a Java 1.5 enhanced switch statement is used on an enum, a synthetic
   * class is created as an anonymous inner class of the class where the
   * switch statement appears. That synthetic class stores a statuc array of the values
   * of the enum, which is named {@code $SwitchMap$com$google$AnEnum}. 
   * We don't want to record the assignments into this JVM-internal array.
   */
  private final String ENUM_SWITCH_MAP_NAME = "$SwitchMap$";
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
  public void visit(TestabilityVisitor.Frame visitor) {
    if (!array.getName().startsWith(ENUM_SWITCH_MAP_NAME)) {
      visitor.assignArray(array, index, value, getLineNumber());
    }
  }

  @Override
  public String toString() {
    return array + "[" + index + "] <- " + value;
  }

}
