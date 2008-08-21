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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.test.metric.asm.Visibility;

public class MetricComputer {

  private final ClassRepository classRepository;
  private final PrintStream err;
  private final WhiteList whitelist;
  private final CostModel costModel;

  public MetricComputer(ClassRepository classRepository, PrintStream err,
      WhiteList whitelist, CostModel costModel) {
    this.classRepository = classRepository;
    this.err = err;
    this.whitelist = whitelist;
    this.costModel = costModel;
  }

  /* used for testing */
  public MethodCost compute(Class<?> clazz, String methodName) {
    ClassInfo classInfo = classRepository.getClass(clazz);
    MethodInfo method = classInfo.getMethod(methodName);
    return compute(method);
  }

  public MethodCost compute(MethodInfo method) {
    TestabilityContext context = new TestabilityContext(classRepository, err,
        whitelist, costModel);
    addStaticCost(method, context);
    addConstructorCost(method, context);
    addSetterInjection(method, context);
    addFieldCost(method, context);
    context.setInjectable(method);
    method.computeMetric(context);
    return context.getLinkedMethodCost(method);
  }

  private void addSetterInjection(MethodInfo baseMethod, TestabilityContext context) {
    for (MethodInfo method : baseMethod.getClassInfo().getMethods()) {
      if (method.getName().startsWith("set")) {
        context.implicitCost(baseMethod, method);
        context.setInjectable(method);
        method.computeMetric(context);
      }
    }
  }

  private void addConstructorCost(MethodInfo method, TestabilityContext context) {
    if (!method.isStatic() && !method.isConstructor()) {
      MethodInfo constructor = getPrefferedConstructor(method.getClassInfo());
      if (constructor != null) {
        context.implicitCost(method, constructor);
        context.setInjectable(constructor);
        constructor.computeMetric(context);
      }
    }
  }

  private void addFieldCost(MethodInfo method,
      TestabilityContext context) {
    for (FieldInfo field : method.getClassInfo().getFields()) {
      if (!field.isPrivate()) {
        context.setInjectable(field);
      }
    }
  }

  private void addStaticCost(MethodInfo baseMethod, TestabilityContext context) {
    if (baseMethod.isStaticConstructor()) {
      return;
    }
    for (MethodInfo method : baseMethod.getClassInfo().getMethods()) {
      if (method.getName().startsWith("<clinit>")) {
        context.implicitCost(baseMethod, method);
        method.computeMetric(context);
      }
    }
  }

  MethodInfo getPrefferedConstructor(ClassInfo classInfo) {
    Collection<MethodInfo> methods = classInfo.getMethods();
    MethodInfo constructor = null;
    int currentArgsCount = -1;
    for (MethodInfo methodInfo : methods) {
      if (methodInfo.getVisibility() != Visibility.PRIVATE
          && methodInfo.getName().startsWith("<init>")) {
        int count = countNonPrimitiveArgs(methodInfo.getParameters());
        if (currentArgsCount < count) {
          constructor = methodInfo;
          currentArgsCount = count;
        }
      }
    }
    return constructor;
  }

  private int countNonPrimitiveArgs(List<ParameterInfo> parameters) {
    int count = 0;
    for (ParameterInfo parameter : parameters) {
      if (parameter.getType().isObject()) {
        count++;
      }
    }
    return count;
  }

  /* used for testing   */
  public ClassCost compute(Class<?> clazz) {
    return compute(classRepository.getClass(clazz));
  }

  public ClassCost compute(ClassInfo clazz) {
    List<MethodCost> methods = new ArrayList<MethodCost>();
    for (MethodInfo method : clazz.getMethods()) {
      methods.add(compute(method));
    }
    ClassCost classCost = new ClassCost(clazz.getName(), methods);
    classCost.link(costModel);
    return classCost;
  }

}
