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
package com.google.test.metric.report;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/**
 * Simple bean to store a difference between two testability reports.
 * @author alexeagle@google.com (Alex Eagle)
 *
 */
public class Diff {

  private final List<ClassDiff> classDiffs;

  public Diff(List<ClassDiff> classDiffs) {
    this.classDiffs = classDiffs;
  }

  public List<ClassDiff> getClassDiffs() {
    return classDiffs;
  }

  public void sort() {
    Collections.sort(classDiffs, new ClassDeltaComparator());
    for (ClassDiff classDiff : classDiffs) {
      Collections.sort(classDiff.methodDiffs, new MethodDeltaComparator());
    }
  }

  public static class ClassDiff {
    private final Integer oldMetric;
    private final Integer newMetric;
    private final List<MethodDiff> methodDiffs;
    private final String className;

    public ClassDiff(String className, Integer oldMetric, Integer newMetric) {
      this.className = className;
      this.oldMetric = oldMetric;
      this.newMetric = newMetric;
      this.methodDiffs = Collections.EMPTY_LIST;
    }

    public ClassDiff(String className, Integer oldMetric, Integer newMetric, List<MethodDiff> methodDiffs) {
      this.className = className;
      this.oldMetric = oldMetric;
      this.newMetric = newMetric;
      this.methodDiffs = methodDiffs;
    }

    public String getClassName() {
      return className;
    }
    public Integer getNewMetric() {
      return newMetric;
    }
    public Integer getOldMetric() {
      return oldMetric;
    }

    public List<MethodDiff> getMethodDiffs() {
      return methodDiffs;
    }

    public Integer getDelta() {
      if (newMetric == null) {
        return -oldMetric;
      }
      if (oldMetric == null) {
        return newMetric;
      }
      return (newMetric - oldMetric);
    }
  }

  public static class MethodDiff {
    private final Integer oldMetric;
    private final Integer newMetric;
    private final String methodName;

    public MethodDiff(String methodName, Integer oldMetric, Integer newMetric) {
      this.oldMetric = oldMetric;
      this.newMetric = newMetric;
      this.methodName = methodName;
    }

    public Integer getOldMetric() {
      return oldMetric;
    }

    public Integer getNewMetric() {
      return newMetric;
    }

    public String getMethodName() {
      return methodName;
    }

    public Integer getDelta() {
      if (newMetric == null) {
        return -oldMetric;
      }
      if (oldMetric == null) {
        return newMetric;
      }
      return (newMetric - oldMetric);
    }
  }

  private static class ClassDeltaComparator implements Comparator<ClassDiff> {
    public int compare(ClassDiff classDiff, ClassDiff classDiff1) {
      int result = classDiff.getDelta().compareTo(classDiff1.getDelta());
      if (result == 0) {
        result = classDiff.getClassName().compareTo(classDiff1.getClassName());
      }
      return result;
    }
  }

  private static class MethodDeltaComparator implements Comparator<MethodDiff> {
    public int compare(MethodDiff methodDiff, MethodDiff methodDiff1) {
      int result = methodDiff.getDelta().compareTo(methodDiff1.getDelta());
      if (result == 0) {
        result = methodDiff.getMethodName().compareTo(methodDiff.getMethodName());
      }
      return result;
    }
  }
}
