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
package com.google.test.metric.testing;

import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInfo;
import com.google.test.metric.MetricComputer;

/**
 * The {@code MetricComputer} needs to be language agnostic as it can work with
 * Java and C++ code. Yet, many tests need more convenient api's, which is what
 * this class provides.
 *
 * @author Jonathan Andrew Wolter
 */
public class MetricComputerJavaDecorator {

  private final MetricComputer metricComputer;
  private final ClassRepository classRepository;

  public MetricComputerJavaDecorator(MetricComputer metricComputer,
      ClassRepository classRepository) {
    this.metricComputer = metricComputer;
    this.classRepository = classRepository;
  }

  public ClassCost compute(ClassInfo clazz) {
    return metricComputer.compute(clazz);
  }

  public MethodCost compute(MethodInfo method) {
    return metricComputer.compute(method);
  }

  /** used for testing */
  public MethodCost compute(String clazz, String methodName) {
    ClassInfo classInfo = classRepository.getClass(clazz);
    MethodInfo method = classInfo.getMethod(methodName);
    return metricComputer.compute(method);
  }

  /** used for testing */
  public MethodCost compute(Class<?> clazz, String method) {
    return compute(clazz.getCanonicalName(), method);
  }

  /** used for testing   */
  public ClassCost compute(String clazz) {
    return metricComputer.compute(classRepository.getClass(clazz));
  }

  /** used for testing   */
  public ClassCost compute(Class<?> clazz) {
    return metricComputer.compute(classRepository.getClass(clazz.getCanonicalName()));
  }

  public MetricComputer getDecoratedComputer() {
    return metricComputer;
  }
}
