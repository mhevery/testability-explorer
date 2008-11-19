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

import org.objectweb.asm.Opcodes;

import com.google.test.metric.ClassInfo;
import com.google.test.metric.Visibility;

public class MethodVisitorBuilderTest extends TestCase {

  public void testSwap() throws Exception {
    ClassInfo classInfo = new ClassInfo("TestClass", false, null, null);
    MethodVisitorBuilder builder = new MethodVisitorBuilder(null , classInfo , "test",
        "()V", null, null, true, false, Visibility.PUBLIC);
    builder.visitInsn(Opcodes.ICONST_0);
    builder.visitInsn(Opcodes.ICONST_0);
    builder.visitInsn(Opcodes.SWAP);
    builder.visitEnd();
  }

  public void testNoop() throws Exception {
    ClassInfo classInfo = new ClassInfo("TestClass", false, null, null);
    MethodVisitorBuilder builder = new MethodVisitorBuilder(null , classInfo , "test",
        "()V", null, null, true, false, Visibility.PUBLIC);
    builder.visitInsn(Opcodes.NOP);
    builder.visitEnd();
  }

}
