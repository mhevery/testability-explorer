// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import org.kohsuke.args4j.CmdLineParser;

import java.io.PrintStream;


/**
 * Guice module which configures the TE application.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestabilityModule extends AbstractModule {

  @Override
  protected void configure() {
    // For printing errors to the caller
    bind(PrintStream.class).toInstance(System.err);
    bind(CmdLineParser.class).toProvider(CmdLineParserProvider.class).in(Singleton.class);
    bind(CommandLineConfig.class).toInstance(new CommandLineConfig(System.out, System.err));
    bind(Runnable.class).to(JavaTestabilityRunner.class);
    bind(JavaTestabilityConfig.class).toProvider(JavaConfigProvider.class);
  }

  // TODO: make provider method when Guice2 is available
  public static class JavaConfigProvider implements Provider<JavaTestabilityConfig> {
    @Inject CommandLineConfig config;
    public JavaTestabilityConfig get() {
      return config.buildTestabilityConfig();
    }
  }

  // TODO: make provider method when Guice2 is available
  public static class CmdLineParserProvider implements Provider<CmdLineParser> {
    @Inject CommandLineConfig commandLineConfig;
    public CmdLineParser get() {
      return new CmdLineParser(commandLineConfig);
    }
  }
}
