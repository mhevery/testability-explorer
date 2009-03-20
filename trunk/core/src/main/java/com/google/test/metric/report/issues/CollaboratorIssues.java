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

import com.google.test.metric.MethodCost;
import static com.google.test.metric.collection.LazyHashMap.newLazyHashMap;
import static com.google.test.metric.report.issues.CollaboratorIssues.CollaboratorType.*;
import com.google.test.metric.report.issues.CollaboratorIssues.CollaboratorType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Issues that arise from non-mockable use of collaborators.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class CollaboratorIssues extends IssuesCategory<CollaboratorType> {
  public CollaboratorIssues(Map<CollaboratorType, List<Issue>> issues) {
    super(issues);
  }

  public CollaboratorIssues() {
    super();
  }

  @Override
  Class<CollaboratorType> getTypeLiteral() {
    return CollaboratorType.class;
  }

  @Override
  public String getName() {
    return "Collaborators";
  }

  public List<Issue> getNewOperatorIssues() {
    return issues.get(NEW_OPERATOR);
  }

  public List<Issue> getStaticMethodIssues() {
    return issues.get(STATIC_METHOD);
  }

  public List<Issue> getFinalMethodIssues() {
    return issues.get(FINAL_METHOD);
  }

  public void add(Issue issue, boolean isStatic) {
    if (isStatic) {
      issues.get(STATIC_METHOD).add(issue);
    } else {
      issues.get(NEW_OPERATOR).add(issue);
    }
  }

  public enum CollaboratorType {
    STATIC_METHOD,
    NEW_OPERATOR,
    FINAL_METHOD,
    PRIVATE_METHOD
  }
}
