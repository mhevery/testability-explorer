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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;

import java.util.List;

/**
 * Used to collect all Java packages that are children of a resource (for
 * example a source folder).
 * 
 * @author klundberg@google.com (Karin Lundberg)
 */
public class JavaPackageVisitor implements IResourceProxyVisitor {
  private List<String> javaPackages;
  private String parentFolderPath;

  public JavaPackageVisitor(List<String> javaPackages, String parentFolderPath) {
    this.javaPackages = javaPackages;
    this.parentFolderPath = parentFolderPath;
  }

  public boolean visit(IResourceProxy proxy) {
    if (proxy.getType() == IResource.FOLDER) {
      String pathString = proxy.requestFullPath().toOSString();
      if (!parentFolderPath.equals(pathString)) {
        pathString = pathString.substring(parentFolderPath.length() + 1, pathString.length());
        pathString = pathString.replace("\\", "/");
        javaPackages.add(pathString);
      }
      return true;
    } else {
      return false;
    }
  }
}
