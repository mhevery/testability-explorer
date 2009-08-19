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

import com.google.test.metric.Type;

public class CppType extends Type {

  public static final Type VOID = new CppType("void");
  public static final Type BYTE = new CppType("byte");
  public static final Type SHORT = new CppType("short");
  public static final Type INT = new CppType("int");
  public static final Type BOOL = new CppType("bool");
  public static final Type CHAR = new CppType("char");
  public static final Type LONG = new CppType("long");
  public static final Type DOUBLE = new CppType("double");
  public static final Type FLOAT = new CppType("float");

  private boolean pointer = false;

  private CppType(String name) {
    super(name, "");
  }

  private CppType(String name, boolean pointer) {
    super(name, "");
    this.pointer = pointer;
  }

  public boolean isPointer() {
    return pointer;
  }

  public static Type fromName(String name) {
    if (name == VOID.toString()) {
      return VOID;
    } else if (name == BYTE.toString()) {
      return BYTE;
    } else if (name == SHORT.toString()) {
      return SHORT;
    } else if (name == INT.toString()) {
      return INT;
    } else if (name == BOOL.toString()) {
      return BOOL;
    } else if (name == CHAR.toString()) {
      return CHAR;
    } else if (name == LONG.toString()) {
      return LONG;
    } else if (name == DOUBLE.toString()) {
      return DOUBLE;
    } else if (name == FLOAT.toString()) {
      return FLOAT;
    }
    return new CppType(name);
  }

  public static Type fromName(String name, boolean pointer) {
    if (!pointer) {
      return fromName(name);
    }
    return new CppType(name, true);
  }
}
