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

import static java.util.Collections.unmodifiableList;

import com.google.test.metric.asm.Visibility;
import com.google.test.metric.method.op.turing.Operation;

import java.util.List;

public class MethodInfo {

  private final ClassInfo classInfo;
  private final String name;
  private final Variable methodThis;
  private final List<ParameterInfo> parameters;
  private final List<LocalVariableInfo> localVariables;
  private final String desc;
  private final long cyclomaticComplexity;
  private final Visibility visibility;
  private final List<Operation> operations;
  private final int startingLineNumber;

  public MethodInfo(ClassInfo classInfo, String methodName,
      int startingLineNumber, String desc, Variable methodThis,
      List<ParameterInfo> parameters, List<LocalVariableInfo> localVariables,
      Visibility visibility, long cylomaticComplexity,
      List<Operation> operations) {
    this.classInfo = classInfo;
    this.name = methodName;
    this.startingLineNumber = startingLineNumber;
    this.desc = desc;
    this.methodThis = methodThis;
    this.parameters = parameters;
    this.localVariables = localVariables;
    this.cyclomaticComplexity = cylomaticComplexity;
    this.visibility = visibility;
    this.operations = operations;
  }

  public String getNameDesc() {
    return name + desc;
  }

  @Override
  public String toString() {
    return getFullName();
  }

//  public String getFullName() {
//    return classInfo.getName() + "." + getNameDesc();
//  }

  public long getNonRecursiveCyclomaticComplexity() {
    return cyclomaticComplexity;
  }

  public String getName() {
    return name;
  }

  public String getFullName() {
//    int paramsBegin = methodName.indexOf('(');
//    int paramsEnd = methodName.indexOf(')');
//    int lastDot = methodName.lastIndexOf('.');
//
//    String method = methodName.substring(lastDot + 1,
//        paramsBegin);
//    String returnValue = deconstructParameters(
//        methodName.substring(paramsEnd + 1));
//
//    if (isStaticConstructor() || isConstructor()) {
////      method = " Constructor";
//      method = " " + methodName.substring(
//          methodName.lastIndexOf('.', lastDot-1) + 1, lastDot);
//      returnValue = "";
//    }
//    String params = methodName.substring(paramsBegin + 1, paramsEnd);
    int paramsEnd = desc.indexOf(')');
    String returnValue = deconstructParameters(desc.substring(paramsEnd + 1));
    String params = desc.substring(1, paramsEnd);
    String methodName = name;

    if (isStaticConstructor() || isConstructor()) {
      returnValue = " ";
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
        paramStr.append(sep + " byte " + arrayRefs);
        break;
      case 'C':
        paramStr.append(sep + " char " + arrayRefs);
        break;
      case 'D':
        paramStr.append(sep + " double " + arrayRefs);
        break;
      case 'F':
        paramStr.append(sep + " float " + arrayRefs);
        break;
      case 'I':
        paramStr.append(sep + " int " + arrayRefs);
        break;
      case 'J':
        paramStr.append(sep + " long " + arrayRefs);
        break;
      case 'L':
        // Object becomes L/java/lang/Object; in internal nomenclature
        String internalClassName = params.substring(i + 1, params.indexOf(';',
            i));
//        String className = internalClassName.substring(
//            internalClassName.lastIndexOf('/') + 1);
        String className = internalClassName.replace('/', '.');
        paramStr.append(sep + " " + className + " " + arrayRefs);
        i = params.indexOf(';', i);
        break;
      case 'S':
        paramStr.append(sep + " short " + arrayRefs);
        break;
      case 'Z':
        paramStr.append(sep + " boolean " + arrayRefs);
        break;
      case 'V':
        paramStr.append(" void ");
        break;
      case '[':
        arrayRefs += "[]";
        break;
      default:
        throw new UnsupportedOperationException();
      }
      if (params.charAt(i) != '[') {
        arrayRefs = "";
        sep = ",";
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
    return !isConstructor() && (!isStatic())
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

  public long getTestCost() {
    // Why -1? a method with a cyclomatic complexity of 1 can
    // be split to n smaller methods. The one single method and
    // N small ones are same cost, but the sum of cyclomatic is not
    // the same unless we change the offset of the method and say that
    // a simple method is 0 and hence splitting 0 to N 0 is still zero
    // and we gain the equivalence.
    return getNonRecursiveCyclomaticComplexity() - 1;
  }

  public void computeMetric(TestabilityContext context) {
    for (Operation operation : getOperations()) {
      operation.computeMetric(context, this);
    }
  }

  public boolean isStaticConstructor() {
    return name.equals("<clinit>");
  }

}
