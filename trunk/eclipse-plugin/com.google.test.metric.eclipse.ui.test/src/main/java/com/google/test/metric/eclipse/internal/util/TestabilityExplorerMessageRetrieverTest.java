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
package com.google.test.metric.eclipse.internal.util;

import com.google.test.metric.report.issues.IssueSubType;
import com.google.test.metric.report.issues.IssueType;

import junit.framework.TestCase;

/**
 * Tests for {@link TestabilityExplorerMessageRetriever}
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityExplorerMessageRetrieverTest extends TestCase {
  TestabilityExplorerMessageRetriever retriever = new TestabilityExplorerMessageRetriever();

  public void testConvertTypeSubTypeToString() throws Exception {
    assertEquals("construction.static_init",
        retriever.convertTypeSubTypeToString(IssueType.CONSTRUCTION, IssueSubType.STATIC_INIT));
    assertEquals("construction.setter",
        retriever.convertTypeSubTypeToString(IssueType.CONSTRUCTION, IssueSubType.SETTER));
    assertEquals("collaborator.non_mockable",
        retriever.convertTypeSubTypeToString(IssueType.COLLABORATOR, IssueSubType.NON_MOCKABLE));
    assertEquals("directcost.complexity",
        retriever.convertTypeSubTypeToString(IssueType.DIRECT_COST, IssueSubType.COMPLEXITY));
  }
  
  public void testGetSuggestion() throws Exception {
    assertEquals("Suggestion: refactor the method by breaking the complex portions into several"
        + " smaller methods.",
        retriever.getSuggestion(IssueType.DIRECT_COST, IssueSubType.COMPLEXITY));
  }
}
