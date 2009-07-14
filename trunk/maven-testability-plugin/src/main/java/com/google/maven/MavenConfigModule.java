// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.maven;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.test.metric.ConfigModule.Error;
import com.google.test.metric.ConfigModule.Output;
import com.google.test.metric.JavaTestabilityConfig;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.ReportGeneratorProvider.ReportFormat;
import com.google.test.metric.report.ReportOptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class MavenConfigModule extends AbstractModule {

  private TestabilityExplorerMojo testabilityExplorerMojo;

  public MavenConfigModule(TestabilityExplorerMojo testabilityExplorerMojo) {
    this.testabilityExplorerMojo = testabilityExplorerMojo;
  }

  @Override
  protected void configure() {

    bind(ClassPath.class).toInstance(new ClassPathFactory().createFromPath(
        testabilityExplorerMojo.mavenProject.getBuild().getOutputDirectory()));
    // TODO: also want to generate one with ReportFormat.html
    // output the reports using getResultPrintStream()
    bind(ReportOptions.class).toInstance(new ReportOptions(testabilityExplorerMojo.cyclomatic,
        testabilityExplorerMojo.global, testabilityExplorerMojo.maxExcellentCost,
        testabilityExplorerMojo.maxAcceptableCost,
        testabilityExplorerMojo.worstOffenderCount, 0, 0, testabilityExplorerMojo.printDepth,
        testabilityExplorerMojo.minCost, "", ""));
    bind(PrintStream.class).annotatedWith(Output.class).toProvider(OutputProvider.class);
    bind(PrintStream.class).annotatedWith(Error.class).toProvider(ErrorProvider.class);
    bind(JavaTestabilityConfig.class).toProvider(ConfigProvider.class);
    bind(TestabilityExplorerMojo.class).toInstance(testabilityExplorerMojo);
  }

  public static class OutputProvider implements Provider<PrintStream> {
    @Inject TestabilityExplorerMojo mojo;
    @Inject JavaTestabilityConfig config;
    public PrintStream get() {
      ReportFormat format = config.getFormat();
      File directory = (format == ReportFormat.html ? mojo.outputDirectory : mojo.targetDirectory);
      if (!directory.exists()) {
        directory.mkdirs();
      }
      try {
        String outFile = mojo.resultfile + "." + format.toString();
        return new PrintStream(new FileOutputStream(new File(directory, outFile)));
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public static class ErrorProvider implements Provider<PrintStream> {
    @Inject TestabilityExplorerMojo mojo;
    public PrintStream get() {
      if (mojo.errorfile != null && mojo.errorfile.exists()) {
        try {
          return new PrintStream(new FileOutputStream(mojo.errorfile));
        } catch (FileNotFoundException e) {
          throw new RuntimeException(e);
        }
      }
      return System.err;
    }
  }

  public static class ConfigProvider implements Provider<JavaTestabilityConfig> {
    @Inject TestabilityExplorerMojo mojo;
    @Inject @Error PrintStream err;
    public JavaTestabilityConfig get() {
      return new JavaTestabilityConfig(Arrays.asList(mojo.filter),
          new RegExpWhiteList(mojo.whiteList),
          err, mojo.printDepth, ReportFormat.valueOf(mojo.format));
    }
  }
}
