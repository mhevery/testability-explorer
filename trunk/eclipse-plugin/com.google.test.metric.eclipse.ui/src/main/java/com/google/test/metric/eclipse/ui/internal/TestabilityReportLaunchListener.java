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
import com.google.test.metric.eclipse.ui.TestabilityReportView;
import com.google.test.metric.eclipse.ui.annotation.TestabilityAnnotationModel;
import com.google.test.metric.report.issues.ClassIssues;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Listener which knows how to handle tasks after a testability launch is successfully completed,
 * including starting a view with the html report and adding annotations to open editors.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityReportLaunchListener implements TestabilityLaunchListener {

  private final Logger logger = new Logger(); 

  public void onLaunchCompleted(final File reportDirectory) {
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
          logger.logException("Error initializing Testability View", e);
        } 
      }
    });
  }

  public void onLaunchCompleted(final IJavaProject javaProject,
      final List<ClassIssues> classIssues) {
    Display.getDefault().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        Map<String, EditorInfo> documentMap =
            getPathToDocumentMapping(javaProject, page.getEditorReferences());
        for (ClassIssues issue : classIssues) {
          String path = issue.getPath();
          if (documentMap.containsKey(path)) {
            EditorInfo info = documentMap.get(path);
            IDocumentProvider documentProvider = info.provider;
            FileEditorInput editorInput = info.input;
            IResource resource = editorInput.getFile();
            IAnnotationModel annotationModel = documentProvider.getAnnotationModel(editorInput);
            if (annotationModel instanceof IAnnotationModelExtension) {
              // TODO(shyamseshadri): Might need to remove AM if already added.
              IAnnotationModelExtension extension = (IAnnotationModelExtension) annotationModel;
              TestabilityAnnotationModel testabilityAnnotationModel =
                  new TestabilityAnnotationModel(issue);
              extension.addAnnotationModel(path + "-" + TestabilityConstants.TESTABILITY,
                  testabilityAnnotationModel);
            }
          }
        }
      }
    });
  }
  
  /**
   * Visible for testing. Gets the Source folders for a java project.
   */
  protected Set<String> getSourceFolders(IJavaProject javaProject) throws JavaModelException {
    Set<String> sourceFolders = new HashSet<String>();
    IPackageFragmentRoot[] roots = javaProject.getPackageFragmentRoots();
    for (IPackageFragmentRoot root : roots) {
      if (!root.isArchive()) {
        IResource rootResource = root.getCorrespondingResource();
        String rootURL = rootResource.getFullPath().toOSString();
        sourceFolders.add(rootURL + System.getProperty("file.separator"));
      }
    }
    return sourceFolders;
  }

  private Map<String, EditorInfo> getPathToDocumentMapping(IJavaProject javaProject,
      IEditorReference[] editorReferences) {
    Map<String, EditorInfo> documentMap = new HashMap<String, EditorInfo>();
    for (IEditorReference editorReference : editorReferences) {
      try {
        IEditorInput editorInput = editorReference.getEditorInput();
        if (editorInput instanceof FileEditorInput) {
          FileEditorInput fileEditorInput = (FileEditorInput) editorInput;
          IFile file = fileEditorInput.getFile();
          IPath path = file.getFullPath();
          if (fileEditorInput.getName().endsWith(".java")) {
            if (javaProject.getProject().equals(file.getProject())) { 
              IWorkbenchPart part = editorReference.getPart(false);
              if (part instanceof AbstractDecoratedTextEditor) {
                AbstractDecoratedTextEditor editor = (AbstractDecoratedTextEditor) part;
                IDocumentProvider documentProvider = editor.getDocumentProvider();
                EditorInfo info = new EditorInfo(fileEditorInput, documentProvider);
                documentMap.put(getNicePath(path.toOSString(), getSourceFolders(javaProject)),
                    info);
              }
            }
          }
        }
      } catch (PartInitException e) {
        logger.logException(e);
      } catch (JavaModelException e) {
        logger.logException(e);
      }
    }
    return documentMap;
  }
  
  /**
   * Visible for testing. Gets the path after the source folder without a trailing dot java.
   */
  protected String getNicePath(String pathString, Set<String> sourceFolders) {
    for (String sourceFolder : sourceFolders) {
      if (pathString.startsWith(sourceFolder)) {
        return stripDotJavaIfExists(pathString, sourceFolder);
      }
    }
    return pathString;
  }

  private String stripDotJavaIfExists(String pathString, String sourceFolder) {
    String substring = pathString.substring(sourceFolder.length());
    if (substring.endsWith(".java")) {
      substring = substring.substring(0, substring.length() - 5);
    }
    return substring;
  }
  
  static class EditorInfo {
    EditorInfo(FileEditorInput editorInput, IDocumentProvider documentProvider) {
      input = editorInput;
      provider = documentProvider;
    }
    IDocumentProvider provider;
    FileEditorInput input;
  }
}
