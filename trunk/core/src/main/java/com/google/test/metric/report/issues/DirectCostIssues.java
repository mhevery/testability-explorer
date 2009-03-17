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

import com.google.test.metric.report.issues.Issue.DirectCostType;
import static com.google.test.metric.collection.LazyHashMap.newLazyHashMap;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Issues that arise from high complexity in the class under analysis.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class DirectCostIssues implements IssuesCategory {

  private Map<DirectCostType, List<Issue>> issues =
      newLazyHashMap(new HashMap<DirectCostType, List<Issue>>(), new IssuesListFactory());

  public boolean isEmpty() {
    return true;
  }

  public Enum[] getTypes() {
    return DirectCostType.values();
  }

  public List<Issue> getIssuesOfType(String type) {
    return issues.get(DirectCostType.valueOf(type));
  }

  public String getName() {
    return "DirectCost";
  }
}
