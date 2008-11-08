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

  public void assertInjectable(ClassInfo classInfo, TestabilityVisitor context) {
    for (Variable field : classInfo.getFields()) {
      verify(field, context);
    }
    for (MethodInfo method : classInfo.getMethods()) {
      for (Variable param : method.getParameters()) {
        verify(param, context);
      }
      for (Variable param : method.getLocalVariables()) {
        verify(param, context);
      }
    }
    Assert.assertTrue(errors, errors.length() == 0);
  }

  public void verify(Variable variable, TestabilityVisitor context) {
    if (variable.getName().equals("this")) {
    } else if (variable.getName().endsWith("_I")) {
      if (!context.getGlobalVariables().isInjectable(variable)) {
        errors += "\n" + variable + " should be injectable";
      }
    } else if (variable.getName().endsWith("_NI")) {
      if (context.getGlobalVariables().isInjectable(variable)) {
        errors += "\n" + variable + " should be non injectable";
      }
    } else {
      errors += "\n" + variable + " should end with '_I' or '_NI'.";
    }
  }


}
