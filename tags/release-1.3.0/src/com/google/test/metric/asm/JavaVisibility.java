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
package com.google.test.metric.asm;

import org.objectweb.asm.Opcodes;

import com.google.test.metric.Visibility;

public class JavaVisibility {

  public static Visibility valueFromJavaBytecode(int access) {
    if ((access & Opcodes.ACC_PUBLIC) == Opcodes.ACC_PUBLIC) {
      return Visibility.PUBLIC;
    } else if ((access & Opcodes.ACC_PROTECTED) == Opcodes.ACC_PROTECTED) {
      return Visibility.PROTECTED;
    } else if ((access & Opcodes.ACC_PRIVATE) == Opcodes.ACC_PRIVATE) {
      return Visibility.PRIVATE;
    } else {
      return Visibility.PACKAGE;
    }
  }
}
