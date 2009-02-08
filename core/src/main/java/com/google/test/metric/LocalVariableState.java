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

import java.util.HashMap;
import java.util.Map;

public class LocalVariableState extends VariableState {
  private final Map<Variable, Integer> lodCount = new HashMap<Variable, Integer>();
  private final VariableState globalVariables;

  public LocalVariableState(VariableState globals) {
    this.globalVariables = globals;
  }

  public VariableState getGlobalVariableState() {
    return globalVariables;
  }

  @Override
  void setGlobal(Variable var) {
    if (var instanceof LocalField || var instanceof FieldInfo) {
      globalVariables.setGlobal(var);
    } else {
      super.setGlobal(var);
    }
  }

  @Override
  void setInjectable(Variable var) {
    if (var instanceof LocalField || var instanceof FieldInfo) {
      globalVariables.setInjectable(var);
    } else {
      super.setInjectable(var);
    }
  }

  @Override
  boolean isGlobal(Variable var) {
    if (super.isGlobal(var)) {
      return true;
    } else {
      return globalVariables.isGlobal(var);
    }
  }

  @Override
  boolean isInjectable(Variable var) {
    if (super.isInjectable(var)) {
      return true;
    } else {
      return globalVariables.isInjectable(var);
    }
  }

  int getLoDCount(Variable variable) {
    Integer count = lodCount.get(variable);
    if (count == null) {
      if (variable instanceof LocalField) {
        LocalField localField = (LocalField) variable;
        return getLoDCount(localField.getField());
      } else {
        return 0;
      }
    } else {
      return count.intValue();
    }
  }

  void setLoDCount(Variable value, int newCount) {
    Integer count = lodCount.get(value);
    int intCount = count == null ? 0 : count;
    if (intCount < newCount) {
      lodCount.put(value, newCount);
    }
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("GLOBALS:\n");
    buf.append(globalVariables.toString());
    buf.append(super.toString());
    buf.append("\nLod:");
    for (Variable var : lodCount.keySet()) {
      buf.append("\n   ");
      buf.append(var);
      buf.append(": ");
      buf.append(lodCount.get(var));
    }
    return buf.toString();
  }

}