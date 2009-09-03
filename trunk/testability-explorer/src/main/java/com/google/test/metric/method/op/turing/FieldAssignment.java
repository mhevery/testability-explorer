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

import com.google.test.metric.FieldInfo;
import com.google.test.metric.TestabilityVisitor;
import com.google.test.metric.Variable;

public class FieldAssignment extends Operation {

  private final Variable fieldInstance;
  private final FieldInfo field;
  private final Variable value;

  public FieldAssignment(int lineNumber, Variable fieldInstance,
      FieldInfo field, Variable value) {
    super(lineNumber);
    this.fieldInstance = fieldInstance;
    this.field = field;
    this.value = value;
  }

  public Variable getFieldInstance() {
    return fieldInstance;
  }

  public FieldInfo getField() {
    return field;
  }

  public Variable getValue() {
	return value;
  }

  @Override
  public void visit(TestabilityVisitor.Frame visitor) {
    visitor.assignField(fieldInstance, field, value, getLineNumber());
  }

  @Override
  public String toString() {
    return field + " <- " + value;
  }
}
