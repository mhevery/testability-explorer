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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableList;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Nullable;
import com.google.common.base.Predicate;
import com.google.test.metric.method.op.turing.Operation;

public class MethodInfo implements Comparable<MethodInfo> {

  private final ClassInfo classInfo;
  private final String name;
  private final Variable methodThis;
  private final List<ParameterInfo> parameters;
  private final List<LocalVariableInfo> localVariables;
  private final List<Integer> linesOfComplexity;
  private final Visibility visibility;
  private final List<Operation> operations;
  private final int startingLineNumber;
  private final boolean isFinal;

  private Predicate<? super MethodInfo> notSelf = new Predicate<MethodInfo>() {
    public boolean apply(@Nullable MethodInfo methodInfo) {
      return methodInfo != MethodInfo.this;
    }
  };
  private final boolean isConstructor;

  public MethodInfo(ClassInfo classInfo, String methodName,
      int startingLineNumber, Variable methodThis,
      List<ParameterInfo> parameters, List<LocalVariableInfo> localVariables,
      Visibility visibility, List<Operation> operations, boolean isFinal,
      boolean isConstructor, List<Integer> linesOfComplexity) {
    this.classInfo = classInfo;
    this.name = methodName;
    this.startingLineNumber = startingLineNumber;
    this.methodThis = methodThis;
    this.parameters = parameters;
    this.localVariables = localVariables;
    this.isConstructor = isConstructor;
    this.linesOfComplexity = linesOfComplexity;
    this.visibility = visibility;
    this.operations = operations;
    this.isFinal = isFinal;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getName() {
    return name;
  }

  public List<ParameterInfo> getParameters() {
    return parameters;
  }

  public List<LocalVariableInfo> getLocalVariables() {
    return localVariables;
  }

  public boolean isConstructor() {
    return isConstructor && !isStatic();
  }

  public Visibility getVisibility() {
    return visibility;
  }

  public List<Operation> getOperations() {
    return unmodifiableList(operations);
  }

  public boolean isStatic() {
    return methodThis == null;
  }

  public boolean isInstance() {
    return methodThis != null;
  }

  public boolean canOverride() {
    return !isConstructor() && !isStatic() && !isFinal
        && getVisibility() != Visibility.PRIVATE;
  }

  public Variable getMethodThis() {
    return methodThis;
  }

  public ClassInfo getClassInfo() {
    return classInfo;
  }

  /**
   * Not actually the starting line number of the method, but the first visited
   * line - see MethodVisitorBuilder for where it comes from.
   */
  public int getStartingLineNumber() {
    return startingLineNumber;
  }

  public boolean isStaticConstructor() {
    return isConstructor && isStatic();
  }

  public boolean isSetter() {
    return visibility != Visibility.PRIVATE && getName().startsWith("void set");
  }

  /**
   * @return Returns all methods in the same class which are setters, other than
   *         this setter
   */
  public Collection<MethodInfo> getSiblingSetters() {
    return newArrayList(filter(classInfo.getSetters(), notSelf));
  }

  public int compareTo(MethodInfo o) {
    if (o == null) {
      return -1;
    }
    return name.compareTo(o.name);
  }

  public int getNonPrimitiveArgCount() {
    int count = 0;
    for (ParameterInfo parameter : getParameters()) {
      if (parameter.getType().isObject()) {
        count++;
      }
    }
    return count;
  }

  public List<Integer> getLinesOfComplexity() {
    return linesOfComplexity;
  }

  public boolean isPrivate() {
    return getVisibility() == Visibility.PRIVATE;
  }

  /**
   * produces a copy of this method, which has
   *
   * @return
   */
  public MethodInfo copyWithNoOperations(ClassInfo parent) {
    List<Operation> operations = Collections.emptyList();
    return new MethodInfo(parent, name, startingLineNumber, methodThis,
        parameters, localVariables, visibility, operations, isFinal,
        isConstructor, linesOfComplexity);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((classInfo == null) ? 0 : classInfo.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MethodInfo other = (MethodInfo) obj;
    if (classInfo == null) {
      if (other.classInfo != null)
        return false;
    } else if (!classInfo.equals(other.classInfo))
      return false;
    if (name == null) {
      if (other.name != null)
        return false;
    } else if (!name.equals(other.name))
      return false;
    return true;
  }

}
