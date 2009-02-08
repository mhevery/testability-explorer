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
package com.google.test.metric.report;

import static com.google.test.metric.report.Constants.NEW_LINE;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import junit.framework.TestCase;

import com.google.test.metric.CostModel;

public class HtmlReportTest extends TestCase {

  ByteArrayOutputStream out = new ByteArrayOutputStream();
  HtmlReport report = new HtmlReport(new PrintStream(out), new CostModel(), 10, 20, 5, null);

  public void testPrintFooter() throws Exception {
    report.printFooter();
    assertTrue(out.toString().length() > 0);
  }

  public void testPrintSummary() throws Exception {
    report.printSummary();
    assertTrue(out.toString().length() > 0);
  }

  public void testPrintHeader() throws Exception {
    report.printHeader();
    String text = out.toString();

    assertTrue(text, text.contains("<style type=\"text/css\">"));
    assertTrue(text, text.contains("<script type=\"text/javascript\""));
    assertTrue(text, text.contains("Report generated"));
    assertTrue(text, text.contains("function toggle(element)"));
    assertTrue(text, text.contains("function clickHandler(event)"));
  }

  public void testPrintWorstOffenders() throws Exception {
    report.printWorstOffenders(65656);
    String text = out.toString();
    assertTrue(text.contains("<div onclick='clickHandler(event)'>"));
    assertTrue(text.endsWith("</div>" + NEW_LINE));
  }
}
