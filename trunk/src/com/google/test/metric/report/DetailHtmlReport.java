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
import com.google.test.metric.CostViolation;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MethodInvokationCost;

public class DetailHtmlReport {

  static class CostSourceComparator implements
      Comparator<CostViolation> {
    public int compare(CostViolation cost1, CostViolation cost2) {
      int c1 = cost1.getCost().getOvarall();
      int c2 = cost2.getCost().getOvarall();
      return (c2 - c1);
    }
  }

  public static class MethodCostComparator implements
      Comparator<MethodCost> {
    public int compare(MethodCost cost1, MethodCost cost2) {
      int c1 = cost1.getOverallCost();
      int c2 = cost2.getOverallCost();
      return (c2 - c1);
    }
  }

  private final PrintStream out;
  private final int maxLineCount;
  private final int maxMethodCount;
  private final SourceLinkGenerator linkGenerator;

  public DetailHtmlReport(PrintStream out, SourceLinkGenerator linkGenerator,
      int maxMethodCount, int maxLineCount) {
    this.out = out;
    this.linkGenerator = linkGenerator;
    this.maxMethodCount = maxMethodCount;
    this.maxLineCount = maxLineCount;
  }

  protected void write(String text) {
    out.println(text);
  }

  public void write(MethodInvokationCost methodInvocationCost, String classFilePath) {
    String costSourceType = methodInvocationCost.getCostSourceType().toString();
    MethodCost methodCost = methodInvocationCost.getMethodCost();
    String text = "<div class=\"Line\">" +
    		"<span class=\"lineNumber\">line&nbsp;{lineNumber}:</span>" +
    		"{methodName} [&nbsp;{cost}&nbsp;] (source: " + costSourceType + ")" +
    		"</div>";
    text = text.replace("{lineNumber}", "" + methodInvocationCost.getLineNumber());
    text = text.replace("{methodName}", "" +
        linkGenerator.buildLineLink(classFilePath,
            methodInvocationCost.getLineNumber(),
            methodCost.getMethodName()));
    text = text.replace("{cost}", "" + methodCost.getOverallCost());
    write(text);
  }

  public void write(MethodCost method, String classFilePath) {
    String text = "<div class=\"Method\">" + "<span class='expand'>[+]</span>"
        + "{methodName} [&nbsp;{cost}&nbsp;]";
    text = text.replace("{methodName}", "" + method.getMethodName());
    text = text.replace("{cost}", "" + method.getOverallCost());
    write(text);

    List<CostViolation> lines = method.getCostSources();
    Collections.sort(lines, new CostSourceComparator());
    for (CostViolation line : lines.subList(0, min(maxLineCount, lines.size()))) {
      if (line instanceof MethodInvokationCost) {
        write((MethodInvokationCost) line, classFilePath);
      }
    }
    write("</div>");
  }

  /**
   * Generates html code for cost of Class, including the costs of the methods
   * inside the class and the lines in the method
   */
  public void write(ClassCost classCost) {
    String text = "<div class=\"Class\">" + "<span class='expand'>[+]</span>"
      + "{className} {link} [&nbsp;{cost}&nbsp;]";
    String classFilePath = linkGenerator.getOriginalFilePath(classCost.getClassName());
    String link = linkGenerator.buildClassLink(classFilePath, "source");
    text = text.replace("{className}", classCost.getClassName());
    text = text.replace("{cost}", "" + classCost.getOverallCost());
    // Don't show the link if we don't have one
    if (!link.equals("source")) {
      text = text.replace("{link}", "<span class=\"smaller\">(" + link + ")</span>");
    } else {
      text = text.replace("{link}", "");
    }
    write(text);

    List<MethodCost> methods = classCost.getMethods();
    Collections.sort(methods, new MethodCostComparator());

    for (MethodCost method : methods.subList(0, min(maxMethodCount, methods.size()))) {
      write(method, classFilePath);
    }
    write("</div>");
  }

}
