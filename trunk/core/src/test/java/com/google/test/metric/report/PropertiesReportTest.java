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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Properties;

import junit.framework.TestCase;

import com.google.test.metric.ClassCost;
import com.google.test.metric.Cost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CyclomaticCost;
import com.google.test.metric.MethodCost;
import com.google.test.metric.SourceLocation;

public class PropertiesReportTest extends TestCase {

  ByteArrayOutputStream out = new ByteArrayOutputStream();
  CostModel costModel = new CostModel(1, 1);
  PropertiesReportGenerator report = new PropertiesReportGenerator(out, costModel);

  private static final String CLASS_NAME = "com.google.foo.Bar";
  public void testReport() throws Exception {

    MethodCost methodCost = new MethodCost("doThing", 3, false, false, false);
    methodCost.addCostSource(new CyclomaticCost(new SourceLocation(null, 0), Cost.cyclomatic(1)));
    methodCost.link();
    final ClassCost classCost = new ClassCost(CLASS_NAME, Arrays.asList(methodCost));
    report.addClassCost(classCost);
    report.printFooter();

    String output = out.toString();
    assertTrue(output.contains("Bar"));
    Properties props = new Properties();
    props.load(new ByteArrayInputStream(out.toByteArray()));
    assertEquals(1, Integer.parseInt(props.getProperty(CLASS_NAME)));
  }
}
