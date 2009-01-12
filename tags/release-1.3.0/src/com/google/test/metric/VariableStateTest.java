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
package com.google.test.metric;

import junit.framework.TestCase;

public class VariableStateTest extends TestCase {

  VariableState state = new VariableState();
  Variable instance = new Variable("var", null, false, false);
  FieldInfo field = new FieldInfo(null, "field", null, false, false, false);
  Variable localField = new LocalField(instance, field);


  public void testGlobals() throws Exception {
    state.setGlobal(instance);
    assertTrue(state.isGlobal(instance));
    assertFalse(state.isGlobal(null));
  }

  public void testInjectables() throws Exception {
    state.setInjectable(instance);
    assertTrue(state.isInjectable(instance));
    assertFalse(state.isInjectable(null));
  }

  public void testToString() throws Exception {
    state.setGlobal(instance);
    state.setInjectable(field);
    String text = state.toString();
    assertTrue(text, text.contains("var"));
    assertTrue(text, text.contains("field"));
  }

  public void testLocalFieldIsGlobalIfInstanceIsGlobal() throws Exception {
    state.setGlobal(instance);
    assertTrue(state.isGlobal(localField));
  }

  public void testLocalFieldIsGlobalIfFieldIsGlobal() throws Exception {
    state.setGlobal(field);
    assertTrue(state.isGlobal(localField));
  }

  public void testLocalFieldIsInjectableIfInstanceIsInjectable() throws Exception {
    state.setInjectable(field);
    assertTrue(state.isInjectable(localField));
  }

}
