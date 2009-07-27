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
import com.google.test.metric.AnalysisModel;
import com.google.test.metric.CostModel;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.JavaTestabilityModule;
import com.google.test.metric.JavaTestabilityRunner;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.ReportGeneratorProvider;
import com.google.test.metric.ReportGeneratorProvider.ReportFormat;
import com.google.test.metric.eclipse.core.TestabilityLaunchListener;
import com.google.test.metric.eclipse.core.plugin.Activator;
import com.google.test.metric.eclipse.internal.util.JavaProjectHelper;
import com.google.test.metric.eclipse.internal.util.Logger;
import com.google.test.metric.eclipse.internal.util.TestabilityConstants;
import com.google.test.metric.report.ReportGenerator;
import com.google.test.metric.report.ReportModel;
import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.SourceLoader;
import com.google.test.metric.report.html.HtmlReportModel;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.ClassMunger;
import com.google.test.metric.report.issues.HypotheticalCostModel;
import com.google.test.metric.report.issues.IssuesReporter;
import com.google.test.metric.report.issues.TriageIssuesQueue;

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
import org.eclipse.jdt.core.JavaModelException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

/**
 * Launcher for testability configurations.
 * 
 * @author klundberg@google.com (Karin Lundberg)
 *
 */
public class TestabilityLauncher implements ILaunchConfigurationDelegate2 {

  private JavaProjectHelper javaProjectHelper = new JavaProjectHelper();
  private final Logger logger = new Logger();

  public boolean buildForLaunch(ILaunchConfiguration configuration, String mode,
      IProgressMonitor monitor) {
    // make sure everything is built before the launch
    return TestabilityConstants.TESTABILITY.equals(mode);
  }

  public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode,
      IProgressMonitor monitor) {
    return TestabilityConstants.TESTABILITY.equals(mode);
  }

  public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) {
    if (TestabilityConstants.TESTABILITY.equals(mode)) {
      return new Launch(configuration, mode, null);
    } else {
      throw new IllegalStateException(
          "Cannot launch testability configuration when not in testability mode.");
    }
  }

  public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode,
      IProgressMonitor monitor) {
    return TestabilityConstants.TESTABILITY.equals(mode);
  }

  @SuppressWarnings("unchecked")
  public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch,
      IProgressMonitor monitor) throws CoreException {
    if (!TestabilityConstants.TESTABILITY.equals(mode)) {
      throw new IllegalStateException(
          "Cannot launch testability configuration when not in testability mode.");
    }

    String projectName =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_PROJECT_NAME, "");

    IJavaProject javaProject = javaProjectHelper.getJavaProject(projectName);
    String projectLocation = javaProjectHelper.getProjectLocation(javaProject);

    String[] classPaths = getClassPaths(javaProject, projectLocation);

    List<String> allJavaPackages = javaProjectHelper.getAllJavaPackages(javaProject);

    ClassPathFactory classPathFactory = new ClassPathFactory();
    ClassPath classPath = classPathFactory.createFromPaths(classPaths);

    IPath pluginStateLocation = Activator.getDefault().getStateLocation();
    String baseReportDirectoryString = 
      configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_REPORT_FOLDER_NAME, "");
    if ("".equals(baseReportDirectoryString)) {
      baseReportDirectoryString = pluginStateLocation.toOSString();
    }
    File reportDirectory = 
        new File(baseReportDirectoryString, javaProject.getProject().getName()
            + "-TestabilityReport");
    if (!reportDirectory.exists()) {
      reportDirectory.mkdirs();
    }

    int maxExcellentCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_EXCELLENT_COST,
            TestabilityConstants.MAX_EXCELLENT_COST);
    int maxAcceptableCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_ACCEPTABLE_COST,
            TestabilityConstants.MAX_ACCEPTABLE_COST);
    int maxClassesInReport = 
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_CLASSES_IN_REPORT,
            TestabilityConstants.MAX_CLASSES_TO_SHOW_IN_ISSUES_REPORTER);
    double globalCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_GLOBAL_STATE_COST,
            TestabilityConstants.GLOBAL_STATE_COST);
    double cyclomaticCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_CYCLOMATIC_COST,
            TestabilityConstants.CYCLOMATIC_COST);
    int printDepth = TestabilityConstants.RECORDING_DEPTH;
    List<String> whitelistPackages = 
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_WHITELIST,
            TestabilityConstants.WHITELIST);
    
    try {
      PrintStream reportStream = new PrintStream(new FileOutputStream(
          new File(reportDirectory, TestabilityConstants.HTML_REPORT_FILENAME)));
      PrintStream errorStream = new PrintStream(new FileOutputStream(
          new File(reportDirectory, TestabilityConstants.ERROR_LOG_FILENAME)));

      RegExpWhiteList whitelist = new RegExpWhiteList("java.");
      for (String packageName : whitelistPackages) {
        whitelist.addPackage(packageName);
      }
      
      CostModel costModel = new CostModel(cyclomaticCost, globalCost);
      JavaClassRepository classRepository = new JavaClassRepository(classPath);
      MetricComputer computer = new MetricComputer(classRepository, errorStream, whitelist, printDepth);
      HypotheticalCostModel hypotheticalCostModel = new HypotheticalCostModel(costModel, 
          new ClassMunger(classRepository), computer);
      IssuesReporter issuesReporter = new IssuesReporter(
          new TriageIssuesQueue<ClassIssues>(maxAcceptableCost,
              maxClassesInReport, new ClassIssues.TotalCostComparator()), hypotheticalCostModel);
      ReportOptions options = new ReportOptions(cyclomaticCost, globalCost, maxExcellentCost,
          maxAcceptableCost, maxClassesInReport, -1, -1, printDepth, -1, "", "");
      SourceLoader sourceLoader = new SourceLoader(classPath);

      AnalysisModel analysisModel = new AnalysisModel(issuesReporter);
      ReportModel reportModel = new HtmlReportModel(costModel, analysisModel, options);
      ReportGenerator report = new ReportGeneratorProvider(classPath, options,
          reportStream, hypotheticalCostModel, ReportFormat.html).build(costModel, reportModel, sourceLoader);

      new JavaTestabilityRunner(report, classPath, classRepository, computer, allJavaPackages, whitelist, errorStream).run();

      boolean runningInCompilationMode = configuration.getAttribute(
          TestabilityConstants.CONFIGURATION_ATTR_RUNNING_IN_COMPILATION_MODE, false);
      notifyAllListeners(options, analysisModel.getWorstOffenders(), javaProject, reportDirectory,
          runningInCompilationMode);

      reportStream.flush();
      reportStream.close();
    } catch (Exception e) {
      logger.logException(e);
    }
  }

  private void notifyAllListeners(ReportOptions reportOptions,
      List<ClassIssues> classIssues, IJavaProject javaProject, File reportDirectory,
      boolean runningInCompilationMode) {
    IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
         "com.google.test.metric.eclipse.core.testabilityLaunchListener");

    for (IConfigurationElement element : elements) {
      try {
        TestabilityLaunchListener launchListener =
            (TestabilityLaunchListener) element.createExecutableExtension("class");
        launchListener.onLaunchCompleted(reportOptions, javaProject, classIssues, reportDirectory,
            runningInCompilationMode);
      } catch (CoreException e) {
        logger.logException("Error creating Testability Launch Listener", e);
      }
    }
  }

  public String[] getClassPaths(IJavaProject javaProject, String projectLocation)
      throws JavaModelException {
    IClasspathEntry[] classPathEntries = javaProject.getRawClasspath();
    String[] classPaths = new String[classPathEntries.length + 1];
    for (int i = 0; i < classPathEntries.length; i++) {
      IClasspathEntry classPathEntry = classPathEntries[i];
      String classPathString = null;
      IPath outputPath = classPathEntry.getOutputLocation();
      if (outputPath != null) {
        classPathString = projectLocation + outputPath.toOSString();
      } else {
        IPath classPath = classPathEntry.getPath();
        classPathString = classPath.toOSString();
        if (!classPathString.startsWith(System.getProperty("file.separator"))) {
          classPathString = System.getProperty("file.separator") + classPathString;
        }
        classPathString = projectLocation + classPathString;
      }
      classPathString = classPathString.replace("\\", "/");
      classPaths[i] = classPathString;
    }
    String defaultOutputPath = javaProject.getOutputLocation().toOSString();
    defaultOutputPath = defaultOutputPath.replace("\\", "/");
    classPaths[classPathEntries.length] = projectLocation + defaultOutputPath;
    return classPaths;
  }
}
