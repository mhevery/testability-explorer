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
package com.google.test.metric.report;

import com.google.test.metric.*;
import com.google.test.metric.report.issues.SourceElement;
import junit.framework.TestCase;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class SourceElementFormattingTest extends TestCase {

  public void testMethodFormatting() throws Exception {
    SourceElement cost = new MethodCost("void translation_unit()", 1, false, false);
    assertEquals("void translation_unit()", cost.shortFormat());
  }

  public void testVariableFormatting() throws Exception {
    Type type = JavaType.fromJava("com.google.test.metric.example.MutableGlobalState." +
        "FinalGlobalExample$Gadget");
    FieldInfo field = new FieldInfo(null, "finalInstance", type, false, false, false);
    SourceElement cost = new LocalField(new Variable(
        "com.google.test.metric.example.MutableGlobalState.FinalGlobalExample$" +
            "FinalGlobal.finalInstance",
        type, false, false), field);
    assertEquals("FinalGlobalExample$Gadget finalInstance", cost.shortFormat());
  }

  public void testDotsInMethodParameters() throws Exception {
    SourceElement cost = new MethodCost(
        "com.google.test.metric.cpp.dom.TranslationUnit parse2(java.lang.String, java.lang.String)",
        12, false, false);
    assertEquals("TranslationUnit parse2(String, String)", cost.shortFormat());
  }
}
