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

import java.io.File;
import java.util.Date;

import junit.framework.TestCase;

import com.google.classpath.ClassPath;
import com.google.classpath.DirectoryClassPath;
import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;
import com.google.test.metric.CostUtil;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.report.Source.Line;

public class SourceReportTest extends TestCase {

  static class TestClass {
    void m1 () {
      CostUtil.staticCost4();
    }
    void m2 (

        ) {
      CostUtil.staticCost3();
    }
  }

  public void testCreateSourceReport() throws Exception {
    ClassPath classPath = new DirectoryClassPath(new File("src-test"));
    SourceLoader loader = new SourceLoader(classPath);
    SourceReport report = new SourceReport(new GradeCategories(0, 0), loader, null, new Date());
    MetricComputer computer = new MetricComputer(new JavaClassRepository(), null, new RegExpWhiteList(), new CostModel());
    ClassCost classCost = computer.compute(TestClass.class.getName());

    ClassReport classReport = report.createClassReport(classCost);
    Source source = classReport.getSource();
    MethodCost m1 = classCost.getMethodCost("void m1()");
    MethodCost m2 = classCost.getMethodCost("void m2()");
    Line l1 = source.getLine(m1.getMethodLineNumber());
    Line l2 = source.getLine(m2.getMethodLineNumber());

    assertTrue(l1.toString(), l1.toString().contains("staticCost4()"));
    assertEquals(4, l1.getCost().getCyclomaticComplexityCost());
    assertTrue(l2.toString(), l2.toString().contains("staticCost3()"));
    assertEquals(3, l2.getCost().getCyclomaticComplexityCost());
  }

}
