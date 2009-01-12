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

import java.util.Map;

public abstract class ViolationCost {

  private final int lineNumber;
  protected final Cost cost;

  /**
   * @param lineNumber
   *          that the {@code methodCost} was called on for the class that
   *          contains this cost.
   * @param cost
   *          the cost of the method getting called from this {@code LineNumber}
   */
  public ViolationCost(int lineNumber, Cost cost) {
    this.lineNumber = lineNumber;
    this.cost = cost;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public abstract String getReason();

  @Override
  public String toString() {
    return "Line " + lineNumber + ": " + getDescription() + " (" + getReason() + ")";
  }

  // TODO: (misko) get rid of this method
  public abstract void link(Cost directCost, Cost dependantCost);

  public Cost getCost() {
    return cost;
  }

  public abstract String getDescription();

  public Map<String, Object> getAttributes() {
    Map<String, Object> atts = cost.getAttributes();

    atts.put("reason", getReason());
    atts.put("line", getLineNumber());
    return atts;
  }

  public boolean isImplicit() {
    return false;
  }
}
