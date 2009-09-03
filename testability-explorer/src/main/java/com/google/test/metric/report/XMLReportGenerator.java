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

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.ViolationCost;

public class XMLReportGenerator extends SummaryReportModel implements ReportGenerator {

  private final ContentHandler out;
  private final CostModel costModel;
  public static final String CLASS_NODE = "class";
  public static final String CLASS_NAME_ATTRIBUTE = ClassCost.CLASS_NAME;
  public static final String CLASS_COST_ATTRIBUTE = "cost";
  public static final String METHOD_NODE = "method";
  public static final String METHOD_NAME_ATTRIBUTE = MethodCost.METHOD_NAME_ATTRIBUTE;
  public static final String METHOD_OVERALL_COST_ATTRIBUTE = "overall";

  public XMLReportGenerator(ContentHandler out, CostModel costModel, int maxExcellentCost,
      int maxAcceptableCost, int worstOffenderCount) {
    super(costModel, maxExcellentCost, maxAcceptableCost, worstOffenderCount);
    this.out = out;
    this.costModel = costModel;
  }

  public XMLReportGenerator(ContentHandler out, CostModel costModel, ReportOptions options) {
    this(out, costModel, options.getMaxExcellentCost(),
        options.getMaxAcceptableCost(), options.getWorstOffenderCount());
  }

  public void printHeader() {
  }

  public void printFooter() {
    try {
      out.startDocument();
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("overall", getOverall());
      values.put("excellent", excellentCount);
      values.put("good", goodCount);
      values.put("needsWork", needsWorkCount);
      startElement("testability", values);
      for (ClassCost classCost : worstOffenders) {
        writeCost(classCost);
      }
      endElement("testability");
      out.endDocument();
    } catch (SAXException e) {
      throw new RuntimeException(e);
    }
  }

  private void writeElement(String elementName, Map<String, Object> values)
      throws SAXException {
    startElement(elementName, values);
    endElement(elementName);
  }

  private void endElement(String elementName) throws SAXException {
    out.endElement(null, elementName, elementName);
  }

  private void startElement(String elementName, Map<String, Object> values)
      throws SAXException {
    AttributesImpl atts = new AttributesImpl();
    for (String key : new TreeSet<String>(values.keySet())) {
      atts.addAttribute(null, key, key, null,
          values.get(key) == null ? "" : values.get(key).toString());
    }
    out.startElement(null, elementName, elementName, atts);
  }

  void writeCost(ViolationCost violation) throws SAXException {
    Map<String, Object> attributes = violation.getAttributes();
    attributes.put("overall", costModel.computeOverall(violation.getCost()));
    writeElement("cost", attributes);
  }

  void writeCost(MethodCost methodCost) throws SAXException {
    Map<String, Object> attributes = methodCost.getAttributes();
    attributes.put(METHOD_OVERALL_COST_ATTRIBUTE, costModel.computeOverall(methodCost.getTotalCost()));
    startElement(METHOD_NODE, attributes);
    for (ViolationCost violation : methodCost.getViolationCosts()) {
      writeCost(violation);
    }
    endElement(METHOD_NODE);
  }

  void writeCost(ClassCost classCost) throws SAXException {
    Map<String, Object> attributes = classCost.getAttributes();
    attributes.put(CLASS_COST_ATTRIBUTE, costModel.computeClass(classCost));
    startElement(CLASS_NODE, attributes);
    for (MethodCost cost : classCost.getMethods()) {
      writeCost(cost);
    }
    endElement(CLASS_NODE);
  }

}
