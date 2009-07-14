// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.inject.AbstractModule;
import com.google.inject.BindingAnnotation;
import com.google.test.metric.report.ReportOptions;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.PrintStream;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ConfigModule extends AbstractModule {

  @Retention(RUNTIME) @BindingAnnotation public @interface Error {}
  @Retention(RUNTIME) @BindingAnnotation public @interface Output {}

  private final String[] args;
  private final PrintStream out;
  private final PrintStream err;

  public ConfigModule(String[] args, PrintStream out, PrintStream err) {
    this.args = args;
    this.out = out;
    this.err = err;
  }

  @Override
  protected void configure() {
    // For printing errors to the caller
    bind(PrintStream.class).annotatedWith(Output.class).toInstance(System.out);
    bind(PrintStream.class).annotatedWith(Error.class).toInstance(System.err);
    CommandLineConfig config = new CommandLineConfig(out, err);
    CmdLineParser parser = new CmdLineParser(config);
    // We actually do this work in configure, since we want to bind the parsed command line
    // options at this time
    try {
      parser.parseArgument(args);
      config.validate();
      bind(ClassPath.class).toInstance(new ClassPathFactory().createFromPath(config.cp));
    } catch (CmdLineException e) {
      err.println(e.getMessage() + "\n");
      parser.setUsageWidth(120);
      parser.printUsage(err);
      err.println("Exiting...");
      // TODO(alexeagle): do we actually need to force an exit here? problem will be manifested
      // as a guice error on the missing binding of ClassPath
    }
    bind(CommandLineConfig.class).toInstance(config);
    bind(ReportOptions.class).toInstance(new ReportOptions(
        config.cyclomaticMultiplier, config.globalMultiplier,
        config.maxExcellentCost, config.maxAcceptableCost, config.worstOffenderCount,
        config.maxMethodCount, config.maxLineCount, config.printDepth, config.minCost,
        config.srcFileLineUrl, config.srcFileUrl));
  }
}
