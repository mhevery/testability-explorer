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

  /** This attempts to answer "What is the source of each line's cost?" */
  public static enum Reason {
    IMPLICIT_CONSTRUCTOR("implicit cost from construction", true),
    //
    IMPLICIT_SETTER("implicit cost calling all setters", true),
    //
    IMPLICIT_STATIC_INIT("implicit cost from static initialization", true),
    //
    NON_OVERRIDABLE_METHOD_CALL("cost from calling non-overridable method", false),
    // TODO(jwolter): be able to tell people why this method could not be
    // overridden:
    // whether it is static, private or final.
    // SOMEDAY(jwolter): it would be nice to make static methods worse than
    // others. Because we don't
    // want to encourage people to subclass for tests.
    LAW_OF_DEMETER("cost from breaking the Law of Demeter", false),
    //
    GLOBAL("dependency on global mutable state", false),

    CYCLOMATIC_COMPLEXITY("cyclomatic complexity", false);

    private final String description;
    private final boolean isImplicit;

    Reason(String description, boolean implicit) {
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

  private final int lineNumber;
  protected final Reason reason;
  protected final Cost cost;

  /**
   * @param lineNumber
   *          that the {@code methodCost} was called on for the class that
   *          contains this cost.
   * @param cost
   *          the cost of the method getting called from this {@code LineNumber}
   * @param costSourceType
   *          the type of cost, used to help guide people why they are getting
   *          charged for each different cost.
   */
  public ViolationCost(int lineNumber, Cost cost, Reason costSourceType) {
    this.lineNumber = lineNumber;
    this.cost = cost;
    this.reason = costSourceType;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public Reason getCostSourceType() {
    return reason;
  }

  @Override
  public String toString() {
    return "Line " + lineNumber + ": " + getDescription() + " (" + reason + ")";
  }

  // TODO: (misko) get rid of this method
  public abstract void link(Cost directCost, Cost dependantCost);

  public Cost getCost() {
    return cost;
  }

  public abstract String getDescription();

  public Map<String, Object> getAttributes() {
    Map<String, Object> atts = cost.getAttributes();

    atts.put("reason", reason);
    atts.put("line", getLineNumber());
    return atts;
  }

  public boolean isImplicit() {
    return reason.isImplicit();
  }
}
