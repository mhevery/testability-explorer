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



public class LoDViolation extends ViolationCost {

  private final String methodName;
  private final int distance;

  public LoDViolation(int lineNumber, String methodName, Cost lod, int distance) {
    super(lineNumber, lod, Reason.LAW_OF_DEMETER);
    this.methodName = methodName;
    this.distance = distance;
  }

  @Override
  public String getDescription() {
    return methodName + "[distance=" + distance + "]";
  }

  @Override
  public void link(Cost directCost, Cost dependantCost) {
    directCost.add(getCost());
  }

}
