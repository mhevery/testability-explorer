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
package com.google.test.metric.eclipse.internal.core;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.test.metric.CostModel;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.TestabilityConfig;
import com.google.test.metric.TestabilityRunner;
import com.google.test.metric.eclipse.core.TestabilityLaunchListener;
import com.google.test.metric.eclipse.core.plugin.Activator;
import com.google.test.metric.eclipse.internal.util.JavaPackageVisitor;
import com.google.test.metric.eclipse.internal.util.JavaProjectHelper;
import com.google.test.metric.eclipse.internal.util.Logger;
import com.google.test.metric.eclipse.internal.util.TestabilityConstants;
import com.google.test.metric.report.GradeCategories;
import com.google.test.metric.report.Report;
import com.google.test.metric.report.SourceLoader;
import com.google.test.metric.report.SourceReport;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Launcher for testability configurations.
 * 
 * @author klundberg@google.com (Karin Lundberg)
 *
 */
public class TestabilityLauncher implements ILaunchConfigurationDelegate2 {

  private static final String TESTABILITY = "testability";
  private JavaProjectHelper javaProjectHelper = new JavaProjectHelper();
  private final Logger logger = new Logger();

  public boolean buildForLaunch(ILaunchConfiguration configuration, String mode,
      IProgressMonitor monitor) {
    // make sure everything is built before the launch
    return TESTABILITY.equals(mode);
  }

  public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode,
      IProgressMonitor monitor) {
    return TESTABILITY.equals(mode);
  }

  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) {
    if (TESTABILITY.equals(mode)) {
      return new Launch(configuration, mode, null);
    } else {
      throw new IllegalStateException(
          "Cannot launch testability configuration when not in testability mode.");
    }
  }

  public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode,
      IProgressMonitor monitor) {
    return TESTABILITY.equals(mode);
  }

  public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
      IProgressMonitor monitor) throws CoreException {
    if (!TESTABILITY.equals(mode)) {
      throw new IllegalStateException(
          "Cannot launch testability configuration when not in testability mode.");
    }

    String projectName =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_PROJECT_NAME, "");

    IJavaProject javaProject = javaProjectHelper.getJavaProject(projectName);
    IPath projectLocationPath = javaProject.getProject().getParent().getLocation();
    String projectLocation = projectLocationPath.toOSString();

    String[] classPaths = getClassPaths(javaProject, projectLocation);

    List<String> allJavaPackages = getAllJavaPackages(javaProject);

    ClassPathFactory classPathFactory = new ClassPathFactory();
    ClassPath classPath = classPathFactory.createFromPaths(classPaths);

    IPath pluginStateLocation = Activator.getDefault().getStateLocation();
    File reportDirectory =
        new File(pluginStateLocation.toOSString(), javaProject.getProject().getName()
            + "-TestabilityReport");

    int maxExcellentCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_EXCELLENT_COST,
            TestabilityConstants.MAX_EXCELLENT_COST);
    int maxAcceptableCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_ACCEPTABLE_COST,
            TestabilityConstants.MAX_ACCEPTABLE_COST);
    int globalCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_GLOBAL_STATE_COST,
            TestabilityConstants.GLOBAL_STATE_COST);
    int cyclomaticCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_CYCLOMATIC_COST,
            TestabilityConstants.CYCLOMATIC_COST);
    Report report =
        getReport(classPath, reportDirectory, maxExcellentCost, maxAcceptableCost, cyclomaticCost,
            globalCost);
    try {
      PrintStream printStream = new PrintStream(new File(reportDirectory, "error-log"));
      // TODO(klundberg) get whitelist from configuration
      RegExpWhiteList whitelist = new RegExpWhiteList() {
        @Override
        public boolean isClassWhiteListed(String arg0) {
          return false;
        }
      };
      int recordingDepth =
          configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_RECORDING_DEPTH,
              TestabilityConstants.RECORDING_DEPTH);

      TestabilityConfig testabilityConfig =
          new TestabilityConfig(allJavaPackages, classPath, whitelist, report, printStream,
              recordingDepth);
      TestabilityRunner testabilityRunner = new TestabilityRunner(testabilityConfig);
      testabilityRunner.run();

      notifyAllListeners(reportDirectory);

      printStream.flush();
      printStream.close();
    } catch (Exception e) {
      logger.logException(e);
    }
  }

  private void notifyAllListeners(File reportDirectory) {
    IConfigurationElement[] elements =
        Platform.getExtensionRegistry().getConfigurationElementsFor(
            "com.google.test.metric.eclipse.core.testabilityLaunchListener");

    for (IConfigurationElement element : elements) {
      try {
        TestabilityLaunchListener launchListener =
            (TestabilityLaunchListener) element.createExecutableExtension("class");
        launchListener.onLaunchCompleted(reportDirectory);
      } catch (CoreException e) {
        logger.logException("Error creating Testability Launch Listener", e);
      }
    }
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

  protected String[] getClassPaths(IJavaProject javaProject, String projectLocation)
      throws JavaModelException {
    IClasspathEntry[] classPathEntries = javaProject.getRawClasspath();
    String[] classPaths = new String[classPathEntries.length + 1];
    for (int i = 0; i < classPathEntries.length; i++) {
      IClasspathEntry classPathEntry = classPathEntries[i];
      String classPathString = null;
      IPath outputPath = classPathEntry.getOutputLocation();
      if (outputPath != null) {
        classPathString = outputPath.toOSString();
      } else {
        IPath classPath = classPathEntry.getPath();
        classPathString = classPath.toOSString();
        if (!classPathString.startsWith(System.getProperty("file.separator"))) {
          classPathString = System.getProperty("file.separator") + classPathString;
        }
        classPathString = projectLocation + classPathString;
      }
      classPaths[i] = classPathString;
    }
    String defaultOutputPath = javaProject.getOutputLocation().toOSString();
    classPaths[classPathEntries.length] = projectLocation + defaultOutputPath;
    return classPaths;
  }

  private Report getReport(ClassPath classPath, File reportDirectory, int maxExcellentCost,
      int maxAcceptableCost, double cyclomaticMultiplier, double globalMultiplier) {
    CostModel costModel = new CostModel(cyclomaticMultiplier, globalMultiplier);
    GradeCategories gradeCategories = new GradeCategories(maxExcellentCost, maxAcceptableCost);
    SourceLoader sourceLoader = new SourceLoader(classPath);
    Report report =
        new SourceReport(gradeCategories, sourceLoader, reportDirectory, costModel, new Date(), 20);
    return report;
  }

  
}
