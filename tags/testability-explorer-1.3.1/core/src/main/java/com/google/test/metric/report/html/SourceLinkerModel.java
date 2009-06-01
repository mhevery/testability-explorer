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
package com.google.test.metric.report.html;

import com.google.test.metric.report.SourceLinker;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

import java.util.List;

/**
 * Wrap the {@link com.google.test.metric.report.SourceLinker#buildLineLink(String, int, String)}
 * method as a Freemarker method.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class SourceLinkerModel implements TemplateMethodModel {
  private final SourceLinker linker;

  public SourceLinkerModel(SourceLinker linker) {
    this.linker = linker;
  }

  public Object exec(List arguments) throws TemplateModelException {
    String file = (String) arguments.get(0);
    int lineNumber = Integer.parseInt((String) arguments.get(1));
    String anchorText = (String) arguments.get(2);
    return linker.buildLineLink(file, lineNumber, anchorText);
  }
}
