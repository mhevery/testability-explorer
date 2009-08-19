// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.eclipse.internal.util;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import junit.framework.TestCase;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.runtime.Path;

import java.util.ArrayList;
import java.util.List;

/**
 * @author klundberg@google.com (Karin Lundberg)
 */
public class JavaPackageVisitorTest extends TestCase {

  public void testVisitSimple() throws Exception {
    JavaPackageVisitor visitor = new JavaPackageVisitor(null, null);

    IResourceProxy proxy = createMock(IResourceProxy.class);
    expect(proxy.getType()).andReturn(IResource.FILE);
    replay(proxy);
    assertFalse(visitor.visit(proxy));
    verify(proxy);
  }

  public void testVisitFolderParentFolderPathEqualsPath() throws Exception {
    List<String> javaPackages = null;
    String parentFolderPath = "Something";
    JavaPackageVisitor visitor = new JavaPackageVisitor(javaPackages, parentFolderPath);

    IResourceProxy proxy = createMock(IResourceProxy.class);
    expect(proxy.getType()).andReturn(IResource.FOLDER);
    expect(proxy.requestFullPath()).andReturn(new Path(parentFolderPath));
    replay(proxy);
    assertTrue(visitor.visit(proxy));
    verify(proxy);
  }

  public void testVisitFolderParentFolderPathDoesNotEqualsPath() throws Exception {
    List<String> javaPackages = new ArrayList<String>();
    String parentFolderPath = "Something";
    String additionalPath = "Else";
    JavaPackageVisitor visitor = new JavaPackageVisitor(javaPackages, parentFolderPath);

    IResourceProxy proxy = createMock(IResourceProxy.class);
    expect(proxy.getType()).andReturn(IResource.FOLDER);
    expect(proxy.requestFullPath()).andReturn(
        new Path(parentFolderPath + System.getProperty("file.separator") + additionalPath));
    replay(proxy);
    assertTrue(visitor.visit(proxy));
    assertEquals(1, javaPackages.size());
    assertEquals(additionalPath, javaPackages.get(0));
    verify(proxy);
  }
}
