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
// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric.report;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.classpath.ClassPath;

import freemarker.cache.TemplateLoader;

public class ClassPathTemplateLoader implements TemplateLoader {
  private final ClassPath classPath;
  private final String prefix;

  public ClassPathTemplateLoader(ClassPath classPath, String prefix) {
    this.classPath = classPath;
    this.prefix = prefix;
  }

  public void closeTemplateSource(Object source) throws IOException {
  }

  public Object findTemplateSource(String name) throws IOException {
    return classPath.isResource(prefix + name) ? name : null;
  }

  public long getLastModified(Object name) {
   return 0;
  }

  public Reader getReader(Object source, String encoding) throws IOException {
    InputStream is = classPath.getResourceAsStream(prefix + source);
    InputStreamReader reader = new InputStreamReader(is, encoding);
    return reader;
  }
}