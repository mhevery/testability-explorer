/*
 * Copyright 2009 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.test.metric.eclipse.internal.core;

import static org.easymock.EasyMock.expect;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;

/**
 * @author klundberg@google.com (Karin Lundberg)
 * 
 */
public class TestabilityLauncherTest extends TestCase {
  private TestabilityLauncher launcher;
  private IJavaProject javaProject;
  private IMocksControl control;

  @Override
  protected void setUp() throws Exception {
    launcher = new TestabilityLauncher();
    control = EasyMock.createControl();
    javaProject = control.createMock(IJavaProject.class);
  }

  public void testGetClassPathsReturnsDefaultOutputPath() throws Exception {
    expect(javaProject.getRawClasspath()).andReturn(new IClasspathEntry[0]);
    expect(javaProject.getOutputLocation()).andReturn(new Path("/expected"));
    control.replay();
    String[] results = launcher.getClassPaths(javaProject, "whatever");
    assertEquals(1, results.length);
    assertEquals("whatever/expected", results[0]);
    control.verify();
  }
  
  public void testGetClassPathsReturnsAbsoluePathWhenOutputPathNull() throws Exception {
    IClasspathEntry[] entries = new IClasspathEntry[1];
    entries[0] = control.createMock(IClasspathEntry.class);
    expect(entries[0].getOutputLocation()).andReturn(null);
    expect(entries[0].getPath()).andReturn(new Path("/expectedpath"));
    expect(javaProject.getRawClasspath()).andReturn(entries);
    expect(javaProject.getOutputLocation()).andReturn(new Path("/expected2"));
    control.replay();
    String projectLocation = "location";
    
    String[] results = launcher.getClassPaths(javaProject, projectLocation);
    
    assertEquals(2, results.length);
    assertEquals(projectLocation + "/expectedpath", results[0]);
    assertEquals(projectLocation + "/expected2", results[1]);
    control.verify();
  }

  public void testGetClassPathsReturnsAbsoluePathWhenOutputPathNotNull() throws Exception {
    IClasspathEntry[] entries = new IClasspathEntry[1];
    entries[0] = control.createMock(IClasspathEntry.class);
    expect(entries[0].getOutputLocation()).andReturn(new Path("/expected"));
    expect(javaProject.getRawClasspath()).andReturn(entries);
    expect(javaProject.getOutputLocation()).andReturn(new Path("/expected2"));
    control.replay();
    String projectLocation = "location";
    
    String[] results = launcher.getClassPaths(javaProject, projectLocation);
    
    assertEquals(2, results.length);
    assertEquals(projectLocation + "/expected", results[0]);
    assertEquals(projectLocation + "/expected2", results[1]);
    control.verify();
  }
}
