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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.Option;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds fields that Args4J sets by parsing the command line options. After the args are parsed,
 * build a TestabilityConfig.
 *
 * @author Jonathan Andrew Wolter <jaw@jawspeak.com>
 */
public class CommandLineConfig {

  @Option(name = "-cp", usage = "colon delimited classpath to analyze (jars or directories)"
      + "\nEx. lib/one.jar:lib/two.jar")
  protected String cp;

  @Option(name = "-printDepth", usage = "Maximum depth to recurse and print costs of classes/methods "
      + "that the classes under analysis depend on. Defaults to 0.")
  int printDepth = 2;

  @Option(name = "-minCost", usage = "Minimum Total Class cost required to print that class' metrics.")
  int minCost = 1;

  @Option(name = "-maxExcellentCost", usage = "Maximum Total Class cost to be classify it as 'excellent'.")
  int maxExcellentCost = 50;

  @Option(name = "-worstOffenderCount", usage = "Print N number of worst offending classes.")
  public int worstOffenderCount = 20;

  @Option(name = "-maxAcceptableCost", usage = "Maximum Total Class cost to be classify it as 'acceptable'.")
  int maxAcceptableCost = 100;

  @Option(name = "-whitelist", usage = "colon delimited whitelisted packages that will not "
      + "count against you. Matches packages/classes starting with "
      + "given values. (Always whitelists java.*. RegExp OK.)")
  String wl = null;

  @Option(name = "-print", usage = "summary: (default) print package summary information.\n"
      + "html: print package summary information in html format.\n"
      + "source: write out annotated source into directory.\n"
      + "detail: print detail drill down information for each method call. (DEPRECATED)\n"
      + "xml: print computer readable XML format.")
  String printer = "summary";

  @Option(name = "-srcFileLineUrl", usage = "template for urls linking to a specific LINE in a file in a web "
      + "source code repository.\n"
      + "Ex. -srcFileLineUrl http://code.repository/basepath/{path}&line={line}\n"
      + "Ex. -srcFileLineUrl http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}#{line}")
  String srcFileLineUrl = "";

  @Option(name = "-srcFileUrl", usage = "template for urls linking to a file in a web source code "
      + "repository.\nEx. -srcFileUrl http://code.repository/basepath/{path}\n"
      + "Ex. -srcFileUrl http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}")
  String srcFileUrl = "";

  // TODO(alexeagle): this parameter is no longer used
  @Option(name = "-maxMethodCount", usage = "max number of methods to print in html summary")
  int maxMethodCount = 10;

  @Option(name = "-maxLineCount", usage = "max number of lines in method to print in html summary")
  int maxLineCount = 10;

  @Option(name = "-cyclomatic", metaVar = "cyclomatic cost multiplier", usage = "When computing the overall cost of the method the "
      + "individual costs are added using weighted average. "
      + "This represents the weight of the cyclomatic cost.")
  double cyclomaticMultiplier = 1;

  @Option(name = "-global", metaVar = "global state cost multiplier", usage = "When computing the overall cost of the method the "
      + "individual costs are added using weighted average. "
      + "This represents the weight of the global state cost.")
  double globalMultiplier = 10;

  @Option(name = "-constructor", metaVar = "constructor does work cost multiplier", usage = "When computing the overall cost of the method the "
      + "individual costs are added using weighted average. "
      + "This represents the weight of the constructor does work cost.")
  double constructorDoesWorkMultiplier = 10;

  @Argument(metaVar = "classes and packages to analyze", usage = "Classes or packages to analyze. "
      + "Matches any class starting with these.\n"
      + "Ex. com.example.analyze.these com.google.and.these.packages " + "com.google.AClass")
  List<String> entryList = new ArrayList<String>();

  PrintStream out;
  PrintStream err;

  public CommandLineConfig(PrintStream out, PrintStream err) {
    this.out = out;
    this.err = err;
  }

  public JavaTestabilityConfig buildTestabilityConfig() throws CmdLineException {
    // This responsibility might belong in a new class.
    cp = (cp != null ? cp : System.getProperty("java.class.path", "."));
    if (entryList.isEmpty()) {
      entryList.add(".");
    }
    return new JavaTestabilityConfig(this);
  }

  public void validate() throws CmdLineException {
    if (cp == null && entryList.isEmpty()) {
      throw new CmdLineException("You must supply either the -cp flag, " +
          "or the argument \"classes and packages to analyze\".");
    }
  }
}
