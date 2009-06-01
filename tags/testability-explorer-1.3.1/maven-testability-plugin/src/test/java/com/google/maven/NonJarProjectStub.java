package com.google.maven;

import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class NonJarProjectStub extends MavenProjectStub {
  @Override
  public String getPackaging() {
    return "pom";
  }
}
