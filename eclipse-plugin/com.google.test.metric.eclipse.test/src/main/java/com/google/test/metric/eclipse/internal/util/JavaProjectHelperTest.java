package com.google.test.metric.eclipse.internal.util;

import junit.framework.TestCase;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
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

import java.net.URI;
import java.util.Map;

public class JavaProjectHelperTest extends TestCase {
  private TestableJavaProject javaProject;
  private JavaProjectHelper javaProjectHelper;

  @Override
  protected void setUp() throws Exception {
    javaProject = new TestableJavaProject();
    javaProjectHelper = new JavaProjectHelper();
  }

  public void testGetAllJavaPackagesReturnsEmptyListWhenAllFragmentsAreArchives() throws Exception {
    javaProject.roots = new IPackageFragmentRoot[1];
    TestablePackageFragmentRoot root = new TestablePackageFragmentRoot();
    root.archive = true;
    javaProject.roots[0] = root;
    assertEquals(0, javaProjectHelper.getAllJavaPackages(javaProject).size());
  }

  public void testGetAllJavaPackagesReturnsEmptyListWhenNoFragments() throws Exception {
    javaProject.roots = new IPackageFragmentRoot[0];
    assertEquals(0, javaProjectHelper.getAllJavaPackages(javaProject).size());
  }

  public void testGetAllJavaPackagesReturnsPackageWhenItsThere() throws Exception {
    javaProject.roots = new IPackageFragmentRoot[1];
    TestablePackageFragmentRoot root = new TestablePackageFragmentRoot();
    root.archive = false;
    javaProject.roots[0] = root;
    ListeningResource resource = new ListeningResource();
    resource.path = "SomePath";
    root.resource = resource;
    javaProjectHelper.getAllJavaPackages(javaProject);
    assertTrue("Resource's accept method was not called", resource.isAcceptCalled);
    assertTrue("Resource's Package visitor not the right type",
        resource.visitor instanceof JavaPackageVisitor);
  }
  
  private class TestablePackageFragmentRoot implements IPackageFragmentRoot {
    public boolean archive;
    public IResource resource;

    public void attachSource(IPath sourcePath, IPath rootPath, IProgressMonitor monitor) {
    }

    public void copy(IPath destination, int updateResourceFlags, int updateModelFlags,
        IClasspathEntry sibling, IProgressMonitor monitor) {
    }

    public IPackageFragment createPackageFragment(String name, boolean force,
        IProgressMonitor monitor) {
      return null;
    }

    public void delete(int updateResourceFlags, int updateModelFlags, IProgressMonitor monitor) {
    }

    public int getKind() {
      return 0;
    }

    public Object[] getNonJavaResources() {
      return null;
    }

    public IPackageFragment getPackageFragment(String packageName) {
      return null;
    }

    public IClasspathEntry getRawClasspathEntry() {
      return null;
    }

    public IPath getSourceAttachmentPath() {
      return null;
    }

    public IPath getSourceAttachmentRootPath() {
      return null;
    }

    public boolean isArchive() {
      return archive;
    }

    public boolean isExternal() {
      return false;
    }

    public void move(IPath destination, int updateResourceFlags, int updateModelFlags,
        IClasspathEntry sibling, IProgressMonitor monitor) {
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
      return resource;
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
  
  private class ListeningResource implements IResource {

    public boolean isAcceptCalled = false;
    public IResourceProxyVisitor visitor;
    public String path;

    public void accept(IResourceVisitor visitor) {
    }

    public void accept(IResourceProxyVisitor visitor, int memberFlags) {
      isAcceptCalled = true;
      this.visitor = visitor;
    }

    public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) {
    }

    public void accept(IResourceVisitor visitor, int depth, int memberFlags) {
    }

    public void clearHistory(IProgressMonitor monitor) {
    }

    public void copy(IPath destination, boolean force, IProgressMonitor monitor) {
    }

    public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) {
    }

    public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) {
    }

    public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {
    }

    public IMarker createMarker(String type) {
      return null;
    }

    public IResourceProxy createProxy() {
      return null;
    }

    public void delete(boolean force, IProgressMonitor monitor) {
    }

    public void delete(int updateFlags, IProgressMonitor monitor) {
    }

    public void deleteMarkers(String type, boolean includeSubtypes, int depth) {
    }

    public boolean exists() {
      return false;
    }

    public IMarker findMarker(long id) {
      return null;
    }

    public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) {
      return null;
    }

    public int findMaxProblemSeverity(String type, boolean includeSubtypes, int depth) {
      return 0;
    }

    public String getFileExtension() {
      return null;
    }

    public IPath getFullPath() {
      return new Path(path);
    }

    public long getLocalTimeStamp() {
      return 0;
    }

    public IPath getLocation() {
      return null;
    }

    public URI getLocationURI() {
      return null;
    }

    public IMarker getMarker(long id) {
      return null;
    }

    public long getModificationStamp() {
      return 0;
    }

    public String getName() {
      return null;
    }

    public IContainer getParent() {
      return null;
    }

    @SuppressWarnings("unchecked")
    public Map getPersistentProperties() {
      return null;
    }

    public String getPersistentProperty(QualifiedName key) {
      return null;
    }

    public IProject getProject() {
      return null;
    }

    public IPath getProjectRelativePath() {
      return null;
    }

    public IPath getRawLocation() {
      return null;
    }

    public URI getRawLocationURI() {
      return null;
    }

    public ResourceAttributes getResourceAttributes() {
      return null;
    }

    @SuppressWarnings("unchecked")
    public Map getSessionProperties() {
      return null;
    }

    public Object getSessionProperty(QualifiedName key) {
      return null;
    }

    public int getType() {
      return 0;
    }

    public IWorkspace getWorkspace() {
      return null;
    }

    public boolean isAccessible() {
      return false;
    }

    public boolean isDerived() {
      return false;
    }

    public boolean isDerived(int options) {
      return false;
    }

    public boolean isHidden() {
      return false;
    }

    public boolean isLinked() {
      return false;
    }

    public boolean isLinked(int options) {
      return false;
    }

    @SuppressWarnings("deprecation")
    public boolean isLocal(int depth) {
      return false;
    }

    public boolean isPhantom() {
      return false;
    }

    @SuppressWarnings("deprecation")
    public boolean isReadOnly() {
      return false;
    }

    public boolean isSynchronized(int depth) {
      return false;
    }

    public boolean isTeamPrivateMember() {
      return false;
    }

    public void move(IPath destination, boolean force, IProgressMonitor monitor) {
    }

    public void move(IPath destination, int updateFlags, IProgressMonitor monitor) {
    }

    public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) {
    }

    public void move(IProjectDescription description, boolean force, boolean keepHistory,
        IProgressMonitor monitor) {
    }

    public void refreshLocal(int depth, IProgressMonitor monitor) {
    }

    public void revertModificationStamp(long value) {
    }

    public void setDerived(boolean isDerived) {
    }

    public void setHidden(boolean isHidden) {
    }

    @SuppressWarnings("deprecation")
    public void setLocal(boolean flag, int depth, IProgressMonitor monitor) {
    }

    public long setLocalTimeStamp(long value) {
      return 0;
    }

    public void setPersistentProperty(QualifiedName key, String value) {
    }

    @SuppressWarnings("deprecation")
    public void setReadOnly(boolean readOnly) {
    }

    public void setResourceAttributes(ResourceAttributes attributes) {
    }

    public void setSessionProperty(QualifiedName key, Object value) {
    }

    public void setTeamPrivateMember(boolean isTeamPrivate) {
    }

    public void touch(IProgressMonitor monitor) {
    }

    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter) {
      return null;
    }

    public boolean contains(ISchedulingRule rule) {
      return false;
    }

    public boolean isConflicting(ISchedulingRule rule) {
      return false;
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
}
