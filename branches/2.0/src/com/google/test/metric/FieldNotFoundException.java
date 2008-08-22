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


public class FieldNotFoundException extends RuntimeException {
  private static final long serialVersionUID = -5544082288915184750L;

  private final ClassInfo classInfo;
  private final String fieldName;

  public FieldNotFoundException(ClassInfo classInfo, String fieldName) {
    super("Field '" + fieldName + "' not found in '" + classInfo.getName() + "'.");
    this.classInfo = classInfo;
    this.fieldName = fieldName;
  }

  public ClassInfo getClassInfo() {
    return classInfo;
  }

  public String getFieldName() {
    return fieldName;
  }

}
