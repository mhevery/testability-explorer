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

import com.google.test.metric.Type;
import static com.google.test.metric.asm.SignatureParser.parse;

import junit.framework.TestCase;

import java.util.List;

public class SignatureParserTest extends TestCase {

  public void testParsePrimitive() throws Exception {
    assertSame(Type.LONG, parse("()J").getReturnType());
  }

  public void testParseObject() throws Exception {
    assertEquals(Type.fromClass(String.class),
        parse("()Ljava/lang/String;").getReturnType());
  }

  public void testParseArray() throws Exception {
    assertEquals(Type.fromClass(String[].class), parse(
        "()[Ljava/lang/String;").getReturnType());
  }

  public void testParseDoubleArray() throws Exception {
    assertEquals(Type.fromClass(String[][].class), parse(
        "()[[Ljava/lang/String;").getReturnType());
  }

  public void testParseArgsDoubleArray() throws Exception {
    List<Type> parameters = parse("([[Ljava/lang/String;J)V")
        .getParameters();
    assertEquals(Type.fromClass(String[][].class), parameters.get(0));
    assertEquals(Type.LONG, parameters.get(1));
  }

}
