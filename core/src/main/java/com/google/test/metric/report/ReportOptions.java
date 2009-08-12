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

/**
 * The various options that affect the reporting of metrics. Just a big JavaBean.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ReportOptions {
  private double cyclomaticMultiplier;
  private double globalMultiplier;
  private double constructorMultiplier;
  private int maxExcellentCost;
  private int maxAcceptableCost;
  private int worstOffenderCount;
  private int maxMethodCount;
  private int maxLineCount;
  private int printDepth;
  private int minCost;
  private String srcFileLineUrl;
  private String srcFileUrl;

  public ReportOptions(double cyclomaticMultiplier, double globalMultiplier,
                       double constructorMultiplier, int maxExcellentCost,
                       int maxAcceptableCost, int worstOffenderCount, int maxMethodCount,
                       int maxLineCount, int printDepth, int minCost, String srcFileLineUrl,
                       String srcFileUrl) {
    this.cyclomaticMultiplier = cyclomaticMultiplier;
    this.globalMultiplier = globalMultiplier;
    this.constructorMultiplier = constructorMultiplier;
    this.maxExcellentCost = maxExcellentCost;
    this.maxAcceptableCost = maxAcceptableCost;
    this.worstOffenderCount = worstOffenderCount;
    this.maxMethodCount = maxMethodCount;
    this.maxLineCount = maxLineCount;
    this.printDepth = printDepth;
    this.minCost = minCost;
    this.srcFileLineUrl = srcFileLineUrl;
    this.srcFileUrl = srcFileUrl;
  }

  public double getCyclomaticMultiplier() {
    return cyclomaticMultiplier;
  }

  public void setCyclomaticMultiplier(double cyclomaticMultiplier) {
    this.cyclomaticMultiplier = cyclomaticMultiplier;
  }

  public double getGlobalMultiplier() {
    return globalMultiplier;
  }

  public void setGlobalMultiplier(double globalMultiplier) {
    this.globalMultiplier = globalMultiplier;
  }

  public int getMaxExcellentCost() {
    return maxExcellentCost;
  }

  public void setMaxExcellentCost(int maxExcellentCost) {
    this.maxExcellentCost = maxExcellentCost;
  }

  public int getMaxAcceptableCost() {
    return maxAcceptableCost;
  }

  public void setMaxAcceptableCost(int maxAcceptableCost) {
    this.maxAcceptableCost = maxAcceptableCost;
  }

  public int getWorstOffenderCount() {
    return worstOffenderCount;
  }

  public void setWorstOffenderCount(int worstOffenderCount) {
    this.worstOffenderCount = worstOffenderCount;
  }

  public int getMaxMethodCount() {
    return maxMethodCount;
  }

  public void setMaxMethodCount(int maxMethodCount) {
    this.maxMethodCount = maxMethodCount;
  }

  public int getMaxLineCount() {
    return maxLineCount;
  }

  public void setMaxLineCount(int maxLineCount) {
    this.maxLineCount = maxLineCount;
  }

  public int getPrintDepth() {
    return printDepth;
  }

  public void setPrintDepth(int printDepth) {
    this.printDepth = printDepth;
  }

  public int getMinCost() {
    return minCost;
  }

  public void setMinCost(int minCost) {
    this.minCost = minCost;
  }

  public String getSrcFileLineUrl() {
    return srcFileLineUrl;
  }

  public void setSrcFileLineUrl(String srcFileLineUrl) {
    this.srcFileLineUrl = srcFileLineUrl;
  }

  public String getSrcFileUrl() {
    return srcFileUrl;
  }

  public void setSrcFileUrl(String srcFileUrl) {
    this.srcFileUrl = srcFileUrl;
  }

  public double getConstructorMultiplier() {
    return constructorMultiplier;
  }
}
