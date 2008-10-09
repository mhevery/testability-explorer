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

import junit.framework.TestCase;

public class MethodInfoTest extends TestCase {

  public void testGetFullName() {
    MethodInfo method = new MethodInfo(new ClassInfo("com.foo.bar", false,
        null, null), "method", 0, "(Ljava/lang/String;)V", null, null, null,
        null, 1, null, false);
    assertEquals("void method(java.lang.String)", method.getFullName());

    method = new MethodInfo(new ClassInfo("f.a.b", false, null, null),
        "mymethod", 0, "(IDLjava/lang/Thread;)Ljava/lang/Object;", null, null,
        null, null, 1, null, false);
    assertEquals("java.lang.Object mymethod(int, double, java.lang.Thread)", method
        .getFullName());

    method = new MethodInfo(new ClassInfo("c.b.ui.UI$ViewHandler", false, null,
        null), "<clinit>", 0, "()V", null, null, null, null, 1, null, false);
    assertEquals("c.b.ui.UI$ViewHandler()", method.getFullName());

    method = new MethodInfo(new ClassInfo("c.b.ui.UI$ViewHandler", false, null,
        null), "<init>", -1, "(I)V", null, null, null, null, 1, null, false);
    assertEquals("c.b.ui.UI$ViewHandler(int)", method.getFullName());
  }

  public void testDeconstructParameters() {
    MethodInfo method = new MethodInfo(null, null, 0, null, null, null, null,
        null, 1, null, false);

    assertEquals("int", method.deconstructParameters("I"));
    assertEquals("double[][][]", method.deconstructParameters("[[[D"));
    assertEquals("java.lang.Object", method.deconstructParameters("Ljava/lang/Object;"));

  }
}
