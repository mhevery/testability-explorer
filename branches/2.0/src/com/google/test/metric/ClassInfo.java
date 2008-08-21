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
package com.google.test.metric;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassInfo {

  private final Map<String, MethodInfo> methods = new HashMap<String, MethodInfo>();
  private final Map<String, FieldInfo> fields = new HashMap<String, FieldInfo>();
  private final String name;
  private final boolean isInterface;
  private final ClassInfo superClass;
  private final List<ClassInfo> interfaces;

  public ClassInfo(String name, boolean isInterface, ClassInfo superClass,
      List<ClassInfo> interfaces) {
    this.isInterface = isInterface;
    this.superClass = superClass;
    this.interfaces = interfaces;
    this.name = name.replace("/", ".");
  }

  public String getName() {
    return name;
  }

  public ClassInfo getSuperClass() {
    return superClass;
  }

  public boolean isInterface() {
    return isInterface;
  }

  public MethodInfo getMethod(String methodName) {
    List<ClassInfo> superClasses = new ArrayList<ClassInfo>();
    superClasses.add(this);
    while (!superClasses.isEmpty()) {
      ClassInfo clazz = superClasses.remove(0);
      MethodInfo methodInfo = clazz.methods.get(methodName);
      if (methodInfo != null) {
        return methodInfo;
      }
      if (clazz.superClass != null) {
        superClasses.add(0, clazz.superClass);
      }
      superClasses.addAll(clazz.interfaces);
    }
    throw new UnsupportedOperationException();
    //throw new MethodNotFoundException(this, methodName);
  }

  public void addMethod(MethodInfo methodInfo) {
    methods.put(methodInfo.getNameDesc(), methodInfo);
  }

  @Override
  public String toString() {
    return name;
  }

  public FieldInfo getField(String fieldName) {
    ClassInfo clazz = this;
    while (clazz != null) {
      FieldInfo fieldInfo = clazz.fields.get(fieldName);
      if (fieldInfo != null) {
        return fieldInfo;
      }
      clazz = clazz.superClass;
    }
    throw new UnsupportedOperationException();
    //throw new FieldNotFoundException(this, fieldName);
  }

  public void addField(FieldInfo fieldInfo) {
    fields.put(fieldInfo.getName(), fieldInfo);
  }

  public Collection<MethodInfo> getMethods() {
    return methods.values();
  }

  public Collection<FieldInfo> getFields() {
    return fields.values();
  }

  public List<ClassInfo> getInterfaces() {
    return interfaces;
  }
}
