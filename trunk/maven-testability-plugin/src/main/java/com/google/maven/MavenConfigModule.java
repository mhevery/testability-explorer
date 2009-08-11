// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.maven;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.ConfigModule.Error;
import com.google.test.metric.ConfigModule.Output;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.JavaTestabilityRunner;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.ReportGeneratorProvider;
import com.google.test.metric.ReportGeneratorProvider.ReportFormat;
import com.google.test.metric.WhiteList;
import com.google.test.metric.report.MultiReportGenerator;
import com.google.test.metric.report.ReportGenerator;
import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.issues.HypotheticalCostModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

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
    bind(ReportOptions.class).toInstance(new ReportOptions(testabilityExplorerMojo.cyclomatic,
        testabilityExplorerMojo.global, testabilityExplorerMojo.maxExcellentCost,
        testabilityExplorerMojo.maxAcceptableCost,
        testabilityExplorerMojo.worstOffenderCount, 0, 0, testabilityExplorerMojo.printDepth,
        testabilityExplorerMojo.minCost, "", ""));
    bind(TestabilityExplorerMojo.class).toInstance(testabilityExplorerMojo);
    bind(WhiteList.class).toInstance(new RegExpWhiteList(testabilityExplorerMojo.whiteList));
    bind(ReportFormat.class).toInstance(ReportFormat.valueOf(testabilityExplorerMojo.format));
    bindConstant().annotatedWith(Names.named("printDepth")).to(testabilityExplorerMojo.printDepth);
    bind(new TypeLiteral<List<String>>() {}).toInstance(Arrays.asList(testabilityExplorerMojo.filter));
    bind(Runnable.class).to(JavaTestabilityRunner.class);
  }

  @Provides ReportGenerator generateHtmlReportAsWellAsRequestedFormat(
      ReportGeneratorProvider requestedReportProvider,
      ClassPath classPath, ReportOptions options,
      HypotheticalCostModel hypotheticalCostModel,
      TestabilityExplorerMojo mojo,
      ReportFormat requestedFormat) {
    if (requestedFormat == ReportFormat.html) {
      return requestedReportProvider.get();
    }
    ReportGenerator htmlReportGenerator =
        new ReportGeneratorProvider(classPath, options, getOutput(mojo, ReportFormat.html),
            hypotheticalCostModel, ReportFormat.html).get();

    return new MultiReportGenerator(htmlReportGenerator, requestedReportProvider.get());
  }

  @Provides ClassRepository getClassRepo(ClassPath classPath) {
    return new JavaClassRepository(classPath);
  }

  @Provides @Output PrintStream getOutput(TestabilityExplorerMojo mojo, ReportFormat format) {
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

  @Provides @Error PrintStream getError(TestabilityExplorerMojo mojo) {
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
