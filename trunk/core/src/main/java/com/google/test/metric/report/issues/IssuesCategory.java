/*
 * Copyright 2009 Google Inc.
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
package com.google.test.metric.report.issues;

import static com.google.test.metric.collection.LazyHashMap.newLazyHashMap;

import java.util.List;
import java.util.Map;
import java.util.Collections;

/**
 * A decorator around a map from issue types to a list of issues of that type.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public abstract class IssuesCategory<E> {

  protected final Map<E, List<Issue>> issues;

  public IssuesCategory() {
    this.issues = newLazyHashMap(new IssuesListFactory());
  }

  public IssuesCategory(Map<E, List<Issue>> issues) {
    this.issues = issues;
  }

  /**
   * @return true iff there are no issue of any type
   */
  public boolean isEmpty() {
    boolean empty = true;
    for (E type : getTypes()) {
      if (issues.containsKey(type) && !issues.get(type).isEmpty()) {
        empty = false;
      }
    }
    return empty;
  }

  public int getSize() {
    int size = 0;
    for (E type : getTypes()) {
      if (issues.containsKey(type)) {
        size += issues.get(type).size();
      }
    }
    return size;
  }

  /**
   * @return the types of issues reported by the issues container.
   */
  public E[] getTypes() {
    return getTypeLiteral().getEnumConstants();
  }

  abstract Class<E> getTypeLiteral();

  /**
   * @param type the string representation of an issue type
   * @return the list of issues of this type
   */
  public List<Issue> getIssuesOfType(String type) {
    E typeEnum = null;
    for (E anEnum : getTypes()) {
      if (anEnum.toString().equals(type)) {
        typeEnum = anEnum;
      }
    }
    if (typeEnum == null) {
      throw new IllegalArgumentException("Unknown issue type " + type);
    }
    if (issues.containsKey(typeEnum)) {
      return issues.get(typeEnum);
    }
    return Collections.emptyList();
  }

  /**
   * The name of this issues category.
   * @return
   */
  abstract String getName();

  @Override
  public String toString() {
    return issues.toString();
  }
}
