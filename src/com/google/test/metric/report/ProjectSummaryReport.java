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

public class ProjectSummaryReport {

  public ProjectReport getClassReport() {
    return classReport;
  }

  public ProjectReport getPackageReport() {
    return packageReport;
  }

  private final ProjectReport classReport;
  private final ProjectReport packageReport;

  public ProjectSummaryReport(ProjectReport classReport,
      ProjectReport packageReport) {
        this.classReport = classReport;
        this.packageReport = packageReport;
  }
}
