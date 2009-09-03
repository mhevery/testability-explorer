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
package com.google.test.metric.asm;

import junit.framework.TestCase;

public class JavaNamerTest extends TestCase {

  private JavaNamer namer = new JavaNamer();

  public void testNameMethod() throws Exception {
    assertEquals("void m()", namer.nameMethod("a.b.C", "m", "()V"));
    assertEquals("long m(int)", namer.nameMethod("a.b.C", "m", "(I)J"));
    assertEquals("long m(int, java.lang.String)", namer.nameMethod("a.b.C", "m", "(ILjava/lang/String;)J"));
    assertEquals("a.B.C m(a.B.C)", namer.nameMethod("a.B.C", "m", "(La/B$C;)La/B$C;"));
    assertEquals("void m(int, int[])", namer.nameMethod("a.B.C", "m", "(I[I)V"));
  }

  public void testStaticInit() throws Exception {
    assertEquals("<static init>()", namer.nameMethod("a.b.C", "<clinit>", "()V"));
  }

  public void testConstructor() throws Exception {
    assertEquals("C()", namer.nameMethod("a.b.C", "<init>", "()La/b/C;"));
    assertEquals("C()", namer.nameMethod("a.B$C", "<init>", "()La/b/C;"));
    assertEquals("C()", namer.nameMethod("C", "<init>", "()La/b/C;"));
  }

}
