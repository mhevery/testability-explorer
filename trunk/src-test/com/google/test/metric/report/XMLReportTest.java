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

import java.io.StringWriter;

import junit.framework.TestCase;

import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CostViolation;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvokationCost;
import com.google.test.metric.CostViolation.Reason;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLReportTest extends TestCase {

  private static final String XML_HEADER = "<?xml version=\"1.0\"?>\n";
  private final StringWriter out = new StringWriter();
  private final XMLSerializer handler = new XMLSerializer();
  private final Cost cost = Cost.create(1, 2, 3, 4);

  @Override
  protected void setUp() throws Exception {
    handler.setOutputCharStream(out);
    handler.startDocument();
  }

  public void testPrintCost() throws Exception {
    XMLReport report = new XMLReport(handler, 0, 0, 0);

    MethodCost methodCost = new MethodCost("methodName", 1, 2) {
      @Override
      public Cost link(CostModel costModel) {
        return cost;
      }
    };
    CostViolation violation = new MethodInvokationCost(123, methodCost,
        Reason.IMPLICIT_STATIC_INIT);
    violation.link(Cost.none(), Cost.none(), null);
    report.writeCost(violation);
    assertXMLEquals("<cost cyclomatic=\"2\" global=\"3\" line=\"123\" "
        + "lod=\"4\" method=\"methodName\" overall=\"1\" "
        + "reason=\"implicit cost from static initialization\"/>");
  }

  private void assertXMLEquals(String expected) {
    assertEquals(XML_HEADER + expected, out.toString());
  }

}
