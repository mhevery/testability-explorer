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

public class LineNumberCost {

  /** This attempts to answer "What is the source of each line's cost?" */
  public static enum CostSourceType {
    IMPLICIT_CONSTRUCTOR("implicit cost from construction"),
    IMPLICIT_SETTER("implicit cost calling all setters"),
    IMPLICIT_STATIC_INIT("implicit cost from static initialization"),
    NON_OVERRIDABLE_METHOD_CALL("cost from calling non-overridable method");
    // TODO(jwolter): be able to tell people why this method could not be overridden:
    //  whether it is static, private or final.
    // SOMEDAY(jwolter): it would be nice to make static methods worse than others. Because we don't
    // want to encourage people to subclass for tests.

    private String description;
    CostSourceType(String description) {
      this.description = description;
    }

    @Override
    public String toString() {
      return description;
    }

  }

  private final int lineNumber;
  private final MethodCost methodCost;
  private final CostSourceType costSourceType;

  /**
   * @param lineNumber that the {@code methodCost} was called on for the class that contains this
   * cost.
   * @param methodCost the cost of the method getting called from this {@code LineNumber}
   * @param costSourceType the type of cost, used to help guide people why they are getting charged
   * for each different cost.
   */
  public LineNumberCost(int lineNumber, MethodCost methodCost, CostSourceType costSourceType) {
    this.lineNumber = lineNumber;
    this.methodCost = methodCost;
    this.costSourceType = costSourceType;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public MethodCost getMethodCost() {
    return methodCost;
  }

  public CostSourceType getCostSourceType() {
    return costSourceType;
  }

  @Override
  public String toString() {
    return methodCost.getMethodName() + ":" + lineNumber;
  }

}
