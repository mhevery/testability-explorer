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

import com.google.test.metric.JavaType;
import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.Constant;
import com.google.test.metric.method.op.turing.MethodInvocation;
import com.google.test.metric.method.op.turing.Operation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Invoke extends StackOperation {

  private final String clazz;
  private final String name;
  private final String signature;
  private final boolean isStatic;
  private final Type returnType;
  private final List<Type> params;
  private final Variable returnValue;

  public Invoke(int lineNumber, String clazz, String name, String signature,
      List<Type> params, boolean isStatic, Type returnType) {
    super(lineNumber);
    this.clazz = clazz;
    this.name = name;
    this.signature = signature;
    this.params = params;
    this.isStatic = isStatic;
    this.returnType = returnType;
    this.returnValue = new Constant("?", returnType);
  }

  @Override
  public int getOperatorCount() {
    int count = isStatic ? 0 : 1;
    for (Type type : params) {
      count += JavaType.isDoubleSlot(type) ? 2 : 1;
    }
    return count;
  }

  @Override
  public List<Variable> apply(List<Variable> input) {
    if (returnType == JavaType.VOID) {
      return Collections.emptyList();
    } else {
      return list(returnValue);
    }
  }

  @Override
  public Operation toOperation(List<Variable> input) {
    List<Variable> parameters = removeDuplicateSlots(input);
    Variable methodThis = isStatic ? null : parameters.remove(0);
    return new MethodInvocation(lineNumber, clazz, name, signature,
        methodThis, parameters, returnValue);
  }

  private List<Variable> removeDuplicateSlots(List<Variable> input) {
    List<Variable> parameters = new ArrayList<Variable>();
    boolean skip = false;
    for (Variable variable : input) {
      if (skip) {
        skip = false;
        continue;
      }
      if (JavaType.isDoubleSlot(variable.getType())) {
        skip = true;
      }
      parameters.add(variable);
    }
    return parameters;
  }

  @Override
  public String toString() {
    return (isStatic ? "invokestatic " : "invoke ") + clazz + "." + name
        + signature + (returnType == null ? "" : " : " + returnType);
  }

}
