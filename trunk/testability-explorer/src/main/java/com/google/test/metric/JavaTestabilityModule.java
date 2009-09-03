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

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.test.metric.ReportGeneratorProvider.ReportFormat;

import java.io.PrintStream;
import java.util.List;

/**
 * Module which provides the simple configuration needed to make Testability Explorer reports.
 * 
 * Contrast this class with the CommandLineConfiguration class which takes simple strings (from the
 * command line), and then parses it into this TestabilityConfiguration, for use by the 
 * TestabilityRunner.
 * @author Jonathan Andrew Wolter <jaw@jawspeak.com>
 */
public class JavaTestabilityModule extends AbstractModule {

  private final List<String> entryList;
  private final PrintStream err;
  private final int printDepth;
  private final ReportFormat format;

  @Inject
  public JavaTestabilityModule(CommandLineConfig config) {
    entryList = config.entryList;
    err = config.err;
    format = config.format;
    printDepth = config.printDepth;
    convertEntryListValues();
  }

  public JavaTestabilityModule(List<String> entryList,
                               PrintStream err, int printDepth, ReportFormat format) {
    this.entryList = entryList;
    this.err = err;
    this.printDepth = printDepth;
    this.format = format;
  }

  public List<String> getEntryList() {
    return entryList;
  }

  public PrintStream getErr() {
    return err;
  }

  public int getPrintDepth() {
    return printDepth;
  }

  public ReportFormat getFormat() {
    return format;
  }

  @Override
  protected void configure() {
  }

  @Provides WhiteList getWhiteList(CommandLineConfig config) {
    RegExpWhiteList regExpWhitelist = new RegExpWhiteList("java.");
    regExpWhitelist.addPackage("javax.");
    for (String packageName : config.wl == null ? new String[] {} : config.wl.split("[,:]")) {
      regExpWhitelist.addPackage(packageName);
    }
    return regExpWhitelist;
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
