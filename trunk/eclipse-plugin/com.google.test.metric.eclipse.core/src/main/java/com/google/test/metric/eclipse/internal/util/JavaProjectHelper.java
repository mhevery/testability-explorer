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

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

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
}
