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
package com.google.test.metric.cpp.dom;

public class BaseClass {
  public enum AccessSpecifier {
    PRIVATE, PROTECTED, PUBLIC
  }

  private ClassDeclaration declaration;
  private final String access_specifier;

  BaseClass(String specifier) {
    this.access_specifier = specifier;
  }

  public AccessSpecifier getAccessSpecifier() {
    AccessSpecifier tmp = AccessSpecifier.PUBLIC;
    if (this.access_specifier.contentEquals("public")) {
      tmp = AccessSpecifier.PUBLIC;
    } else if (this.access_specifier.contentEquals("protected")) {
      tmp = AccessSpecifier.PROTECTED;
    } else if (this.access_specifier.contentEquals("private")) {
      tmp = AccessSpecifier.PRIVATE;
    }
    return tmp;
  }

  public void setDeclaration(ClassDeclaration base) {
    this.declaration = base;
  }

  public ClassDeclaration getDeclaration() {
    return declaration;
  }
}
