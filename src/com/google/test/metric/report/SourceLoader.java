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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.classpath.ClassPath;

public class SourceLoader {

  private final ClassPath classPath;

  public SourceLoader(ClassPath classPath) {
    this.classPath = classPath;
  }

  public Source load(String name) {
    name = name.replaceAll("\\$.*", "");
    String resource = name.replace(".", "/") + ".java";
    InputStream is = classPath.getResourceAsStream(resource);
    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
    ArrayList<Source.Line> lines = new ArrayList<Source.Line>();
    String line;
    int lineNumber = 1;
    try {
      while ((line = reader.readLine()) != null) {
        lines.add(new Source.Line(lineNumber, line));
        lineNumber ++;
      }
      reader.close();
      return new Source(lines);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

}
