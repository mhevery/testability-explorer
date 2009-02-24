package com.google.maven;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;
import org.apache.maven.artifact.DependencyResolutionRequiredException;

import java.util.List;
import java.util.Arrays;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ProjectStub extends MavenProjectStub {
  @Override
  public List getRuntimeClasspathElements() throws DependencyResolutionRequiredException {
    return Arrays.asList("target/classes");
  }
}
