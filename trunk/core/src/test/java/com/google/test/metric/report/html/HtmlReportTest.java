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
package com.google.test.metric.report.html;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

import junit.framework.TestCase;

import com.google.test.metric.CostModel;
import com.google.test.metric.ClassCost;
import com.google.test.metric.MethodCost;

public class HtmlReportTest extends TestCase {

  ByteArrayOutputStream out = new ByteArrayOutputStream();
  HtmlReport report = new HtmlReport(new PrintStream(out), new CostModel(), 10, 20, 5, null);

  public void testPrintReport() throws Exception {
    report.printHeader();
    report.addClassCost(new ClassCost("classFoo", Arrays.asList(new MethodCost("methodFoo", 1))));
    report.printFooter();
    String text = out.toString();

    assertTrue(text, text.contains("<script type=\"text/javascript\""));
    assertTrue(text, text.contains("Report generated"));
    assertTrue(text, text.contains("function toggle(element)"));
    assertTrue(text, text.contains("function clickHandler(event)"));
  }

}
