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
package com.google.test.metric;

/**
 * Has the responsibility of acting as the static main() entrypoint to the application.
 * Delegates to CommandLineConfig for available command line options to parse.
 * Delegates to Args4J for the responsibility of parsing options.
 * Delegates to TestabilityRunner to actually run the analysis.
 */
import java.io.PrintStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Testability {
  private final PrintStream err;
  private final CommandLineConfig commandLineConfig;

  public Testability(PrintStream err, CommandLineConfig commandLineConfig) {
    this.err = err;
    this.commandLineConfig = commandLineConfig;
  }

  public static void main(String... args) {
    main(System.err, new CommandLineConfig(System.out, System.err), args);
  }

  public static void main(PrintStream err, CommandLineConfig commandLineConfig, String... args) {
    new Testability(err, commandLineConfig).run(args);
  }

  public void run(String... args) {
    CmdLineParser parser = new CmdLineParser(commandLineConfig);
    try {
      parser.parseArgument(args);
      commandLineConfig.validate();
      new TestabilityRunner(commandLineConfig.buildTestabilityConfig()).run();
    } catch (CmdLineException e) {
      err.println(e.getMessage() + "\n");
      parser.setUsageWidth(120);
      parser.printUsage(err);
      err.println("Exiting...");
    }
  }

}
