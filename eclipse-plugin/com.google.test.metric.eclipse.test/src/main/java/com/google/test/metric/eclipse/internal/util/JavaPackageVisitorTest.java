// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.eclipse.internal.util;

import junit.framework.TestCase;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

import java.util.ArrayList;
import java.util.List;

/**
 * @author klundberg@google.com (Karin Lundberg)
 */
public class JavaPackageVisitorTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  public void testVisitSimple() throws Exception {
    JavaPackageVisitor visitor = new JavaPackageVisitor(null, null);

    TestableResourceProxy proxy = new TestableResourceProxy();
    proxy.type = IResource.FILE;
    assertFalse(visitor.visit(proxy));
  }

  public void testVisitFolderParentFolderPathEqualsPath() throws Exception {
    List<String> javaPackages = null;
    String parentFolderPath = "Something";
    JavaPackageVisitor visitor = new JavaPackageVisitor(javaPackages, parentFolderPath);

    TestableResourceProxy proxy = new TestableResourceProxy();
    proxy.type = IResource.FOLDER;
    proxy.path = parentFolderPath;
    assertTrue(visitor.visit(proxy));
  }

  public void testVisitFolderParentFolderPathDoesNotEqualsPath() throws Exception {
    List<String> javaPackages = new ArrayList<String>();
    String parentFolderPath = "Something";
    String additionalPath = "Else";
    JavaPackageVisitor visitor = new JavaPackageVisitor(javaPackages, parentFolderPath);

    TestableResourceProxy proxy = new TestableResourceProxy();
    proxy.type = IResource.FOLDER;
    proxy.path = parentFolderPath + System.getProperty("file.separator") + additionalPath;
    assertTrue(visitor.visit(proxy));
    assertEquals(1, javaPackages.size());
    assertEquals(additionalPath, javaPackages.get(0));
  }

  private class TestableResourceProxy implements IResourceProxy {
    public int type;
    public String path;

    public long getModificationStamp() {
      return 0;
    }

    public String getName() {
      return null;
    }

    public Object getSessionProperty(QualifiedName key) {
      return null;
    }

    public int getType() {
      return type;
    }

    public boolean isAccessible() {
      return false;
    }

    public boolean isDerived() {
      return false;
    }

    public boolean isHidden() {
      return false;
    }

    public boolean isLinked() {
      return false;
    }

    public boolean isPhantom() {
      return false;
    }

    public boolean isTeamPrivateMember() {
      return false;
    }

    public IPath requestFullPath() {
      return new Path(path);
    }

    public IResource requestResource() {
      return null;
    }
  }
}
