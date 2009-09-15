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

import com.google.test.metric.ClassInfo;
import com.google.test.metric.FieldInfo;
import com.google.test.metric.Type;
import com.google.test.metric.Visibility;

public class TypeBuilder {

  private final ClassInfo info;

  public TypeBuilder(ClassInfo info) {
    this.info = info;
  }

  public void addField(String name, Type type, Visibility visibility,
      boolean isGlobal, boolean isFinal) {
    boolean isPrivate = Visibility.PRIVATE == visibility;
    FieldInfo fieldInfo = new FieldInfo(info, name, type, isFinal, isGlobal,
        isPrivate);
    info.addField(fieldInfo);
  }

  public String getName() {
    return info.getName();
  }

}
