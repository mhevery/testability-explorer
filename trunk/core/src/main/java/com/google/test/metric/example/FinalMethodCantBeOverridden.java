/*
 * Copyright 2009 Google Inc.
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
package com.google.test.metric.example;

/**
 * This class demonstrates that if a method is final, it cannot be
 * overridden to make a seam.
 */
public class FinalMethodCantBeOverridden {
  /**
   * This class has a final method with a cyclomatic complexity of 2.
   */
  public static class HasFinalMethod {
    final String computeString() {
      boolean x = true;
      boolean a = x ? false : true;
      String b = a ? "hello" : "goodbye";
      return b;
    }
  }

  private HasFinalMethod collaborator;

  // Even though the collaborator is injected into the constructor...
  public FinalMethodCantBeOverridden(HasFinalMethod collaborator) {
    this.collaborator = collaborator;
  }

  /**
   * there is no seam between us and the computeString() method, so we have a
   * non-mockable cost of 2.
   */
  public void NeedsAnInstance() {
    collaborator.computeString();
  }
}
