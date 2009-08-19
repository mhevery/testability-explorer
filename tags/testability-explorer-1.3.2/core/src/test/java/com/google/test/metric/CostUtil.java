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

public class CostUtil {
  // This adds a global cost of 1 to everything else in the class. No matter
  // if it is a primitive or Object -- any time you have globaly mutable state
  // (non-final static state), you will have hard to test code.  (And hard to
  // parallelize code).
  private static boolean x = false;

  public static boolean staticCost0() {
    return x;
  }

  public boolean instanceCost0() {
    return x;
  }

  public static boolean staticCost1() {
    boolean a = x ? false : true;
    return a;
  }

  public boolean instanceCost1() {
    boolean a = x ? false : true;
    return a;
  }

  public static boolean staticCost2() {
    boolean a = x ? false : true;
    boolean b = a ? false : true;
    return b;
  }

  public boolean instanceCost2() {
    boolean a = x ? false : true;
    boolean b = a ? false : true;
    return b;
  }

  public static boolean staticCost3() {
    boolean a = x ? false : true;
    boolean b = a ? false : true;
    boolean c = b ? false : true;
    return c;
  }

  public boolean instanceCost3() {
    boolean a = x ? false : true;
    boolean b = a ? false : true;
    boolean c = b ? false : true;
    return c;
  }

  public static boolean staticCost4() {
    boolean a = x ? false : true;
    boolean b = a ? false : true;
    boolean c = b ? false : true;
    boolean d = c ? false : true;
    return d;
  }

  public boolean instanceCost4() {
    boolean a = x ? false : true;
    boolean b = a ? false : true;
    boolean c = b ? false : true;
    boolean d = c ? false : true;
    return d;
  }
}