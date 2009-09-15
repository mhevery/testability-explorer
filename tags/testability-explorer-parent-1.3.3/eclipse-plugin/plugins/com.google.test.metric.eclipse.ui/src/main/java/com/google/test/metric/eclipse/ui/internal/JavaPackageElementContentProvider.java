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

import com.google.test.metric.eclipse.internal.util.JavaProjectHelper;
import com.google.test.metric.eclipse.internal.util.Logger;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides the list of packages to be displayed in the whitelist selection dialog.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class JavaPackageElementContentProvider implements ITreeContentProvider {

  private JavaProjectHelper javaProjectHelper;
  private final static Logger logger = new Logger();

  public JavaPackageElementContentProvider() {
    javaProjectHelper = new JavaProjectHelper();
  }

  public Object[] getChildren(Object element) {
    if (element instanceof IJavaProject) {
      try {
        return javaProjectHelper.getAllJavaPackageFragmentRoots((IJavaProject) element).toArray();
      } catch (JavaModelException e) {
        logger.logException(e);
      } catch (CoreException e) {
        logger.logException(e);
      } 
    } else if (element instanceof IPackageFragmentRoot || element instanceof IPackageFragment) {
      try {
        List<IJavaElement> javaElements = new ArrayList<IJavaElement>();
        IJavaElement[] children = ((IParent) element).getChildren();
        for (IJavaElement javaElement : children) {
          if (javaElement instanceof IPackageFragment && !javaElement.getElementName().equals("")) {
            javaElements.add(javaElement);
          }
        }
        return javaElements.toArray();
      } catch (JavaModelException e) {
        logger.logException(e);
      }
    }
    return null;
  }

  public Object getParent(Object element) {
    if (element instanceof IJavaElement) {
      return ((IJavaElement) element).getParent();
    }
    return null;
  }

  public boolean hasChildren(Object element) {
    // TODO(shyamseshadri): Figure out a better, faster, more optimum way to do this.
    return getChildren(element).length > 0;
  }

  public Object[] getElements(Object inputElement) {
    return getChildren(inputElement);
  }

  public void dispose() {}

  public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}
}
