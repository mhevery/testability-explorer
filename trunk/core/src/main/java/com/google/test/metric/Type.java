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

public class Type {
  private final String name;
  private final String code;
  private final int array;

  protected Type() {
    this.name = null;
    this.code = null;
    this.array = 0;
  }

  protected Type(String name, String code) {
    this(0, name, code);
  }

  protected Type(int array, String name, String code) {
    this.array = array;
    this.name = name;
    if (name.contains(";")) {
      throw new IllegalArgumentException();
    }
    this.code = code;
  }

  @Override
  public String toString() {
    return name;
  }

  public String getCode() {
    return code;
  }

  public Type toArray() {
    return new Type(array + 1, name + "[]", "[" + code);
  }

  public boolean isPrimitive() {
    return code.length() == 1;
  }

  public boolean isObject() {
    return !isPrimitive();
  }

  public boolean isArray() {
    return array > 0;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + array;
    result = prime * result + ((code == null) ? 0 : code.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    Type other = (Type) obj;
    return code.equals(other.code);
  }

}
