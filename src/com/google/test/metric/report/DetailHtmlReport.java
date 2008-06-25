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

import static java.lang.Math.min;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.test.metric.ClassCost;
import com.google.test.metric.LineNumberCost;
import com.google.test.metric.MethodCost;

public class DetailHtmlReport {

  static class LineNumberCostComparator implements
      Comparator<LineNumberCost> {
    public int compare(LineNumberCost cost1, LineNumberCost cost2) {
      long c1 = cost1.getMethodCost().getOverallCost();
      long c2 = cost2.getMethodCost().getOverallCost();
      return (int) (c2 - c1);
    }
  }

  public static class MethodCostComparator implements
      Comparator<MethodCost> {
    public int compare(MethodCost cost1, MethodCost cost2) {
      long c1 = cost1.getOverallCost();
      long c2 = cost2.getOverallCost();
      return (int) (c2 - c1);
    }
  }

  private final PrintStream out;
  private final int maxLineCount;
  private final int maxMethodCount;

  public DetailHtmlReport(PrintStream out, int maxMethodCount, int maxLineCount) {
    this.out = out;
    this.maxMethodCount = maxMethodCount;
    this.maxLineCount = maxLineCount;
  }

  protected void write(String text) {
    out.println(text);
  }

  public void write(LineNumberCost lineNumberCost) {
    String text = "<div class=\"Line\">" +
    		"<span class=\"lineNumber\">line&nbsp;{lineNumber}:</span>" +
    		"{methodName} [&nbsp;{cost}&nbsp;]" +
    		"</div>";
    text = text.replace("{lineNumber}", "" + lineNumberCost.getLineNumber());
    text = text.replace("{methodName}", "" +
        lineNumberCost.getMethodCost().getMethodName());
    text = text.replace("{cost}", "" + lineNumberCost.getMethodCost().getOverallCost());
    write(text);
  }

  public void write(MethodCost method) {
    String text = "<div class=\"Method\">" + "<span class='expand'>[+]</span>"
        + "{methodName} [&nbsp;{cost}&nbsp;]";
    text = text.replace("{methodName}", "" + method.getMethodName());
    text = text.replace("{cost}", "" + method.getOverallCost());
    write(text);

    List<LineNumberCost> lines = method.getOperationCosts();
    Collections.sort(lines, new LineNumberCostComparator());
    for (LineNumberCost line : lines.subList(0, min(maxLineCount, lines.size()))) {
      write(line);
    }
    write("</div>");
  }

  /**
   * Generates html code for cost of Class, including the costs of the methods
   * inside the class and the lines in the method
   */
  public void write(ClassCost classCost) {
    String text = "<div class=\"Class\">" + "<span class='expand'>[+]</span>"
      + "{className} [&nbsp;{cost}&nbsp;]";
    text = text.replace("{className}", "" + classCost.getClassName());
    text = text.replace("{cost}", "" + classCost.getOverallCost());
    write(text);

    List<MethodCost> methods = classCost.getMethods();
    Collections.sort(methods, new MethodCostComparator());

    for (MethodCost method : methods.subList(0, min(maxMethodCount, methods.size()))) {
      write(method);
    }
    write("</div>");
  }

}
