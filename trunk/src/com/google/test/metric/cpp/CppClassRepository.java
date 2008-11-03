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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.cpp.dom.ClassDeclaration;
import com.google.test.metric.cpp.dom.TranslationUnit;
import com.google.test.metric.cpp.dom.Visitor;

public class CppClassRepository implements ClassRepository {

  private final Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();

  private class ClassInfoBuilder extends Visitor {
    @Override
    public void beginVisit(ClassDeclaration classDeclaration) {
      ClassInfo classInfo = new ClassInfo(classDeclaration.getName(), false,
          null, new ArrayList<ClassInfo>());
      classes.put(classDeclaration.getName(), classInfo);
    }
  }

  public ClassInfo getClass(String clazzName) {
    return classes.get(clazzName);
  }

  public void addClass(ClassInfo classInfo) {
    classes.put(classInfo.getName(), classInfo);
  }

  void parse(InputStream in) throws Exception {
    TranslationUnit unit = new Parser().parse(in);
    ClassInfoBuilder builder = new ClassInfoBuilder();
    unit.accept(builder);
  }
}
