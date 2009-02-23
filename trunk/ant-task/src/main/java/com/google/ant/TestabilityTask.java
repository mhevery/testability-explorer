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

package com.google.ant;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import com.google.test.metric.*;
import com.google.test.metric.ReportPrinterBuilder.ReportFormat;
import com.google.test.metric.report.Report;
import com.google.test.metric.report.ReportOptions;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

public class TestabilityTask extends Task {

  private final TaskModel model = new TaskModel();

  public void setCyclomatic(int cost) {
    model.setCyclomatic(cost);
  }

  public void setGlobal(int global) {
    model.setGlobal(global);
  }

  public void setMaxAcceptableCost(int cost) {
    model.setMaxAcceptableCost(cost);
  }

  public void setMaxExcellentCost(int cost) {
    model.setMaxExcellentCost(cost);
  }

  public void setMinCost(int cost) {
    model.setMinCost(cost);
  }


  public void setPrint(String printVal) {
    model.setPrint(printVal);
  }

  public void setPrintDepth(int depth) {
    model.setPrintDepth(depth);
  }

  public void setWhiteList(String whiteListVal) {
    model.setWhiteList(whiteListVal);
  }

  public void setWorstOffenderCount(int count) {
    model.setWorstOffenderCount(count);
  }

  public void setResultFile(String resultFile) {
    model.setResultFile(resultFile);
  }

  public void setErrorFile(String errorFile) {
    model.setErrorFile(errorFile);
  }

  public void setFilter(String filter) {
    model.setFilter(filter);
  }

  public void setFailProperty(String property) {
    model.setFailProperty(property);
  }

  public void addClasspath(Path p) {
    model.addClasspath(p);
  }

  @Override
  public void execute() throws BuildException {
    List<String> validationMessages = new ArrayList<String>();
    boolean allOk = model.validate(validationMessages);

    if (allOk) {
      log("INFO: Testability Starting ...", Project.MSG_VERBOSE);
      logValidationMessages(validationMessages);
      logParameters();

      runTestabilityExplorer();

      log("INFO: Testability Done", Project.MSG_VERBOSE);
    }
    else {
      logValidationMessages(validationMessages);
      failBuild(validationMessages);
    }

    checkResultsAreOk();
  }

  private void runTestabilityExplorer() {
    List<String> entries = Arrays.asList(model.getFilter());
    WhiteList packageWhiteList = new RegExpWhiteList(model.getWhiteList());
    ReportOptions options = setOptions();
    ClassPath classPath = new ClassPathFactory().createFromPath(model.getClassPath());
    Report report = new ReportPrinterBuilder(classPath, options,
        ReportFormat.valueOf(model.getPrint()), model.getResultPrintStream(), entries).build();
    TestabilityConfig config = new TestabilityConfig(entries, classPath, packageWhiteList,
        report, model.getErrorPrintStream(), model.getPrintDepth());
    new TestabilityRunner(config).run();
  }

  private ReportOptions setOptions() {
    ReportOptions options = new ReportOptions();
    options.setPrintDepth(model.getPrintDepth());
    options.setMinCost(model.getMinCost());
    options.setMaxAcceptableCost(model.getMaxAcceptableCost());
    options.setMaxExcellentCost(model.getMaxExcellentCost());
    options.setCyclomaticMultiplier(model.getCyclomatic());
    options.setGlobalMultiplier(model.getGlobal());
    options.setWorstOffenderCount(model.getWorstOffenderCount());
    return options;
  }

  private void logParameters() {
    log("-cyclomatic"+ " " +  Integer.toString(model.getCyclomatic()), Project.MSG_VERBOSE);
    log("-global"+ " " +  Integer.toString(model.getGlobal()), Project.MSG_VERBOSE);
    log("-filter" + " " + model.getFilter(), Project.MSG_VERBOSE);
    log("-cp"+ " " +  model.getClassPath(), Project.MSG_VERBOSE);
    log("-printDepth"+ " " +   Integer.toString(model.getPrintDepth()), Project.MSG_VERBOSE);
    log("-minCost"+ " " +  Integer.toString(model.getMinCost()), Project.MSG_VERBOSE);
    log("-maxExcellentCost"+ " " +  Integer.toString(model.getMaxExcellentCost()), Project.MSG_VERBOSE);
    log("-maxAcceptableCost"+ " " +  Integer.toString(model.getMaxAcceptableCost()), Project.MSG_VERBOSE);
    log("-worstOffenderCount"+ " " +  Integer.toString(model.getWorstOffenderCount()), Project.MSG_VERBOSE);
    log("-whitelist"+ " " +  model.getWhiteList(), Project.MSG_VERBOSE);
    log("-print"+ " " +  model.getPrint(), Project.MSG_VERBOSE);
  }

  private void checkResultsAreOk() {
//        if (false) {
//            failBuild("The avg of violations per ncss is to high - <failvalue>: " + 0);
//        }
  }

  private void failBuild(List<String> messages) {

    if (model.isFailPropertySet())
    {
      failBySettingFailProperty();
    }
    else
    {
      failByDying(messages);
    }
  }

  private void failByDying(List<String> messages) {
    String flattened = "";

    for (String message : messages) {
      flattened += message + ". ";
    }

    throw new BuildException(flattened);
  }

  private void failBySettingFailProperty() {
    getProject().setNewProperty(model.getFailProperty(), "true");
  }

  private void logValidationMessages(List<String> messages) {
    for (String message: messages) {
      log("CAUTION: " + message, Project.MSG_VERBOSE);
    }
  }
}
