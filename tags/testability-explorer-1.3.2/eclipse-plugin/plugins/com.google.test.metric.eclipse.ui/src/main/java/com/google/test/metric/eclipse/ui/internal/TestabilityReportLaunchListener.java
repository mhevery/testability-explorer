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

import com.google.test.metric.eclipse.core.TestabilityLaunchListener;
import com.google.test.metric.eclipse.internal.util.Logger;
import com.google.test.metric.eclipse.internal.util.TestabilityConstants;
import com.google.test.metric.eclipse.internal.util.TestabilityExplorerMessageRetriever;
import com.google.test.metric.eclipse.ui.TestabilityReportView;
import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.Issue;
import com.google.test.metric.report.issues.IssueType;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.MarkerUtilities;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Listener which knows how to handle tasks after a testability launch is successfully completed,
 * including starting a view with the html report and adding annotations to open editors.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityReportLaunchListener implements TestabilityLaunchListener {

  private final Logger logger = new Logger(); 
  private final TestabilityExplorerMessageRetriever retriever =
      new TestabilityExplorerMessageRetriever();

  public void onLaunchCompleted(final ReportOptions reportOptions, final IJavaProject javaProject,
      final List<ClassIssues> classIssues, File reportDirectory, boolean runningInCompilationMode) {
    if (!runningInCompilationMode) {
      showHtmlReportView(reportDirectory);
    }
    try {
      createMarkersFromClassIssues(classIssues, javaProject);
      if (!runningInCompilationMode) {
        showTestabilityView();
      }
    } catch (CoreException e) {
      logger.logException(e);
    }
  }
  
  private void createMarkersFromClassIssues(List<ClassIssues> classIssues,
      IJavaProject javaProject) throws CoreException {
    javaProject.getProject().deleteMarkers(TestabilityConstants.TESTABILITY_COLLABORATOR_MARKER_TYPE,
        true, IResource.DEPTH_INFINITE);
    javaProject.getProject().deleteMarkers(TestabilityConstants.TESTABILITY_CONSTRUCTOR_MARKER_TYPE,
            true, IResource.DEPTH_INFINITE);
    javaProject.getProject().deleteMarkers(TestabilityConstants.TESTABILITY_DIRECT_COST_MARKER_TYPE,
            true, IResource.DEPTH_INFINITE);
    IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
    List<IPath> sourceFolderPaths = new ArrayList<IPath>();
    for (IPackageFragmentRoot root : roots) {
      if (!root.isArchive()) {
        IResource rootResource = root.getCorrespondingResource();
        sourceFolderPaths.add(rootResource.getFullPath().removeFirstSegments(1));
      }
    }
    for (ClassIssues classIssue : classIssues) {
      IResource resource = getAbsolutePathFromJavaFile(classIssue.getClassName(), sourceFolderPaths,
          javaProject.getProject());
      if (resource != null) {
        for (Issue issue : classIssue.getMostImportantIssues()) {
          Map<String, Object> attributes = new HashMap<String, Object>();
          attributes.put(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
          attributes.put(IMarker.LINE_NUMBER, issue.getLocation().getLineNumber());
          attributes.put(IMarker.MESSAGE,
              retriever.getSuggestion(issue.getType(), issue.getSubType()));
          IssueType issueType = issue.getType(); 
          attributes.put(TestabilityConstants.ISSUE_TYPE, issue.getType().toString());
          String markerType = null;
          if (IssueType.COLLABORATOR.equals(issueType)) {
            markerType = TestabilityConstants.TESTABILITY_COLLABORATOR_MARKER_TYPE;
          } else if (IssueType.CONSTRUCTION.equals(issueType)) {
            markerType = TestabilityConstants.TESTABILITY_CONSTRUCTOR_MARKER_TYPE;
          } else if (IssueType.DIRECT_COST.equals(issueType)) {
            markerType = TestabilityConstants.TESTABILITY_DIRECT_COST_MARKER_TYPE;
          }
          if (markerType != null) {
            MarkerUtilities.createMarker(resource, attributes, markerType);
          }
        }
      } else {
        logger.logException("No Resource found for Class : " + classIssue.getClassName(), null);
      }
    }
  }

  private IResource getAbsolutePathFromJavaFile(String completeClassName,
      List<IPath> sourceFolderPaths, IProject project) {
    String path = completeClassName.replaceAll("\\.", "/");
    for (IPath sourceFolderPath : sourceFolderPaths) {
      IPath totalPath = sourceFolderPath.append(path + ".java");
      if (project.exists(totalPath)) {
        return project.findMember(totalPath);
      }
    }
    return null;
  }

  private void showTestabilityView() {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
          IViewPart viewPart = page.showView("com.google.test.metric.eclipse.ui.testabilityView");
        } catch (PartInitException e) {
          logger.logException("Error initializing Testability View", e);
        } 
      }
    });
  }

  private void showHtmlReportView(final File reportDirectory) {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        try {
          IViewPart viewPart = page.showView("com.google.test.metric.eclipse.ui.browserview");
          if (viewPart instanceof TestabilityReportView) {
            ((TestabilityReportView) viewPart).setUrl(reportDirectory.getAbsolutePath() 
                + "/" + TestabilityConstants.HTML_REPORT_FILENAME);
          }
        } catch (PartInitException e) {
          logger.logException("Error initializing Testability Report View", e);
        } 
      }
    });
  }
}
