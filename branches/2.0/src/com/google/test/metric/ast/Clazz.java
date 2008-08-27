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

package com.google.test.metric.ast;

import com.google.test.metric.FieldNotFoundException;
import com.google.test.metric.MethodNotFoundException;
import com.google.test.metric.ast.AbstractSyntaxTree.Node;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class Clazz extends Node implements ClassHandle, ClassInfo {
  String name;
  Module module;
  Collection<ClassHandle> superClasses;
  Map<String, FieldInfo> fields = new HashMap<String, FieldInfo>();

  Set<Method> methods = new HashSet<Method>();

  Clazz(Module newModule, String newName,
      Collection<ClassHandle> theSuperClasses) {
    module = newModule;
    name = newName;
    superClasses = new HashSet<ClassHandle>(theSuperClasses);
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  public ClassHandle getHandle() {
    return this;
  }

  /**
   * {@inheritDoc}
   */
  public MethodInfo getMethod(String methodName)
      throws MethodNotFoundException {
    for (MethodInfo info : methods) {
      if (info.getName().equals(methodName)) {
        return info;
      }
    }
    throw new MethodNotFoundException(name, methodName);
  }

  /**
   * {@inheritDoc}
   */
  public FieldInfo getField(String fieldName) throws FieldNotFoundException {
    if (fields.containsKey(fieldName)) {
      return fields.get(fieldName);
    }
    throw new FieldNotFoundException(name, fieldName);
  }

  public void registerMethod(Method method) {
    methods.add(method);
  }

  public void registerField(Field field) {
    fields.put(field.getName(), field);
  }

  public Collection<FieldInfo> getFields() {
    return Collections.unmodifiableCollection(fields.values());
  }

  public Collection<MethodInfo> getMethods() {
    Collection<MethodInfo> result = new ArrayList<MethodInfo>(methods.size());
    for (Method m : methods) {
      result.add(m);
    }
    return result;
  }
}