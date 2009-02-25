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
package com.google.test.metric.javascript;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.Parser;

import java.io.Reader;
import java.io.StringReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

/**
 * Stores the set of javascript input files
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class FileRepository {
  private final Map<String, Reader> files = new HashMap<String, Reader>();

  public void addSourceFile(String filename, String contents) {
    files.put(filename, new StringReader(contents));
  }

  public List<AstRoot> getAsts() throws IOException {
    List<AstRoot> asts = new LinkedList<AstRoot>();
    for (Map.Entry<String, Reader> file : files.entrySet()) {
      Parser parser = new Parser();
      asts.add(parser.parse(file.getValue(), file.getKey(), 1));
    }
    return asts;
  }
}
