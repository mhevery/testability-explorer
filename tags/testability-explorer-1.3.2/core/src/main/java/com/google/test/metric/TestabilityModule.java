// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric;

import com.google.classpath.ClassPath;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.test.metric.report.ReportGenerator;


/**
 * Guice module which configures the TE application.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestabilityModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(Runnable.class).to(JavaTestabilityRunner.class);
    bind(ReportGenerator.class).toProvider(ReportGeneratorProvider.class);
  }

  @Provides ClassRepository getClassRepo(ClassPath classPath) {
    return new JavaClassRepository(classPath);
  }
}
