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

import static java.util.Arrays.asList;

import java.io.StringWriter;

import junit.framework.TestCase;

import org.xml.sax.SAXException;

import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.GlobalCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvokationCost;
import com.google.test.metric.ViolationCost;
import static com.google.test.metric.Reason.*;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class XMLReportTest extends TestCase {

  private static final String XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
  private final StringWriter out = new StringWriter();
  private final XMLSerializer handler = new XMLSerializer();
  private final CostModel costModel = new CostModel();

  @Override
  protected void setUp() throws Exception {
    handler.setOutputCharStream(out);
    handler.startDocument();
    OutputFormat format = new OutputFormat();
    format.setIndenting(true);
    handler.setOutputFormat(format);
  }

  private void assertXMLEquals(String expected) throws SAXException {
    handler.endDocument();
    assertEquals(XML_HEADER + expected, out.toString().trim());
  }

  private void write(String text) throws SAXException {
    char[] chars = new char[text.length()];
    text.getChars(0, text.length(), chars, 0);
    handler.characters(chars, 0, chars.length);
  }

  public void testPrintCost() throws Exception {
    XMLReport report = new XMLReport(handler, costModel, 0, 0, 0);

    MethodCost methodCost = new MethodCost("methodName", 1, false, false);
    methodCost.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    methodCost.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    ViolationCost violation = new MethodInvokationCost(123, methodCost,
        IMPLICIT_STATIC_INIT, Cost.cyclomatic(2).add(Cost.global(3)), null);
    report.writeCost(violation);
    assertXMLEquals("<cost cyclomatic=\"2\" global=\"3\" line=\"123\" "
        + "lod=\"0\" method=\"methodName\" overall=\"32\" "
        + "reason=\"implicit cost from static initialization\"/>");
  }

  // This was throwing NPE before
  public void testPrintCostNullReason() throws Exception {
    XMLReport report = new XMLReport(handler, costModel, 0, 0, 0);

    MethodCost methodCost = new MethodCost("methodName", 1, false, false);
    methodCost.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    methodCost.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    ViolationCost violation = new MethodInvokationCost(123, methodCost,
        NON_OVERRIDABLE_METHOD_CALL, Cost.cyclomatic(2).add(Cost.global(3)), null);
    report.writeCost(violation);
    assertXMLEquals("<cost cyclomatic=\"2\" global=\"3\" line=\"123\" "
        + "lod=\"0\" method=\"methodName\" overall=\"32\" "
        + "reason=\"" + NON_OVERRIDABLE_METHOD_CALL + "\"/>");
  }

  public void testPrintMethodCost() throws Exception {
    XMLReport report = new XMLReport(handler, costModel, 0, 0, 0) {
      @Override
      public void writeCost(ViolationCost violation) throws SAXException {
        write("L" + violation.getLineNumber() + ",");
      }

    };
    MethodCost methodCost = new MethodCost("methodName", 123, false, false);
    methodCost.addCostSource(new GlobalCost(123, null, Cost.global(1)));
    methodCost.addCostSource(new CyclomaticCost(234, Cost.cyclomatic(1)));
    methodCost.addCostSource(new CyclomaticCost(345, Cost.cyclomatic(1)));
    methodCost.addCostSource(new GlobalCost(456, null, Cost.global(1)));
    methodCost.link();
    report.writeCost(methodCost);
    assertXMLEquals("<method cyclomatic=\"2\" global=\"2\" line=\"123\" "
        + "lod=\"0\" name=\"methodName\" overall=\"22\">L123,L234,L345,L456,</method>");
  }

  public void testPrintClassCost() throws Exception {
    XMLReport report = new XMLReport(handler, costModel, 0, 0, 0) {
      @Override
      public void writeCost(MethodCost methodCost) throws SAXException {
        write(methodCost.getMethodName() + "()");
      }
    };
    MethodCost m1 = new MethodCost("M1", -1, false, false);
    m1.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    m1.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    MethodCost m2 = new MethodCost("M2", -1, false, false);
    m2.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    m1.link();
    m2.link();
    ClassCost classCost = new ClassCost("className", asList(m1, m2));
    report.writeCost(classCost);
    assertXMLEquals("<class class=\"className\" cost=\"1\">M1()M2()</class>");
  }

  public void testWholeDocument() throws Exception {
    XMLReport report = new XMLReport(handler, costModel, 1, 2, 3) {
      @Override
      public void writeCost(ClassCost cost) throws SAXException {
        write(cost.getClassName() + ";");
      }
    };
    report.printHeader();
    MethodCost m1 = new MethodCost("M1", -1, false, false);
    m1.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    m1.addCostSource(new CyclomaticCost(0, Cost.cyclomatic(1)));
    m1.link();
    ClassCost c1 = new ClassCost("C1", asList(m1));
    ClassCost c2 = new ClassCost("C2", asList(m1));
    report.addClassCost(c1);
    report.addClassCost(c2);
    report.printFooter();
    assertXMLEquals("<testability excellent=\"0\" good=\"0\" " +
    		"needsWork=\"2\" overall=\"2\">C1;C2;</testability>");
  }

}
