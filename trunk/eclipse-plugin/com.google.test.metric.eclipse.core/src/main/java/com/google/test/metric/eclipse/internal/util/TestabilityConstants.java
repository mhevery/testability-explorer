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

import java.util.ArrayList;
import java.util.List;

public class TestabilityConstants {
  public static final String CONFIGURATION_ATTR_PROJECT_NAME =
      "testability.launch.configuration.projectname";
  public static final String CONFIGURATION_ATTR_REPORT_FOLDER_NAME =
      "testability.launch.configuration.folder.name";
  
  public static final String CONFIGURATION_ATTR_RECORDING_DEPTH =
      "testability.launch.configuration.recording.depth";
  public static final String CONFIGURATION_ATTR_CYCLOMATIC_COST =
      "testability.launch.configuration.cyclomatic.cost";
  public static final String CONFIGURATION_ATTR_GLOBAL_STATE_COST =
      "testability.launch.configuration.global.state.cost";
  
  public static final String CONFIGURATION_ATTR_MAX_EXCELLENT_COST =
      "testability.launch.configuration.max.excellent.cost";
  public static final String CONFIGURATION_ATTR_MAX_ACCEPTABLE_COST =
      "testability.launch.configuration.max.acceptable.cost";
  
  public static final String CONFIGURATION_ATTR_MAX_CLASSES_IN_REPORT =
      "testability.launch.configuration.max.classes.in.report";
  
  public static final String CONFIGURATION_ATTR_WHITELIST =
      "testability.launch.configuration.whitelist";

  public static final String CONFIGURATION_ATTR_RUN_ON_BUILD =
      "testability.launch.configuration.run.on.build";

  public static final String CONFIGURATION_ATTR_RUNNING_IN_COMPILATION_MODE =
      "testability.launch.configuration.running.in.compilation.mode";
  
  public static final String TESTABILITY_DIRECT_COST_MARKER_TYPE =
      "com.google.test.metric.eclipse.ui.testabilityMarkerDirectCost";
  public static final String TESTABILITY_CONSTRUCTOR_MARKER_TYPE =
      "com.google.test.metric.eclipse.ui.testabilityMarkerConstructor";
  public static final String TESTABILITY_COLLABORATOR_MARKER_TYPE =
      "com.google.test.metric.eclipse.ui.testabilityMarkerCollaborator";  
  
  public static final int MAX_ACCEPTABLE_COST = 100;
  public static final int MAX_EXCELLENT_COST = 20;
  public static final int CYCLOMATIC_COST = 1;
  public static final int GLOBAL_STATE_COST = 10;
  public static final int RECORDING_DEPTH = 10;
  public static final List<String> WHITELIST = new ArrayList<String>();
  public static final String ERROR_LOG_FILENAME = "error-log";
  public static final String HTML_REPORT_FILENAME = "report.html";
  public static final String TESTABILITY = "testability";
  public static final int MAX_CLASSES_TO_SHOW_IN_ISSUES_REPORTER = 25;
  
  public static final String ISSUE_TYPE = "issueType";
  public static final String TESTABILITY_LAUNCH_GROUP =
      "com.google.test.metric.eclipse.ui.launchGroup.testability";
  public static final String TESTABILITY_LAUNCH_CONFIGURATION_TYPE =
      "com.google.test.metric.eclipse.core.launchTestabilityConfigurationType";
  public static final String TESTABILITY_MODE = "testability";
  
  private TestabilityConstants() {
  }
}
