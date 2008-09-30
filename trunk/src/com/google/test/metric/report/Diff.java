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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple bean to store a difference between two testability reports.
 * @author alexeagle@google.com (Alex Eagle)
 *
 */
public class Diff {

  public class Change {

    private final int oldMetric;
    private final int newMetric;
    private final String className;

    public Change(String className, int oldMetric, int newMetric) {
      this.className = className;
      this.oldMetric = oldMetric;
      this.newMetric = newMetric;
    }

    public String getClassName() {
      return className;
    }
    public int getNewMetric() {
      return newMetric;
    }
    public int getOldMetric() {
      return oldMetric;
    }
  }

  private final List<Change> addedClasses = new ArrayList<Change>();
  private final List<Change> removedClasses = new ArrayList<Change>();
  private final List<Change> changedClasses = new ArrayList<Change>();

  public void addAddedClass(Change className) {
    addedClasses.add(className);
  }

  public void addRemovedClass(Change className) {
    removedClasses.add(className);
  }

  public List<Change> getAddedClasses() {
    return addedClasses;
  }

  public List<Change> getRemovedClasses() {
    return removedClasses;
  }

  public void addChangedClass(Change className) {
    changedClasses.add(className);
  }

  public List<Change> getChangedClasses() {
    return changedClasses;
  }

  public void addAddedClass(String className, int metric) {
    addAddedClass(new Change(className, -1, metric));
  }

  public void addRemovedClass(String className, int metric) {
    addRemovedClass(new Change(className, metric, -1));
  }

  public void addChangedClass(String className, int oldMetric, int newMetric) {
    addChangedClass(new Change(className, oldMetric, newMetric));
  }

  public void print(PrintStream out) {
    out.println("Added:");
    for (Change change : addedClasses) {
      out.println("  " + change.getClassName());
    }
    out.println("Removed:");
    for (Change change : removedClasses) {
      out.println("  " + change.getClassName());
    }
    out.println("Changed:");
    for (Change change : changedClasses) {
      out.printf("  %s (%+d)\n", change.getClassName(),
          change.getNewMetric() - change.getOldMetric());
    }
  }
}
