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

public class SourceLinker implements SourceLinkGenerator {

  private final String lineUrlTemplate;
  private final String classUrlTemplate;
  private final boolean noTemplate;

  /**
   *
   * @param lineUrlTemplate Template for generating Urls for divs with class "Line"
   * @param classUrlTemplate Template for generating Urls for divs with class "Class"
   */
  public SourceLinker(String lineUrlTemplate, String classUrlTemplate) {
    this.classUrlTemplate = classUrlTemplate;
    this.lineUrlTemplate = lineUrlTemplate;
    this.noTemplate = lineUrlTemplate.equals("") || classUrlTemplate.equals("");
  }

  public String buildClassLink(String filePath, String anchorText) {
    if (noTemplate) {
      return anchorText;
    }
    String url = String.format("<a href=\"%s\" target=\"source\">%s</a>", classUrlTemplate,
            anchorText);
    url = url.replace("{path}", filePath);
    return url;
  }


  public String buildLineLink(String filePath, int lineNumber, String anchorText) {
    if (noTemplate) {
      return anchorText;
    }
    String url = String.format("<a href=\"%s\" target=\"source\">%s</a>", lineUrlTemplate,
            anchorText);
    url = url.replace("{path}", filePath);
    url = url.replace("{line}", "" + lineNumber);
    return url;
  }

  public String getOriginalFilePath(String className) {
    if (noTemplate) {
      return className;
    }
    className = className.replace('.', '/');

    int internalClassDelim = className.indexOf('$');
    if (internalClassDelim > -1) {
      className = className.substring(0, internalClassDelim );
    }
    return className + ".java";
  }

}
