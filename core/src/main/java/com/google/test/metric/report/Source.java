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

import java.util.ArrayList;
import java.util.List;

import com.google.test.metric.Cost;
import com.google.test.metric.MethodCost;

public class Source {
  public static class Line {

    private final int lineNumber;
    private final String text;
    private final List<MethodCost> methodCosts = new ArrayList<MethodCost>();
    private final Cost cost = new Cost();

    public Line(int lineNumber, String text) {
      this.lineNumber = lineNumber;
      this.text = text;
    }

    public int getLineNumber() {
      return lineNumber;
    }

    public String getText() {
      return text;
    }

    public String getScore() {
      return cost.toHtmlReportString();
    }

    public void addCost(Cost cost) {
      this.cost.add(cost);
    }

    public Cost getCost() {
      return cost;
    }

    public List<MethodCost> getMethodCosts() {
      return methodCosts;
    }

    public void addMethodCost(MethodCost methodCost) {
      this.methodCosts.add(methodCost);
    }

    @Override
    public String toString() {
      return String.format("%-3d %10s | %s", lineNumber, cost, text);
    }

  }

  private final List<Line> lines;

  public Source(List<Line> lines) {
    this.lines = lines;
  }

  public Line getLine(int line) {
    line = Math.max(1, line);
    line = Math.min(lines.size(), line);
    if (lines.size() == 0) {
      return new Line(0, "");
    }
    return lines.get(line - 1);
  }

  public List<Line> getLines() {
    return lines;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    for (Line line : lines) {
      buf.append(line);
      buf.append("\n");
    }
    return buf.toString();
  }

}
