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

import com.google.classpath.ClassPath;
import com.google.test.metric.report.Report;

/**
 * Value object which represents the simple configuration needed to make Testability Explorer reports.
 * 
 * Contrast this class with the CommandLineConfiguration class which takes simple strings (from the
 * command line), and then parses it into this TestabilityConfiguration, for use by the 
 * TestabilityRunner.
 * @author Jonathan Andrew Wolter <jaw@jawspeak.com>
 */
public class TestabilityConfig {

  // TODO I could be persuaded to make all fields public, since this is just a data holder.
  private final List<String> entryList;
  private final ClassPath classPath;
  private final RegExpWhiteList whitelist;
  private final Report report;
  private final PrintStream err;
  private final int printDepth;

  public TestabilityConfig(
      List<String> entryList, 
      ClassPath classPath,
      RegExpWhiteList whitelist, 
      Report report, 
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

  public RegExpWhiteList getWhitelist() {
    return whitelist;
  }

  public Report getReport() {
    return report;
  }

  public PrintStream getErr() {
    return err;
  }

  public int getPrintDepth() {
    return printDepth;
  }
  
}
