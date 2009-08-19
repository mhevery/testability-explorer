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

import java.io.FileReader;
import java.io.FileWriter;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.w3c.dom.Document;

import freemarker.template.Configuration;

public class ReportDiffer {

  @Option(name="-oldFile", usage="name of the old XML report", required=true)
  private String oldFile;

  @Option(name="-newFile", usage="name of the new XML report", required=true)
  private String newFile;

  @Option(name="-htmlReportFile", usage="name of the HTML result file", required=true)
  private String htmlReportFile;

  @Option(name="-oldLinkUrl", usage="a URL, to be used as a link on 'old' numbers (optional)\n" +
      "  may link to anything, ie. source code, the annotated testability report\n" +
      "  for example: http://myhost.com/oldReport/{path}.html", required=false)
  private String oldLinkUrl;

  @Option(name="-newLinkUrl", usage="a URL, to be used as a link on 'new' numbers (optional)\n" +
      "  may link to anything, ie. source code, the annotated testability report\n" +
      "  for example: http://myhost.com/newReport/{path}.html", required=false)
  private String newLinkUrl;


  public static void main(String[] args) throws Exception {
    ReportDiffer differ = new ReportDiffer();
    differ.parseArgs(args);
    differ.doDiff(new DiffReportFactory());
  }

  private void doDiff(DiffReportFactory diffReportFactory) throws Exception {
    XMLReportLoader reportLoader = new XMLReportLoader();
    Document oldReport = reportLoader.loadXML(new FileReader(oldFile));
    Document newReport = reportLoader.loadXML(new FileReader(newFile));
    FileWriter out = new FileWriter(htmlReportFile);
    Diff diff = new XMLReportDiffer().diff(oldReport, newReport);
    DiffReport report = diffReportFactory.buildReport(diff);
    if (oldLinkUrl != null && !oldLinkUrl.equals("")) {
      report.setOldSourceUrl(oldLinkUrl);
    }
    if (newLinkUrl != null && !newLinkUrl.equals("")) {
      report.setOldSourceUrl(newLinkUrl);
    }
    report.writeHtml(out);
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

  private static class DiffReportFactory {
    public DiffReport buildReport(Diff diff) {
      return new DiffReport(diff, new Configuration());
    }
  }
}
