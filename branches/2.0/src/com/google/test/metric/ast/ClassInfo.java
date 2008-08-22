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
package com.google.test.metric.ast;

//For now we use our own FieldInfo and MethodInfo interfaces. Once they are
//done and tested, we'll move them to test.metric
//import com.google.test.metric.FieldInfo;
//import com.google.test.metric.MethodInfo;

import com.google.test.metric.FieldNotFoundException;
import com.google.test.metric.MethodNotFoundException;

/**
 * Contains methods to retrieve all language independent information of a class
 * in the AST.
 */
public interface ClassInfo {

  /**
   * @return The name of the represented class.
   */
  String getName();

  /**
   * @return A string representation of the represented class.
   */
  String toString();

  /**
   * @param methodName The name of a method in this class.
   * @return a handle to ANY method of this class with this name.
   * @throws MethodNotFoundException if no such method exists.
   */
  MethodInfo getMethod(String methodName) throws MethodNotFoundException;

  /**
   * @param fieldName The name of a field in this class.
   * @return a handle to the field in this class with this name.
   * @throws FieldNotFoundException if no such field exists.
   */
  FieldInfo getField(String fieldName) throws FieldNotFoundException;

}
