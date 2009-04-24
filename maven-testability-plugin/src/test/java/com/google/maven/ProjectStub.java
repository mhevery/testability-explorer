package com.google.maven;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.model.Build;

import java.util.List;
import java.util.Arrays;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ProjectStub extends MavenProjectStub {
  @Override
  public String getPackaging() {
    return "jar";
  }

  @Override
  public Build getBuild() {
    return new Build() {
      @Override
      public String getOutputDirectory() {
        return "target/classes";
      }
    };
  }
}
