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

import junit.framework.Assert;

public class InjectabilityVerifier {

  private String errors = "";

  public void assertInjectable(ClassInfo classInfo, VariableState globalVariables) {
    for (Variable field : classInfo.getFields()) {
      verify(field, globalVariables);
    }
    for (MethodInfo method : classInfo.getMethods()) {
      for (Variable param : method.getParameters()) {
        verify(param, globalVariables);
      }
      for (Variable param : method.getLocalVariables()) {
        verify(param, globalVariables);
      }
    }
    Assert.assertTrue(errors, errors.length() == 0);
  }

  public void verify(Variable variable, VariableState globalVariables) {
    if (variable.getName().equals("this")) {
    } else if (variable.getName().endsWith("_I")) {
      if (!globalVariables.isInjectable(variable)) {
        errors += "\n" + variable + " should be injectable";
      }
    } else if (variable.getName().endsWith("_NI")) {
      if (globalVariables.isInjectable(variable)) {
        errors += "\n" + variable + " should be non injectable";
      }
    } else {
      errors += "\n" + variable + " should end with '_I' or '_NI'.";
    }
  }


}
