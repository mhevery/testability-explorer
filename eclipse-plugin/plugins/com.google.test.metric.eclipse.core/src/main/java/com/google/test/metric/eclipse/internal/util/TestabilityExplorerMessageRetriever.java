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

import java.io.IOException;
import java.util.Properties;

/**
 * Helper class which retrieves the suggestions for various issues and their subtypes
 * from the Testability Explorer messages bundle.
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityExplorerMessageRetriever {

  private final Logger logger = new Logger();
  private final Properties properties = new Properties();
  public static final String PROPERTY_PREFIX = "report.explain.class.";
  public static final String PROPERTY_SUFFIX = ".suggest";
  
  public TestabilityExplorerMessageRetriever() {
    try {
      properties.load(getClass().getResourceAsStream("/messages.properties"));
    } catch (IOException e) {
      logger.logException(e);
    }
  }
  
  public String convertTypeSubTypeToString(IssueType type, IssueSubType subType) {
    return type.toString().replace("_", "").toLowerCase() + "." + subType.toString().toLowerCase();
  }

  public String getSuggestion(IssueType type, IssueSubType subType) {
    return properties.getProperty(PROPERTY_PREFIX + convertTypeSubTypeToString(type, subType)
        + PROPERTY_SUFFIX);
  }
}
