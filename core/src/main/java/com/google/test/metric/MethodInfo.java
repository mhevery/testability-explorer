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

import com.google.common.base.Nullable;
import com.google.common.base.Predicate;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import com.google.test.metric.method.op.turing.Operation;

import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.unmodifiableList;
import java.util.List;

public class MethodInfo implements Comparable<MethodInfo> {

  private final ClassInfo classInfo;
  private final String name;
  private final Variable methodThis;
  private final List<ParameterInfo> parameters;
  private final List<LocalVariableInfo> localVariables;
  private final String desc;
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

  public MethodInfo(ClassInfo classInfo, String methodName,
      int startingLineNumber, String desc, Variable methodThis,
      List<ParameterInfo> parameters, List<LocalVariableInfo> localVariables,
      Visibility visibility, List<Integer> linesOfComplexity,
      List<Operation> operations, boolean isFinal) {
    this.classInfo = classInfo;
    this.name = methodName;
    this.startingLineNumber = startingLineNumber;
    this.desc = desc;
    this.methodThis = methodThis;
    this.parameters = parameters;
    this.localVariables = localVariables;
    this.linesOfComplexity = linesOfComplexity;
    this.visibility = visibility;
    this.operations = operations;
    this.isFinal = isFinal;
  }

  public String getNameDesc() {
    return name + desc;
  }

  @Override
  public String toString() {
    return getFullName();
  }

  public String getName() {
    return name;
  }

  //TODO: Refactor this!
  public String getFullName() {
    int paramsEnd = desc.indexOf(')');
    String returnValue = deconstructParameters(desc.substring(paramsEnd + 1)) + " ";
    String params = desc.substring(1, paramsEnd);
    String methodName = name;

    if (isStaticConstructor()) {
      return classInfo.getName() + ".<static init>";
    } else if (isConstructor()) {
      returnValue = "";
      methodName = classInfo.getName();
    }
    return returnValue + methodName + "(" + deconstructParameters(params) + ")";
  }

  public String deconstructParameters(String params) {
    StringBuilder paramStr = new StringBuilder();
    int i = 0;
    String sep = "";
    String arrayRefs = "";
    while (i < params.length()) {
      switch (params.charAt(i)) {
      case 'B':
        paramStr.append(sep + "byte" + arrayRefs);
        break;
      case 'C':
        paramStr.append(sep + "char" + arrayRefs);
        break;
      case 'D':
        paramStr.append(sep + "double" + arrayRefs);
        break;
      case 'F':
        paramStr.append(sep + "float" + arrayRefs);
        break;
      case 'I':
        paramStr.append(sep + "int" + arrayRefs);
        break;
      case 'J':
        paramStr.append(sep + "long" + arrayRefs);
        break;
      case 'L':
        // Object becomes L/java/lang/Object; in internal nomenclature
        String internalClassName = params.substring(i + 1, params.indexOf(';',
            i));
        String className = internalClassName.replace('/', '.');
        paramStr.append(sep + className + arrayRefs);
        i = params.indexOf(';', i);
        break;
      case 'S':
        paramStr.append(sep + "short" + arrayRefs);
        break;
      case 'Z':
        paramStr.append(sep + "boolean" + arrayRefs);
        break;
      case 'V':
        paramStr.append("void");
        break;
      case '[':
        arrayRefs += "[]";
        break;
      default:
        throw new UnsupportedOperationException();
      }
      if (params.charAt(i) != '[') {
        arrayRefs = "";
        sep = ", ";
      }
      i++;
    }
    return paramStr.toString();
  }

  public List<ParameterInfo> getParameters() {
    return parameters;
  }

  public List<LocalVariableInfo> getLocalVariables() {
    return localVariables;
  }

  public boolean isConstructor() {
    return name.equals("<init>");
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
    return name.equals("<clinit>");
  }

  public boolean isSetter() {
    return visibility != Visibility.PRIVATE && getName().startsWith("set");
  }

  /**
   * @return Returns all methods in the same class which are setters, other than this setter
   */
  public Collection<MethodInfo> getSiblingSetters() {
    return newArrayList(filter(classInfo.getSetters(), notSelf));
  }

  public int compareTo(MethodInfo o) {
    if (o == null) {
      return -1;
    }
    return getFullName().compareTo(o.getFullName());
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
   * @return
   */
  public MethodInfo copyWithNoOperations(ClassInfo parent) {
    List<Operation> operations = Collections.emptyList();
    return new MethodInfo(parent, name, startingLineNumber, desc, methodThis,
        parameters, localVariables, visibility, linesOfComplexity, operations, isFinal);
  }
}
