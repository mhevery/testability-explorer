package com.google.maven;

import org.apache.maven.model.Build;
import org.apache.maven.plugin.testing.stubs.MavenProjectStub;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ProjectStub extends MavenProjectStub {
	
  Build build = new Build() {
    @Override
    public String getOutputDirectory() {
      return "target/classes";
    }
  };
	
  public ProjectStub() {
    //We need some compile dependencies to calculate the classpath	
    addCompileSourceRoot(getBuild().getOutputDirectory());
  }
  @Override
  public String getPackaging() {
    return "jar";
  }

  @Override
  public Build getBuild() {
    return this.build;
  }
}
