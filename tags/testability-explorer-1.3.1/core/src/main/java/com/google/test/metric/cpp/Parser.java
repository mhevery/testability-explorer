/*
 * Copyright 2008 Google Inc.
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
package com.google.test.metric.cpp;

import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.test.metric.cpp.dom.TranslationUnit;

public class Parser {

  public TranslationUnit parse(InputStream in) throws Exception {
    RootBuilder builder = new RootBuilder();
    Reader reader = new InputStreamReader(in);
    InternalLexer lexer = new InternalLexer(reader);
    InternalParser parser = new InternalParser(lexer);
    parser.translation_unit(builder);
    return builder.getNode();
  }

  public TranslationUnit parse(String source) throws Exception {
    return this.parse(source, new NodeDictionary());
  }

  public TranslationUnit parse(String source, NodeDictionary dict)
      throws Exception {
    RootBuilder builder = new RootBuilder(dict);
    Reader reader = new CharArrayReader(source.toCharArray());
    InternalLexer lexer = new InternalLexer(reader);
    InternalParser parser = new InternalParser(lexer);
    parser.translation_unit(builder);
    return builder.getNode();
  }
}
