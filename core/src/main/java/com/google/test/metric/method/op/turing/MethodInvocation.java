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

import java.util.List;

public class MethodInvocation extends Operation {

  private final String name;
  private final String clazzName;
  private final Variable methodThis;
  private final List<Variable> parameters;
  private final Variable returnVariable;

  public MethodInvocation(int lineNumber, String clazz, String name,
      Variable methodThis, List<Variable> parameters,
      Variable returnVariable) {
    super(lineNumber);
    this.clazzName = clazz;
    this.name = name;
    this.methodThis = methodThis;
    this.parameters = parameters;
    this.returnVariable = returnVariable;
  }

  public List<Variable> getParameters() {
    return parameters;
  }

  public String getName() {
    return name;
  }

  public String getOwner() {
    return clazzName;
  }

  @Override
  public String toString() {
    return clazzName + ":" + name;
  }

  @Override
  public void visit(TestabilityVisitor.Frame visitor) {
    visitor.recordMethodCall(clazzName, getLineNumber(), name,
        methodThis, parameters, returnVariable);
  }

  public Variable getMethodThis() {
    return methodThis;
  }

  /**
   * @return true iff this is an invocation of methodName in a class className
   */
  public boolean equals(String className, String methodName) {
    return className.equals(clazzName) && methodName.equals(name);
  }

  public String getClazzName() {
    return clazzName;
  }
}
