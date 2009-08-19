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

public class LocalVariableStateTest extends TestCase {

  VariableState globals = new VariableState();
  LocalVariableState locals = new LocalVariableState(globals);
  Variable instance = new Variable("var", null, false, false);
  Variable lod = new Variable("lod", null, false, false);
  FieldInfo field = new FieldInfo(null, "field", null, false, false, false);

  public void testGlobalRouting() throws Exception {
    locals.setGlobal(field);
    locals.setInjectable(field);

    assertTrue(globals.isGlobal(field));
    assertTrue(globals.isInjectable(field));
  }

  public void testLocalRouting() throws Exception {
    locals.setGlobal(instance);
    locals.setInjectable(instance);

    assertTrue(locals.isGlobal(instance));
    assertTrue(locals.isInjectable(instance));
  }

  public void testToString() throws Exception {
    locals.setGlobal(instance);
    locals.setInjectable(field);
    locals.setLoDCount(lod, 123);
    String text = locals.toString();
    assertTrue(text, text.contains("var"));
    assertTrue(text, text.contains("field"));
    assertTrue(text, text.contains("lod"));
    assertTrue(text, text.contains("123"));
  }

  public void testLodCount() throws Exception {
    locals.setLoDCount(lod, 123);
    assertEquals(123, locals.getLoDCount(lod));
    assertEquals(0, locals.getLoDCount(instance));
  }

}
