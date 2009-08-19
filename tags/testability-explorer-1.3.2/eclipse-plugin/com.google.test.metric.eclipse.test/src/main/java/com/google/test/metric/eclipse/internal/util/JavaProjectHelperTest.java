package com.google.test.metric.eclipse.internal.util;

import static org.easymock.EasyMock.expect;

import junit.framework.TestCase;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;

public class JavaProjectHelperTest extends TestCase {
  private IJavaProject javaProject;
  private JavaProjectHelper javaProjectHelper;
  private IMocksControl control;

  @Override
  protected void setUp() throws Exception {
    control = EasyMock.createControl();
    javaProject = control.createMock(IJavaProject.class);
    javaProjectHelper = new JavaProjectHelper();
  }

  public void testGetAllJavaPackagesReturnsEmptyListWhenAllFragmentsAreArchives() throws Exception {
    IPackageFragmentRoot[] roots = new IPackageFragmentRoot[1];
    IPackageFragmentRoot root = control.createMock(IPackageFragmentRoot.class);
    expect(root.isArchive()).andReturn(true);
    roots[0] = root;
    expect(javaProject.getPackageFragmentRoots()).andReturn(roots);
    control.replay();
    assertEquals(0, javaProjectHelper.getAllJavaPackages(javaProject).size());
    control.verify();
  }

  public void testGetAllJavaPackagesReturnsEmptyListWhenNoFragments() throws Exception {
    expect(javaProject.getPackageFragmentRoots()).andReturn(new IPackageFragmentRoot[0]);
    control.replay();
    assertEquals(0, javaProjectHelper.getAllJavaPackages(javaProject).size());
    control.verify();
  }

  public void testGetAllJavaPackagesReturnsPackageWhenItsThere() throws Exception {
    IPackageFragmentRoot[] roots = new IPackageFragmentRoot[1];
    IPackageFragmentRoot root = control.createMock(IPackageFragmentRoot.class);
    IResource resource = control.createMock(IResource.class);
    expect(resource.getFullPath()).andReturn(new Path("SomePath"));
    resource.accept(EasyMock.isA(JavaPackageVisitor.class), EasyMock.anyInt());
    
    expect(root.isArchive()).andReturn(false);
    expect(root.getCorrespondingResource()).andReturn(resource);
    roots[0] = root;
    expect(javaProject.getPackageFragmentRoots()).andReturn(roots);
    control.replay();
    javaProjectHelper.getAllJavaPackages(javaProject);
    control.verify();
  }
}
