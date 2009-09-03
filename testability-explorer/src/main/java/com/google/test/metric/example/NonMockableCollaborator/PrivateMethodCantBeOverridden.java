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
package com.google.test.metric.example.NonMockableCollaborator;

/**
 * This is an example of how a private method cannot be overridden,
 * so it prevents a seam between that class and a collaborator.
 */
public class PrivateMethodCantBeOverridden {
  /**
   * This class has a private method with a cyclomatic complexity of 2.
   */
  public static class HasPrivateMethod {
    private String computeString() {
      boolean x = true;
      boolean a = x ? false : true;
      String b = a ? "hello" : "goodbye";
      return b;
    }
  }

  private HasPrivateMethod collaborator;
  /**
   * Even though the collaborator is injected into the constructor...
   */
  public PrivateMethodCantBeOverridden(HasPrivateMethod collaborator) {
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
