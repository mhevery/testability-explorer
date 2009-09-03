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
package com.google.test.metric.asm;

import static com.google.test.metric.JavaType.fromDescParameters;
import static com.google.test.metric.JavaType.fromDescReturn;

import com.google.test.metric.Type;

public class JavaNamer {

  public String nameMethod(String className, String methodName,
      String methodDesc) {
    StringBuilder fullName = new StringBuilder();
    if (methodName.equals("<init>")) {
      int index = Math.max(className.lastIndexOf("."), className
          .lastIndexOf("$")) + 1;
      fullName.append(className.substring(index));
    } else if (methodName.equals("<clinit>")) {
      fullName.append("<static init>");
    } else {
      fullName.append(toClass(fromDescReturn(methodDesc)));
      fullName.append(" ");
      fullName.append(methodName);
    }
    fullName.append("(");
    String sep = "";
    for (Type type : fromDescParameters(methodDesc)) {
      fullName.append(sep);
      fullName.append(toClass(type));
      sep = ", ";
    }
    fullName.append(")");
    String javaName = fullName.toString();
    return javaName;
  }

  private String toClass(Type type) {
    return type.toString().replace('$', '.');
  }

  public String nameClass(String name) {
    return name.replace('/', '.').replace('$', '.');
  }

}
