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


public class CyclomaticComplexityTest extends ClassRepositoryTestCase {

  private ClassInfo classInfo;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    classInfo = repo.getClass(CyclomaticMethods.class);
  }

  public static class CyclomaticMethods {
    public void emptyMethod_1() {
    }

    public void simpleMethod_1() {
      int i = 0;
      i += 1;
    }

    public void ifMethod_2() {
      int a = 0;
      if (a < 0) {
        a++;
      } else {
        a--;
      }
    }

    public void ifMethodNoElse_2() {
      int a = 0;
      if (a < 0) {
        a++;
      }
    }

    public void tryCatch_2() {
      int a = 0;
      try {
        a++;
      } catch (RuntimeException e) {
        a++;
      }
    }

    public void tryCatchFinally_2() {
      int a = 0;
      try {
        a++;
      } catch (RuntimeException e) {
        a++;
      } finally {
        a++;
      }
    }

    public void emptySwitch_2() {
      int a = 0;
      switch (a) {
        case 0:
          a = 0;
      }
    }

    public void emptySwitch_5() {
      int a = 0;
      switch (a) {
        case 0:
          a = 0;
          break;
        case 1:
          a = 1;
          break;
        case 2:
          a = 2;
          break;
        case 4:
          a = 4;
          break;
        default:
      }
    }

    public void switchImplementWithLookUp_3() {
      int a = 0;
      switch (a) {
        case 0:
          a = 0;
          break;
        case 9999:
          a = 9999;
          break;
        default:
          a = -1;
      }
    }

    public void switchWithDefault_2() {
      int a = 0;
      switch (a) {
        case 0:
          a = 0;
          break;
        default:
      }
    }
  }

  public void testVerifyAllMethodsCyclomaticComplexity() throws Exception {
    String errors = "";
    for (MethodInfo method : classInfo.getMethods()) {
      String name = method.getName();
      int _Index = name.lastIndexOf('_');
      if (_Index > 0) {
        long expectedCC = Long.parseLong(name.substring(_Index + 1));
        long actualCC = method.getNonRecursiveCyclomaticComplexity();
        if (expectedCC != actualCC) {
          errors += "\n" + method.getName()
              + " should have Cyclomatic Complexity of "
              + expectedCC + " but was " + actualCC;
        }
      }
    }
    assertTrue(errors, errors.length() == 0);
  }
}
