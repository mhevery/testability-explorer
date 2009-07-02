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

import junit.framework.TestCase;

import com.google.test.metric.FieldInfo;
import com.google.test.metric.JavaType;
import com.google.test.metric.LocalField;
import com.google.test.metric.MethodCost;
import com.google.test.metric.Type;
import com.google.test.metric.Variable;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class SourceElementFormattingTest extends TestCase {

  public void testMethodFormatting() throws Exception {
    MethodCost cost = new MethodCost("void translation_unit()", 1, false, false, false);
    assertEquals("void translation_unit()", cost.getDescription());
  }

  public void testVariableFormatting() throws Exception {
    Type type = JavaType.fromJava("com.google.test.metric.example.MutableGlobalState." +
        "FinalGlobalExample$Gadget");
    FieldInfo field = new FieldInfo(null, "finalInstance", type, false, false, false);
    LocalField localField = new LocalField(new Variable(
        "com.google.test.metric.example.MutableGlobalState.FinalGlobalExample$" +
            "FinalGlobal.finalInstance",
        type, false, false), field);
    assertEquals("FinalGlobalExample$Gadget finalInstance", localField.getDescription());
  }

  public void testDotsInMethodParameters() throws Exception {
    MethodCost cost = new MethodCost(
        "com.google.test.metric.cpp.dom.TranslationUnit parse2(java.lang.String, java.lang.String)",
        12, false, false, false);
    assertEquals("TranslationUnit parse2(String, String)", cost.getDescription());
  }
}
