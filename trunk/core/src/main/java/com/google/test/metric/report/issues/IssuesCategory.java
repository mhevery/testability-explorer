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

import java.util.List;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public interface IssuesCategory {

  /**
   * @return true iff there are no issue of any type
   */
  boolean isEmpty();

  /**
   * @return the types of issues reported by the issues container.
   */
  Enum[] getTypes();

  /**
   * @param type the string representation of an issue type
   * @return the list of issues of this type
   */
  List<Issue> getIssuesOfType(String type);

  /**
   * The name of this issues category.
   * @return
   */
  String getName();
}
