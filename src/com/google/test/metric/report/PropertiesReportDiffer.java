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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class PropertiesReportDiffer {

  private final InputStream in1;
  private final InputStream in2;

  public PropertiesReportDiffer(InputStream in1, InputStream in2) {
    this.in1 = in1;
    this.in2 = in2;
  }

  public Diff diff() {
    Diff diff = new Diff();
    Properties oldMetrics = new Properties(),
      newMetrics = new Properties();
    try {
      oldMetrics.load(in1);
      newMetrics.load(in2);

      Set<String> oldClasses = getStringPropertyNames(oldMetrics);
      Set<String> newClasses = getStringPropertyNames(newMetrics);

      for (String className : oldClasses) {
        final int oldValue = Integer.parseInt(oldMetrics.getProperty(className));
        if (newClasses.contains(className)) {
          final int newValue = Integer.parseInt(newMetrics.getProperty(className));
          if (newValue != oldValue) {
            diff.addChangedClass(className, oldValue, newValue);
          }
          newClasses.remove(className);
        } else {
          diff.addRemovedClass(className, oldValue);
        }

      }
      // anything left in newClasses was not in oldClasses
      for (String className : newClasses) {
        diff.addAddedClass(className, Integer.parseInt(newMetrics.getProperty(className)));
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return diff;

  }

  /**
   * Get the keys from the properties, as a Set of Strings.
   * TODO: replace with {@link Properties#stringPropertyNames()} when 1.6 is available
   * @param metrics
   * @return unique property names
   */
  private Set<String> getStringPropertyNames(Properties metrics) {
    @SuppressWarnings("unchecked")
    List<String> propNames = (List<String>) Collections.list(metrics.propertyNames());
    Set<String> names = new TreeSet<String>();
    for (String name : propNames) {
      names.add(name);
    }
    return names;
  }

}
