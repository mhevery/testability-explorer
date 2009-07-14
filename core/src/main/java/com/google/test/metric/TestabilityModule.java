// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric;

import com.google.classpath.ClassPath;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provider;
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
    bind(ClassRepository.class).toProvider(JavaClassRepositoryProvider.class);
  }

  public static class JavaClassRepositoryProvider implements Provider<ClassRepository> {
    @Inject ClassPath classPath;
    public ClassRepository get() {
      return new JavaClassRepository(classPath);
    }
  }
}
