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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ReportDiffer {

  @Argument(metaVar="oldFile", index=0, usage="name of the old file", required=true)
  private String oldFile;

  @Argument(metaVar="newFile", index=1, usage="name of the new file", required=true)
  private String newFile;

  public static void main(String[] args) throws FileNotFoundException, CmdLineException {
    ReportDiffer differ = new ReportDiffer();
    differ.parseArgs(args);
    differ.doDiff();
  }

  private void doDiff() throws FileNotFoundException {
    // Currently PropertiesReportDiffer is hardcoded, as it is the only one supported
    Diff diff = new PropertiesReportDiffer(new FileInputStream(oldFile),
        new FileInputStream(newFile)).diff();
    diff.print(System.out);
  }

  private void parseArgs(String[] args) throws CmdLineException {
    CmdLineParser parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage() + "\n");
      parser.setUsageWidth(120);
      parser.printUsage(System.err);
      throw new CmdLineException("Exiting...");
    }
  }
}
