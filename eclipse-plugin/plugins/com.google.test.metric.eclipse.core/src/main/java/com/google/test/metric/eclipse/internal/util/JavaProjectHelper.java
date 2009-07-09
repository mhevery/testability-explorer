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
package com.google.test.metric.eclipse.internal.util;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides utility methods for accessing Java projects.
 * 
 * @author klundberg@google.com (Karin Lundberg)
 * 
 */
public class JavaProjectHelper {

  private final Logger logger = new Logger();

  public IJavaProject[] getJavaProjects() {
    IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
    IJavaModel javaModel = JavaCore.create(workspaceRoot);
    IJavaProject[] javaProjects = null;
    try {
      javaProjects = javaModel.getJavaProjects();
    } catch (JavaModelException e) {
      logger.logException("Error getting Java Projects", e);
    }
    return javaProjects;
  }

  public IJavaProject getJavaProject(String projectName) {
    if (projectName != null && projectName.length() > 0) {
      IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
      IJavaModel javaModel = JavaCore.create(workspaceRoot);
      return javaModel.getJavaProject(projectName);
    } else {
      return null;
    }
  }

  public String getProjectLocation(IJavaProject javaProject) {
    IProject project = javaProject.getProject();
    IPath rawLocation = project.getRawLocation();
    IPath projectLocation;
    if (rawLocation != null) {
      projectLocation = rawLocation.removeLastSegments(1);
    } else {
      projectLocation = project.getParent().getLocation();
    }
    return projectLocation.toOSString();
  }

  public List<String> getAllJavaPackages(IJavaProject javaProject) throws JavaModelException,
      CoreException {
    List<String> allJavaPackages = new ArrayList<String>();
    IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
    for (IPackageFragmentRoot root : roots) {
      if (!root.isArchive()) {
        IResource rootResource = root.getCorrespondingResource();
        String rootURL = rootResource.getFullPath().toOSString();
        rootResource.accept(new JavaPackageVisitor(allJavaPackages, rootURL), IContainer.NONE);
      }
    }
    return allJavaPackages;
  }
  
  public List<IPackageFragmentRoot> getAllJavaPackageFragmentRoots(IJavaProject javaProject)
      throws JavaModelException, CoreException {
    List<IPackageFragmentRoot> allJavaPackageFragmentRoot = new ArrayList<IPackageFragmentRoot>();
    IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
    for (IPackageFragmentRoot root : roots) {
      if (!root.isArchive() && !root.getElementName().equals("")) {
        allJavaPackageFragmentRoot.add(root);
      }
    }
    return allJavaPackageFragmentRoot;
  }
}
