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

import static com.google.test.metric.asm.SignatureParser.parse;

import java.util.List;

import junit.framework.TestCase;

import com.google.test.metric.JavaType;
import com.google.test.metric.Type;

public class SignatureParserTest extends TestCase {

  public void testParsePrimitive() throws Exception {
    assertSame(JavaType.LONG, parse("()J").getReturnType());
  }

  public void testParseObject() throws Exception {
    assertEquals(JavaType.fromClass(String.class),
        parse("()Ljava/lang/String;").getReturnType());
  }

  public void testParseArray() throws Exception {
    assertEquals(JavaType.fromClass(String[].class), parse(
        "()[Ljava/lang/String;").getReturnType());
  }

  public void testParseDoubleArray() throws Exception {
    assertEquals(JavaType.fromClass(String[][].class), parse(
        "()[[Ljava/lang/String;").getReturnType());
  }

  public void testParseArgsDoubleArray() throws Exception {
    List<Type> parameters = parse("([[Ljava/lang/String;J)V")
        .getParameters();
    assertEquals(JavaType.fromClass(String[][].class), parameters.get(0));
    assertEquals(JavaType.LONG, parameters.get(1));
  }

}
