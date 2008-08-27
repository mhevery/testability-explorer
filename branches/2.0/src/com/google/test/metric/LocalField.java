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
package com.google.test.metric;

import com.google.test.metric.ast.FieldHandle;
import com.google.test.metric.ast.VariableImpl;


public class LocalField extends VariableImpl {

  private final Variable instance;
  private final FieldHandle fieldHandle;

  public LocalField(Variable instance, FieldHandle fieldHandle) {
    super(fieldHandle.getName(), fieldHandle.getType(), fieldHandle.isFinal(),
        fieldHandle.isGlobal());
    this.instance = instance;
    this.fieldHandle = fieldHandle;
  }

  @Override
  public String toString() {
    return fieldHandle.toString();
  }

  public FieldHandle getField() {
    return fieldHandle;
  }

  public Variable getInstance() {
    return instance;
  }

}
