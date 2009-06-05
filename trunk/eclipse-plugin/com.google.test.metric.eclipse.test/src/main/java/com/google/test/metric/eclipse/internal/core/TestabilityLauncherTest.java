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

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IBuffer;
import org.eclipse.jdt.core.IClasspathAttribute;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IRegion;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.eval.IEvaluationContext;

import java.util.Map;

/**
 * @author klundberg@google.com (Karin Lundberg)
 * 
 */
public class TestabilityLauncherTest extends TestCase {
  private TestabilityLauncher launcher;
  private TestableJavaProject javaProject;

  @Override
  protected void setUp() throws Exception {
    launcher = new TestabilityLauncher();
    javaProject = new TestableJavaProject();
  }

  public void testGetClassPathsReturnsDefaultOutputPath() throws Exception {
    javaProject.classpathEntries = new TestableClassPathEntry[0];
    javaProject.outputLocation = "/expected";
    
    String[] results = launcher.getClassPaths(javaProject, "whatever");
    assertEquals(1, results.length);
    assertEquals("whatever/expected", results[0]);
  }
  
  public void testGetClassPathsReturnsAbsoluePathWhenOutputPathNull() throws Exception {
    javaProject.classpathEntries = new TestableClassPathEntry[1];
    javaProject.classpathEntries[0] = new TestableClassPathEntry();
    javaProject.classpathEntries[0].outputLocation = null;
    javaProject.classpathEntries[0].path = "/expectedpath";
    
    javaProject.outputLocation = "/expected2";
    String projectLocation = "location";
    
    String[] results = launcher.getClassPaths(javaProject, projectLocation);
    
    assertEquals(2, results.length);
    assertEquals(projectLocation + "/expectedpath", results[0]);
    assertEquals(projectLocation + "/expected2", results[1]);
  }

  public void testGetClassPathsReturnsAbsoluePathWhenOutputPathNotNull() throws Exception {
    javaProject.classpathEntries = new TestableClassPathEntry[1];
    javaProject.classpathEntries[0] = new TestableClassPathEntry();
    javaProject.classpathEntries[0].outputLocation = "/expected";
    javaProject.outputLocation = "/expected2";
    String projectLocation = "location";
    
    String[] results = launcher.getClassPaths(javaProject, projectLocation);
    
    assertEquals(2, results.length);
    assertEquals(projectLocation + "/expected", results[0]);
    assertEquals(projectLocation + "/expected2", results[1]);
  }
  
  private class TestableJavaProject implements IJavaProject {

    public IPackageFragmentRoot[] roots;
    public TestableClassPathEntry[] classpathEntries;
    public String outputLocation;

    public IClasspathEntry decodeClasspathEntry(String encodedEntry) {
      return null;
    }

    public String encodeClasspathEntry(IClasspathEntry classpathEntry) {
      return null;
    }

    public IJavaElement findElement(IPath path) {
      return null;
    }

    public IJavaElement findElement(IPath path, WorkingCopyOwner owner) {
      return null;
    }

    public IJavaElement findElement(String bindingKey, WorkingCopyOwner owner) {
      return null;
    }

    public IPackageFragment findPackageFragment(IPath path) {
      return null;
    }

    public IPackageFragmentRoot findPackageFragmentRoot(IPath path) {
      return null;
    }

    public IPackageFragmentRoot[] findPackageFragmentRoots(IClasspathEntry entry) {
      return null;
    }

    public IType findType(String fullyQualifiedName) {
      return null;
    }

    public IType findType(String fullyQualifiedName, IProgressMonitor progressMonitor) {
      return null;
    }

    public IType findType(String fullyQualifiedName, WorkingCopyOwner owner) {
      return null;
    }

    public IType findType(String packageName, String typeQualifiedName) {
      return null;
    }

    public IType findType(String fullyQualifiedName, WorkingCopyOwner owner,
        IProgressMonitor progressMonitor) {
      return null;
    }

    public IType findType(String packageName, String typeQualifiedName,
        IProgressMonitor progressMonitor) {
      return null;
    }

    public IType findType(String packageName, String typeQualifiedName, WorkingCopyOwner owner) {
      return null;
    }

    public IType findType(String packageName, String typeQualifiedName, WorkingCopyOwner owner,
        IProgressMonitor progressMonitor) {
      return null;
    }

    public IPackageFragmentRoot[] getAllPackageFragmentRoots() {
      return null;
    }

    public Object[] getNonJavaResources() {
      return null;
    }

    public String getOption(String optionName, boolean inheritJavaCoreOptions) {
      return null;
    }

    @SuppressWarnings("unchecked")
    public Map getOptions(boolean inheritJavaCoreOptions) {
      return null;
    }

    public IPath getOutputLocation() {
      return new Path(outputLocation);
    }

    public IPackageFragmentRoot getPackageFragmentRoot(String externalLibraryPath) {
      return null;
    }

    public IPackageFragmentRoot getPackageFragmentRoot(IResource resource) {
      return null;
    }

    public IPackageFragmentRoot[] getPackageFragmentRoots() {
      return roots;
    }

    @SuppressWarnings("deprecation")
    public IPackageFragmentRoot[] getPackageFragmentRoots(IClasspathEntry entry) {
      return null;
    }

    public IPackageFragment[] getPackageFragments() {
      return null;
    }

    public IProject getProject() {
      return null;
    }

    public IClasspathEntry[] getRawClasspath() {
      return classpathEntries;
    }

    public String[] getRequiredProjectNames() {
      return null;
    }

    public IClasspathEntry[] getResolvedClasspath(boolean ignoreUnresolvedEntry) {
      return null;
    }

    public boolean hasBuildState() {
      return false;
    }

    public boolean hasClasspathCycle(IClasspathEntry[] entries) {
      return false;
    }

    public boolean isOnClasspath(IJavaElement element) {
      return false;
    }

    public boolean isOnClasspath(IResource resource) {
      return false;
    }

    public IEvaluationContext newEvaluationContext() {
      return null;
    }

    public ITypeHierarchy newTypeHierarchy(IRegion region, IProgressMonitor monitor) {
      return null;
    }

    public ITypeHierarchy newTypeHierarchy(IRegion region, WorkingCopyOwner owner,
        IProgressMonitor monitor) {
      return null;
    }

    public ITypeHierarchy newTypeHierarchy(IType type, IRegion region, IProgressMonitor monitor) {
      return null;
    }

    public ITypeHierarchy newTypeHierarchy(IType type, IRegion region, WorkingCopyOwner owner,
        IProgressMonitor monitor) {
      return null;
    }

    public IPath readOutputLocation() {
      return null;
    }

    public IClasspathEntry[] readRawClasspath() {
      return null;
    }

    public void setOption(String optionName, String optionValue) {
    }

    @SuppressWarnings("unchecked")
    public void setOptions(Map newOptions) {
    }

    public void setOutputLocation(IPath path, IProgressMonitor monitor) {
    }

    public void setRawClasspath(IClasspathEntry[] entries, IProgressMonitor monitor) {
    }

    public void setRawClasspath(IClasspathEntry[] entries, boolean canModifyResources,
        IProgressMonitor monitor) {
    }

    public void setRawClasspath(IClasspathEntry[] entries, IPath outputLocation,
        IProgressMonitor monitor) {
    }

    public void setRawClasspath(IClasspathEntry[] entries, IPath outputLocation,
        boolean canModifyResources, IProgressMonitor monitor) {
    }

    public IJavaElement[] getChildren() {
      return null;
    }

    public boolean hasChildren() {
      return false;
    }

    public boolean exists() {
      return false;
    }

    public IJavaElement getAncestor(int ancestorType) {
      return null;
    }

    public String getAttachedJavadoc(IProgressMonitor monitor) {
      return null;
    }

    public IResource getCorrespondingResource() {
      return null;
    }

    public String getElementName() {
      return null;
    }

    public int getElementType() {
      return 0;
    }

    public String getHandleIdentifier() {
      return null;
    }

    public IJavaModel getJavaModel() {
      return null;
    }

    public IJavaProject getJavaProject() {
      return null;
    }

    public IOpenable getOpenable() {
      return null;
    }

    public IJavaElement getParent() {
      return null;
    }

    public IPath getPath() {
      return null;
    }

    public IJavaElement getPrimaryElement() {
      return null;
    }

    public IResource getResource() {
      return null;
    }

    public ISchedulingRule getSchedulingRule() {
      return null;
    }

    public IResource getUnderlyingResource() {
      return null;
    }

    public boolean isReadOnly() {
      return false;
    }

    public boolean isStructureKnown() {
      return false;
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
      return null;
    }

    public void close() {
    }

    public String findRecommendedLineSeparator() {
      return null;
    }

    public IBuffer getBuffer() {
      return null;
    }

    public boolean hasUnsavedChanges() {
      return false;
    }

    public boolean isConsistent() {
      return false;
    }

    public boolean isOpen() {
      return false;
    }

    public void makeConsistent(IProgressMonitor progress) {
    }

    public void open(IProgressMonitor progress) {
    }

    public void save(IProgressMonitor progress, boolean force) {
    }
  }

  private class TestableClassPathEntry implements IClasspathEntry {

    public String outputLocation;
    public String path;

    public boolean combineAccessRules() {
      return false;
    }

    public IAccessRule[] getAccessRules() {
      return null;
    }

    public int getContentKind() {
      return 0;
    }

    public int getEntryKind() {
      return 0;
    }

    public IPath[] getExclusionPatterns() {
      return null;
    }

    public IClasspathAttribute[] getExtraAttributes() {
      return null;
    }

    public IPath[] getInclusionPatterns() {
      return null;
    }

    public IPath getOutputLocation() {
      return outputLocation == null ? null : new Path(outputLocation);
    }

    public IPath getPath() {
      return new Path(path);
    }

    @Deprecated
    public IClasspathEntry getResolvedEntry() {
      return null;
    }

    public IPath getSourceAttachmentPath() {
      return null;
    }

    public IPath getSourceAttachmentRootPath() {
      return null;
    }

    public boolean isExported() {
      return false;
    }
  }
}
