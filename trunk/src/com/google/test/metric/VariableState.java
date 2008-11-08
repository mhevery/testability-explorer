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
// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric;

import java.util.HashSet;
import java.util.Set;

public class VariableState {
  private final Set<Variable> injectables = new HashSet<Variable>();
  private final Set<Variable> globals = new HashSet<Variable>();

  boolean isGlobal(Variable var) {
    if (var == null) {
      return false;
    }
    if (var.isGlobal()) {
      return true;
    }
    if (globals.contains(var)) {
      return true;
    }
    if (var instanceof LocalField) {
      LocalField field = (LocalField) var;
      return isGlobal(field.getInstance()) || isGlobal(field.getField());
    }
    return false;
  }

  boolean isInjectable(Variable var) {
    if (var == null) {
      return false;
    }
    if (injectables.contains(var)) {
      return true;
    } else {
      if (var instanceof LocalField) {
        return isInjectable(((LocalField) var).getField());
      } else {
        return false;
      }
    }
  }

  void setGlobal(Variable var) {
    globals.add(var);
  }

  void setInjectable(Variable var) {
    injectables.add(var);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("\nInjectables:");
    for (Variable var : injectables) {
      buf.append("\n   ");
      buf.append(var);
    }
    buf.append("\nGlobals:");
    for (Variable var : globals) {
      buf.append("\n   ");
      buf.append(var);
    }
    return buf.toString();
  }

}