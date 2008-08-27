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

import com.google.test.metric.LocalField;
import com.google.test.metric.Variable;
import com.google.test.metric.ast.FieldHandle;

import java.util.List;

public class GetField extends StackOperation {

  private final FieldHandle fieldHandle;

  public GetField(int lineNumber, FieldHandle fieldHandle) {
    super(lineNumber);
    this.fieldHandle = fieldHandle;
  }

  @Override
  public String toString() {
    return "get " + (fieldHandle.isGlobal() ? "static " : "") + fieldHandle;
  }

  @Override
  public int getOperatorCount() {
    return fieldHandle.isGlobal() ? 0 : 1;
  }

  @Override
  public List<Variable> apply(List<Variable> input) {
    Variable instance = fieldHandle.isGlobal() ? null : input.get(0);
    return list(new LocalField(instance, fieldHandle));
    //return list(fieldHandle);
  }

}
