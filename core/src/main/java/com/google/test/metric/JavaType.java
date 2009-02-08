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
package com.google.test.metric;

public class JavaType extends Type {

  public static final Type VOID = new Type("void", "V");
  public static final Type BYTE = new Type("byte", "B");
  public static final Type SHORT = new Type("short", "S");
  public static final Type INT = new Type("int", "I");
  public static final Type BOOLEAN = new Type("boolean", "Z");
  public static final Type CHAR = new Type("char", "C");
  public static final Type LONG = new Type("long", "J");
  public static final Type DOUBLE = new Type("double", "D");
  public static final Type FLOAT = new Type("float", "F");
  public static final Type OBJECT = fromClass(Object.class);

  private JavaType(String name, String code) {
    super(name, code);
  }

  public static boolean isDoubleSlot(Type type) {
    return type == DOUBLE || type == LONG;
  }

  public static Type fromClass(Class<?> clazz) {
    if (clazz == Byte.class) {
      return BYTE;
    } else if (clazz == Short.class) {
      return SHORT;
    } else if (clazz == Integer.class) {
      return INT;
    } else if (clazz == Boolean.class) {
      return BOOLEAN;
    } else if (clazz == Character.class) {
      return CHAR;
    } else if (clazz == Long.class) {
      return LONG;
    } else if (clazz == Double.class) {
      return DOUBLE;
    } else if (clazz == Float.class) {
      return FLOAT;
    } else if (clazz.isArray()) {
      return fromClass(clazz.getComponentType()).toArray();
    } else {
      return fromJava(clazz.getName());
    }
  }

  public static Type fromJava(String clazz) {
    if (clazz.contains(";")) {
      throw new IllegalArgumentException();
    }
    clazz = clazz.replace('/', '.');
    String jvm = "L" + clazz.replace('.', '/') + ";";
    return new Type(0, clazz, jvm);
  }

  public static Type fromDesc(String desc) {
    char ch = desc.charAt(0);
    switch (ch) {
      case 'V':
        return VOID;
      case 'B':
        return BYTE;
      case 'S':
        return SHORT;
      case 'Z':
        return BOOLEAN;
      case 'C':
        return CHAR;
      case 'I':
        return INT;
      case 'J':
        return LONG;
      case 'F':
        return FLOAT;
      case 'D':
        return DOUBLE;
      case '[':
        return fromDesc(desc.substring(1)).toArray();
      case 'L':
        String javaClass = desc.substring(1, desc.length() - 1);
        javaClass = javaClass.replace('/', '.');
        return new Type(0, javaClass, desc);
      default:
        throw new IllegalArgumentException(desc);
    }
  }
}
