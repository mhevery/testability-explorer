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

import static java.lang.System.getProperty;

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.LineNumberCost;
import com.google.test.metric.MethodCost;

import junit.framework.TestCase;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class DetailHtmlReportTest extends TestCase {

  public static final String NEW_LINE = getProperty("line.separator");

  ByteArrayOutputStream out = new ByteArrayOutputStream();
  PrintStream stream = new PrintStream(out, true);

  public void testWriteLineCost() throws Exception {
    LineNumberCost lineCost = new LineNumberCost(123,
        createMethodCallWithOverallCost("a.methodName()V", 64));

    DetailHtmlReport report = new DetailHtmlReport(stream);
    report.write(lineCost);
    String text = out.toString();

    assertTrue(text, text.contains("<div class=\"Line\""));
    assertTrue(text, text.contains("123"));
    assertTrue(text, text.contains("methodName"));
    assertTrue(text, text.contains("64"));
    assertTrue(text, text.endsWith("</div>" + NEW_LINE));
  }

  private MethodCost createMethodCallWithOverallCost(String methodName, int overallCost) {
    MethodCost cost = new MethodCost(methodName, -1, overallCost);
    cost.link(new CostModel(1, 1));
    assertEquals(overallCost, cost.getOverallCost());
    return cost;
  }

  public void testWriteMethodCost() throws Exception {
    DetailHtmlReport report = new DetailHtmlReport(stream) {
      @Override
      public void write(LineNumberCost lineNumberCost) {
        write(" MARKER:" + lineNumberCost.getLineNumber());
      }
    };

    MethodCost method = createMethodCallWithOverallCost("a.methodX()V", 567 + 789);
    method.addMethodCost(123, createMethodCallWithOverallCost("cost1", 567));
    method.addMethodCost(543, createMethodCallWithOverallCost("cost2", 789));
    report.write(method);
    String text = out.toString();
    assertTrue(text, text.contains("<div class=\"Method\""));
    assertTrue(text, text.contains("<span class='expand'>[+]</span>"));
    assertTrue(text, text.contains("methodX"));
    assertTrue(text, text.contains("[&nbsp;" + (567 + 789) + "&nbsp;]"));
    assertTrue(text, text.contains("MARKER:123"));
    assertTrue(text, text.contains("MARKER:543"));
    assertTrue(text, text.endsWith("</div>" + NEW_LINE));
  }

  public void testWriteClassCost() throws Exception {
    DetailHtmlReport report = new DetailHtmlReport(stream) {
      @Override
      public void write(MethodCost methodCost) {
        write (" MARKER:" + methodCost.getMethodName());
      }
    };

    List<MethodCost> methods = new ArrayList<MethodCost>();
    methods.add(createMethodCallWithOverallCost("methodX", 233));
    methods.add(createMethodCallWithOverallCost("methodY", 544));
    ClassCost classCost = new ClassCost("classFoo", methods);
    classCost.link(new CostModel(1, 1));
    report.write(classCost);
    String text = out.toString();

    assertTrue(text, text.contains("<div class=\"Class\""));
    assertTrue(text, text.contains("<span class='expand'>[+]</span>"));
    assertTrue(text, text.contains("classFoo"));
    assertTrue(text, text.contains("[&nbsp;" + 544 + "&nbsp;]"));
    assertTrue(text, text.contains("MARKER:methodX"));
    assertTrue(text, text.contains("MARKER:methodY"));
    assertTrue(text, text.endsWith("</div>" + NEW_LINE));
  }

}
