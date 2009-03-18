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
package com.google.test.metric.example;

/**
 * An example of how static method calls to a complex method
 * count as cyclomatic complexity cost due to a non-mockable
 * dependency.
 */
public class StaticMethodCalled {

  /**
   * A static method is defined in this class, with a
   * cyclomatic complexity of 2.
   */
  public static class HasStaticMethod {
    public static boolean isGreat() {
      boolean x = true;
      boolean a = x ? true : false;
      return a && x ? false : true;
    }
  }

  /**
   * In this code, there is a call to the static method. This is equivalent
   * to calling new() to make an instance of HasStaticMethod - there is no
   * seam between us and that method.
   */
  public void callTheMethod() {
    /**
     * This will incur a cyclomatic complexity cost of 2.
     */
    HasStaticMethod.isGreat();
  }
}
