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

import java.io.PrintStream;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.test.metric.ReportGeneratorBuilder.ReportFormat;
import com.google.test.metric.report.ReportGenerator;
import com.google.test.metric.report.ReportOptions;

/**
 * Value object which represents the simple configuration needed to make Testability Explorer reports.
 * 
 * Contrast this class with the CommandLineConfiguration class which takes simple strings (from the
 * command line), and then parses it into this TestabilityConfiguration, for use by the 
 * TestabilityRunner.
 * @author Jonathan Andrew Wolter <jaw@jawspeak.com>
 */
public class JavaTestabilityConfig {

  // TODO I could be persuaded to make all fields public, since this is just a data holder.
  private final List<String> entryList;
  private final ClassPath classPath;
  private final WhiteList whitelist;
  private final ReportGenerator report;
  private final PrintStream err;
  private final int printDepth;

  public JavaTestabilityConfig(CommandLineConfig config) throws CmdLineException {
    entryList = config.entryList;
    classPath = new ClassPathFactory().createFromPath(config.cp);
    err = config.err;
    printDepth = config.printDepth;
    convertEntryListValues();
    ReportOptions options = new ReportOptions(config.cyclomaticMultiplier,
        config.globalMultiplier, config.maxExcellentCost,
        config.maxAcceptableCost, config.worstOffenderCount,
        config.maxMethodCount, config.maxLineCount, config.printDepth,
        config.minCost, config.srcFileLineUrl, config.srcFileUrl);
    ReportFormat format;
    try {
      format = ReportFormat.valueOf(config.printer);
    } catch (Exception e) {
      throw new CmdLineException("Don't understand '-print' option '" + config.printer + "'");
    }
    report = new ReportGeneratorBuilder(classPath, options, format, config.out, entryList).build();
    RegExpWhiteList regExpWhitelist = new RegExpWhiteList("java.");
    for (String packageName : config.wl == null ? new String[] {} : config.wl.split("[,:]")) {
      regExpWhitelist.addPackage(packageName);
    }
    whitelist = regExpWhitelist;
  }

  public JavaTestabilityConfig(
      List<String> entryList, 
      ClassPath classPath,
      WhiteList whitelist,
      ReportGenerator report, 
      PrintStream err, 
      int printDepth) {
        this.entryList = entryList;
        this.classPath = classPath;
        this.whitelist = whitelist;
        this.report = report;
        this.err = err;
        this.printDepth = printDepth;
  }

  public List<String> getEntryList() {
    return entryList;
  }

  public ClassPath getClassPath() {
    return classPath;
  }

  public WhiteList getWhitelist() {
    return whitelist;
  }

  public ReportGenerator getReport() {
    return report;
  }

  public PrintStream getErr() {
    return err;
  }

  public int getPrintDepth() {
    return printDepth;
  }
  
  /*
   * Converts entryList class and package values into paths by replacing any "." with a "/" Also
   * checks for entries that contain multiple values separated by a space (" "), and splits them out
   * into separate entries. This may happen when main() is called explicitly from another class,
   * such as the {@link com.google.ant.TestabilityTask}
   */
  // TODO(jwolter) can we remove this now that there is a convenient programmatic API.
  private void convertEntryListValues() {
    for (int i = 0; i < entryList.size(); i++) {
      if (entryList.get(i).contains(" ")) {
        String[] entries = entryList.get(i).split(" ");
        entryList.set(i, entries[0].replaceAll("/", "."));
        for (int j = 1; j < entries.length; j++) {
          entryList.add(entries[j].replaceAll("/", "."));
        }
      } else {
        entryList.set(i, entryList.get(i).replaceAll("/", "."));
      }
    }
  }
}
