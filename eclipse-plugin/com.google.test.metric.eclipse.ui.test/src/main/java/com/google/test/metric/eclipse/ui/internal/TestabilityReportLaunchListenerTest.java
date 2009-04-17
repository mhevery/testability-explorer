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
package com.google.test.metric.eclipse.ui.internal;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests for {@link TestabilityReportLaunchListener}.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityReportLaunchListenerTest extends TestCase {
  public void testGetNicePath() throws Exception {
    TestabilityReportLaunchListener listener = new TestabilityReportLaunchListener();
    Set<String> sourceFolders = new HashSet<String>();
    sourceFolders.add("Project/src/");
    sourceFolders.add("Project/src_0/");
    String pathString = "Project/src/com/google/test/MyClass";
    assertEquals("com/google/test/MyClass", listener.getNicePath(pathString, sourceFolders));
  }
  
  public void testGetNicePathNotMatching() throws Exception {
    TestabilityReportLaunchListener listener = new TestabilityReportLaunchListener();
    Set<String> sourceFolders = new HashSet<String>();
    sourceFolders.add("Project/src/");
    String pathString = "Project/src_0/com/google/test/MyClass";
    assertEquals(pathString, listener.getNicePath(pathString, sourceFolders));
  }
  
  public void testGetNicePathWithDotJava() throws Exception {
    TestabilityReportLaunchListener listener = new TestabilityReportLaunchListener();
    Set<String> sourceFolders = new HashSet<String>();
    sourceFolders.add("Project/src/");
    sourceFolders.add("Project/src_0/");
    String pathString = "Project/src/com/google/test/MyClass.java";
    assertEquals("com/google/test/MyClass", listener.getNicePath(pathString, sourceFolders));
  }
  
  public void testGetSourceFolders() throws Exception {
    TestabilityReportLaunchListener listener = new TestabilityReportLaunchListener();
    IJavaProject project = EasyMock.createMock(IJavaProject.class);
    IPackageFragmentRoot normalRoot = EasyMock.createMock(IPackageFragmentRoot.class);
    EasyMock.expect(normalRoot.isArchive()).andReturn(false);
    IResource resource = EasyMock.createMock(IResource.class);
    EasyMock.expect(normalRoot.getCorrespondingResource()).andReturn(resource);
    String fullPath = "src_0";
    EasyMock.expect(resource.getFullPath()).andReturn(new Path(fullPath));
    
    IPackageFragmentRoot archiveRoot = EasyMock.createMock(IPackageFragmentRoot.class);
    EasyMock.expect(archiveRoot.isArchive()).andReturn(true);
    EasyMock.expect(project.getPackageFragmentRoots())
        .andReturn(new IPackageFragmentRoot[]{normalRoot, archiveRoot});

    EasyMock.replay(project, normalRoot, archiveRoot, resource);

    Set<String> sourceFolders = listener.getSourceFolders(project);
    assertEquals(1, sourceFolders.size());
    assertEquals(fullPath + System.getProperty("file.separator"), sourceFolders.iterator().next());
    EasyMock.verify(project, normalRoot, archiveRoot, resource);
  }
}
