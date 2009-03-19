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
package com.google.test.metric.example.ExpensiveConstructor;

public class StaticWorkInTheConstructor {
  public static class StaticHolder {
    static boolean x = false;
    // A static method with a cyclomatic complexity of 2.
    public static boolean staticCost2() {
      boolean a = x ? false : true;
      boolean b = a ? false : true;
      return b;
    }
  }

  // Should this have a global state cost? It does not now, yet it abuses global state.
  public StaticWorkInTheConstructor() {
    StaticHolder.staticCost2();
  }
}
