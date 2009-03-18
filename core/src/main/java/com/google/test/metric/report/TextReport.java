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

import com.google.test.metric.ClassCost;
import com.google.test.metric.CostModel;

import java.io.PrintStream;


public class TextReport extends SummaryReport implements Report {

  private final PrintStream out;
  public TextReport(PrintStream out, CostModel costModel, int maxExcellentCost, int maxAcceptableCost, int worstOffenderCount) {
    super(costModel, maxExcellentCost, maxAcceptableCost, worstOffenderCount);
    this.out = out;
  }

  public TextReport(PrintStream out, CostModel costModel, ReportOptions options) {
    this(out, costModel, options.getMaxExcellentCost(), options.getMaxAcceptableCost(),
            options.getWorstOffenderCount());
  }

  public void printSummary() {
    int total = costs.size();
    out.printf("      Analyzed classes: %5d%n", total);
    out.printf(" Excellent classes (.): %5d %5.1f%%%n", excellentCount, 100f * excellentCount / total);
    out.printf("      Good classes (=): %5d %5.1f%%%n", goodCount, 100f * goodCount / total);
    out.printf("Needs work classes (@): %5d %5.1f%%%n", needsWorkCount, 100f * needsWorkCount / total);
    PieGraph graph = new PieGraph(50, new CharMarker('.', '=', '@'));
    String chart = graph.render(excellentCount, goodCount, needsWorkCount);
    out.printf("             Breakdown: [%s]%n", chart);
  }

  public void printDistribution(int rows, int width) {
    TextHistogram histogram = new TextHistogram(width, rows, new Marker() {
      public char get(int index, float value) {
        if (value < maxExcellentCost) {
          return '.';
        } else if (value < maxAcceptableCost) {
          return '=';
        } else {
          return '@';
        }
      }
    });
    float[] values = new float[costs.size()];
    int i = 0;
    for (int cost : costs) {
      values[i++] = cost;
    }
    for (String graph : histogram.graph(values)) {
      out.println(graph);
    }
  }

  public void printWorstOffenders(int worstOffenderCount) {
    out.println();
    out.println("Highest Cost");
    out.println("============");
    for (ClassCost cost : worstOffenders) {
      out.println(cost);
    }
  }

  /* (non-Javadoc)
   * @see com.google.test.metric.report.Report#print()
   */
  public void printFooter() {
    printSummary();
    printDistribution(25, 70);
    printWorstOffenders(worstOffenderCount);
  }

  public void printHeader() {
  }

}
