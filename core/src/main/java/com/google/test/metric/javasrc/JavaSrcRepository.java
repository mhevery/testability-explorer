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
package com.google.test.metric.javasrc;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import com.google.classpath.ClassPath;
import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;

public class JavaSrcRepository implements ClassRepository {

  private final ClassPath classPath;
  private Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();
  private final ClassRepository parentRepository;

  public JavaSrcRepository(ClassRepository parentRepository, ClassPath classPath) {
    this.parentRepository = parentRepository;
    this.classPath = classPath;
  }

  public ClassInfo getClass(String clazzName) {
    ClassInfo info = getCachedClass(clazzName);
    if (info != null) {
      return info;
    }
    parse(clazzName);
    info = classes.get(clazzName);
    if (info != null) {
      return info;
    }
    return parentRepository.getClass(clazzName);
  }

  private void parse(String clazzName) {
    String src = clazzName.replace('.', '/').replaceAll("\\$.*", "") + ".java";
    InputStream srcStream = classPath.getResourceAsStream(src);
    if (srcStream == null) {
      return;
    }
    JavaLexer lexer = new JavaLexer(srcStream);
    JavaRecognizer recognizer = new JavaRecognizer(lexer);
    recognizer.getASTFactory().setASTNodeClass(CommonASTWithLine.class);
    JavaTreeParser treeParser = new JavaTreeParser();
    Qualifier qualifier = new Qualifier();
    treeParser.builder = new CompilationUnitBuilder(this, qualifier, src);
    try {
      recognizer.compilationUnit();
      qualifier.compilationUnit(recognizer.getAST());
      treeParser.compilationUnit(recognizer.getAST());
    } catch (RecognitionException e) {
      throw new RuntimeException(e);
    } catch (TokenStreamException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String toString() {
    return classes.toString();
  }

  public void addClass(ClassInfo info) {
    classes.put(info.getName(), info);
  }

  public ClassInfo getCachedClass(String clazzName) {
    return classes.get(clazzName);
  }

}
