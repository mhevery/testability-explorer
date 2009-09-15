/*
 * Copyright 2009 Google Inc.
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

import com.google.classpath.ClassPath;
import com.google.classpath.RegExpResourceFilter;
import static com.google.classpath.RegExpResourceFilter.ANY;
import static com.google.classpath.RegExpResourceFilter.ENDS_WITH_CLASS;
import com.google.inject.Inject;
import com.google.test.metric.ConfigModule.Error;
import com.google.test.metric.report.ReportGenerator;
import com.google.test.metric.report.issues.IssuesReporter;

import java.io.IOException;
import java.io.PrintStream;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Has the responsibility of kicking off the analysis. A programmatic interface into using
 * Testability Explorer.
 *
 * @author Jonathan Andrew Wolter <jaw@jawspeak.com>
 */
public class JavaTestabilityRunner implements Runnable {

  private final ReportGenerator report;
  private final ClassPath classPath;
  private final ClassRepository classRepository;
  private final MetricComputer computer;
  private final List<String> entryList;
  private final WhiteList whiteList;
  private final PrintStream err;

  @Inject
  public JavaTestabilityRunner(ReportGenerator report,
                               ClassPath classPath, ClassRepository classRepository,
                               MetricComputer computer, List<String> entryList, 
                               WhiteList whiteList, @Error PrintStream err) {
    this.report = report;
    this.classPath = classPath;
    this.classRepository = classRepository;
    this.computer = computer;
    this.entryList = entryList;
    this.whiteList = whiteList;
    this.err = err;
  }

  public AnalysisModel generateModel(IssuesReporter issuesReporter) {
    SortedSet<String> classNames = new TreeSet<String>();
    RegExpResourceFilter resourceFilter = new RegExpResourceFilter(ANY, ENDS_WITH_CLASS);
    AnalysisModel model = new AnalysisModel(issuesReporter);
    for (String entry : entryList) {
      if (entry.equals(".")) {
        entry = "";
      }
      // TODO(jonathan) seems too complicated, replacing "." with "/" using the resource filter, then right below replace all "/" with "."
      classNames.addAll(asList(classPath.findResources(entry.replace(".", "/"), resourceFilter)));
    }
    for (String resource : classNames) {
      String className = resource.replace(".class", "").replace("/", ".").replace('$', '.');
      try {
        if (!whiteList.isClassWhiteListed(className)) {
          ClassInfo clazz = classRepository.getClass(className);
          ClassCost classCost = computer.compute(clazz);
          model.addClassCost(classCost);
        }
      } catch (ClassNotFoundException e) {
        err.println("WARNING: can not analyze class '" + className
            + "' since class '" + e.getClassName() + "' was not found. Chain: " + e.getMessage());
      }
    }

    return model;
  }

  public void renderReport(AnalysisModel model) {
    try {
      report.printHeader();

      for (ClassCost classCost : model.getClassCosts()) {
        report.addClassCost(classCost);
      }

      report.printFooter();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void run() {
    renderReport(generateModel(null));
  }

}
