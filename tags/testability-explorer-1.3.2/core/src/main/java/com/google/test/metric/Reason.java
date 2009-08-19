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
package com.google.test.metric;

/** This attempts to answer "What is the source of each line's cost?" */
public enum Reason {
  IMPLICIT_CONSTRUCTOR("implicit cost from construction", true),
  //
  IMPLICIT_SETTER("implicit cost calling all setters", true),
  //
  IMPLICIT_STATIC_INIT("implicit cost from static initialization", true),
  //
  NON_OVERRIDABLE_METHOD_CALL("cost from calling non-overridable method", false);
  // TODO(jwolter): be able to tell people why this method could not be overridden:
  // whether it is static, private or final.
  // SOMEDAY(jwolter): it would be nice to make static methods worse than others. Because we don't
  // want to encourage people to subclass for tests.

  private final String description;
  private final boolean isImplicit;

  private Reason(String description, boolean implicit) {
    this.description = description;
    isImplicit = implicit;
  }

  @Override
  public String toString() {
    return description;
  }

  public boolean isImplicit() {
    return isImplicit;
  }
}
