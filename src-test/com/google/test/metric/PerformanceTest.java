/*
 * Copyright 2008 Google Inc.
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
package com.google.test.metric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Properties;

public class PerformanceTest {

  public static void main(String[] args) throws Exception {
    new PerformanceTest().testStringAnalysisPerformance();
  }

  Runtime runtime = Runtime.getRuntime();
  JavaClassRepository repo = new JavaClassRepository();
  MetricComputer computer = new MetricComputer(repo, null,
      new RegExpWhiteList(), new CostModel());
  File reportDir = new File("target/reports/perf");

  public void testStringAnalysisPerformance() throws Exception {
    System.gc();
    long startMem = usedMemory();
    long startTime = System.currentTimeMillis();
    ClassCost runPerformance = runPerformance(String.class);
    long endTime = System.currentTimeMillis();
    long endMem = usedMemory();
    System.gc();
    runPerformance.hashCode();
    runPerformance = null;
    System.gc();
    long end2Mem = usedMemory();

    double time = (endTime - startTime) / 1000d;
    double memTotal = (endMem - startMem) / 1024d / 1024d;
    double memCost = (endMem - end2Mem) / 1024d / 1024d;
    System.out.println("Execution time: " + time + " sec");
    System.out.println("Total Memory: " + memTotal + " MB");
    System.out.println("ClassCost Memory: " + memCost + " MB");
    reportDir.mkdirs();
    writeResult(time, "time.prop");
    writeResult(memTotal, "memTotal.prop");
    writeResult(memCost, "memCost.prop");
  }

  private void writeResult(double result, String file)
      throws FileNotFoundException {
    Properties timeProp = new Properties();
    timeProp.setProperty("YVALUE", ""+result);
    PrintStream os = new PrintStream(new File(reportDir, file));
    timeProp.list(os);
    os.close();
  }

  private long usedMemory() {
    return runtime.totalMemory() - runtime.freeMemory();
  }

  private ClassCost runPerformance(Class<String> clazz) {
    return computer.compute(clazz.getName());
  }

}
