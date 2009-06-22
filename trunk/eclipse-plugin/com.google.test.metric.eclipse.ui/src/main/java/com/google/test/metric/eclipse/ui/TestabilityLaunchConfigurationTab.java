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
package com.google.test.metric.eclipse.ui;

import com.google.test.metric.eclipse.internal.util.JavaProjectHelper;
import com.google.test.metric.eclipse.internal.util.Logger;
import com.google.test.metric.eclipse.internal.util.TestabilityConstants;
import com.google.test.metric.eclipse.internal.util.TestabilityLaunchConfigurationHelper;
import com.google.test.metric.eclipse.ui.internal.JavaPackageElementContentProvider;
import com.google.test.metric.eclipse.ui.plugin.Activator;
import com.google.test.metric.eclipse.ui.plugin.ImageNotFoundException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.ui.JavaElementLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityLaunchConfigurationTab extends AbstractLaunchConfigurationTab {
  private Text projectText;
  private Text reportFolderText;
  private ListViewer whiteListList;
  private Text recordingDepthText;
  private Text cyclomaticCostText;
  private Text globalStateCostText;
  private Text maxExcellentCostText;
  private Text maxAcceptableCostText;
  private Text maxClassesToShowInIssuesReportText;
  private Button runOnCompileCheckbox;

  private JavaProjectHelper javaProjectHelper = new JavaProjectHelper();
  private TestabilityLaunchConfigurationHelper configurationHelper =
      new TestabilityLaunchConfigurationHelper();

  private Logger logger = new Logger();

  public void createControl(Composite parent) {
    Composite control = new Composite(parent, SWT.NONE);
    control.setLayout(new GridLayout(1, false));
    super.setControl(control);

    Group projectPropertiesControl = new Group(control, SWT.NONE);
    projectPropertiesControl.setLayout(new GridLayout(3, false));
    projectPropertiesControl.setText("Project:");
    GridData projectGridData = new GridData(GridData.FILL_HORIZONTAL);
    projectPropertiesControl.setLayoutData(projectGridData);

    createProjectProperties(projectPropertiesControl);

    Group testabilityPropertiesControl = new Group(control, SWT.NONE);
    testabilityPropertiesControl.setLayout(new GridLayout(2, false));
    testabilityPropertiesControl.setText("Testability Report Properties:");
    GridData testabilityGridData = new GridData(GridData.FILL_HORIZONTAL);
    testabilityPropertiesControl.setLayoutData(testabilityGridData);

    createTestabilityPropertiesControls(testabilityPropertiesControl);
  }

  private void createProjectProperties(Composite control) {
    Label projectLabel = new Label(control, SWT.NONE);
    projectLabel.setText("Project:");

    projectText = new Text(control, SWT.BORDER);
    GridData projectGridData = new GridData(GridData.FILL_HORIZONTAL);
    projectText.setLayoutData(projectGridData);
    projectText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Button projectBrowseButton = new Button(control, SWT.PUSH);
    projectBrowseButton.setText("Browse...");
    projectBrowseButton.addSelectionListener(new SelectionListener() {
      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setUpBrowseProjectDialog();
      }
    });

    Label reportFolderLabel = new Label(control, SWT.NONE);
    reportFolderLabel.setText("Report Folder:");

    reportFolderText = new Text(control, SWT.BORDER);
    GridData folderGridData = new GridData(GridData.FILL_HORIZONTAL);
    reportFolderText.setLayoutData(folderGridData);
    reportFolderText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Button reportFolderBrowseButton = new Button(control, SWT.PUSH);
    reportFolderBrowseButton.setText("Browse...");
    reportFolderBrowseButton.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setUpBrowseFolderDialog();
      }
    });

    Label whiteList = new Label(control, SWT.NONE);
    whiteList.setText("White list:");
    whiteListList = new ListViewer(control, SWT.BORDER | SWT.V_SCROLL | SWT.MULTI);
    whiteListList.getList().setBounds(0, 0, 100, 100);
    GridData whiteListGridData = new GridData(GridData.FILL_HORIZONTAL);
    whiteListGridData.heightHint = 100;
    whiteListGridData.verticalSpan = 2;
    whiteListList.getList().setLayoutData(whiteListGridData);
    whiteListList.getList().addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });
    Button whitelistPackagesBrowseButton = new Button(control, SWT.PUSH);
    whitelistPackagesBrowseButton.setText("Add...");
    whitelistPackagesBrowseButton.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setUpWhitelistPackagesDialog();
      }
    });
    GridData whiteListPackagesBrowseGridData = new GridData();
    whitelistPackagesBrowseButton.setLayoutData(whiteListPackagesBrowseGridData);

    Label spacer = new Label(control, SWT.NONE);

    Button whitelistPackagesRemoveButton = new Button(control, SWT.PUSH);
    whitelistPackagesRemoveButton.setText("Remove");
    whitelistPackagesRemoveButton.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        removeSelectedPackageFromWhitelist();
      }
    });
    GridData whiteListRemoveGridData = new GridData();
    whiteListRemoveGridData.verticalAlignment = SWT.TOP;
    whitelistPackagesRemoveButton.setLayoutData(whiteListRemoveGridData);
    
    Label spacer2 = new Label(control, SWT.NONE);
    runOnCompileCheckbox = new Button(control, SWT.CHECK);
    runOnCompileCheckbox.setText("Run this configuration at every build");
    runOnCompileCheckbox.addSelectionListener(new SelectionListener() {

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void widgetSelected(SelectionEvent e) {
        setTabDirty();
      }
    });
    GridData checkBoxGrid = new GridData();
    checkBoxGrid.horizontalSpan = 2;
    runOnCompileCheckbox.setLayoutData(checkBoxGrid);
  }

  private void removeSelectedPackageFromWhitelist() {
    whiteListList.getList().remove(whiteListList.getList().getSelectionIndices());
    setTabDirty();
  }

  private void createTestabilityPropertiesControls(Composite control) {
    Label depth = new Label(control, SWT.NONE);
    depth.setText("Depth:");
    recordingDepthText = new Text(control, SWT.BORDER);
    GridData recordingDepthGridData = new GridData();
    recordingDepthGridData.widthHint = 50;
    recordingDepthText.setLayoutData(recordingDepthGridData);
    recordingDepthText.setToolTipText("Maximum depth to recurse into when examining classes");
    recordingDepthText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Label cyclomaticCost = new Label(control, SWT.NONE);
    cyclomaticCost.setText("Cyclomatic Cost:");
    cyclomaticCostText = new Text(control, SWT.BORDER);
    GridData cyclomaticCostGridData = new GridData();
    cyclomaticCostGridData.widthHint = 50;
    cyclomaticCostText.setLayoutData(cyclomaticCostGridData);
    cyclomaticCostText.setToolTipText("Cost multiplier for Cyclomatic Complexity Issues");
    cyclomaticCostText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Label globalStateCost = new Label(control, SWT.NONE);
    globalStateCost.setText("Global State Cost:");
    globalStateCostText = new Text(control, SWT.BORDER);
    GridData globalStateCostGridData = new GridData();
    globalStateCostGridData.widthHint = 50;
    globalStateCostText.setLayoutData(globalStateCostGridData);
    globalStateCostText.setToolTipText("Cost multiplier for Global State Issues");
    globalStateCostText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Label maxExcellentCost = new Label(control, SWT.NONE);
    maxExcellentCost.setText("Max Cost for Excellent Classes:");
    maxExcellentCostText = new Text(control, SWT.BORDER);
    GridData maxExcellentCostGridData = new GridData();
    maxExcellentCostGridData.widthHint = 50;
    maxExcellentCostText.setLayoutData(maxExcellentCostGridData);
    maxExcellentCostText.setToolTipText("Cost Threshold which differenties Excellent classes from"
        + " Acceptable classes. Classes which have a cost below this are Excellent classes, while "
        + "classes which have a cost higher are not very testable.");
    maxExcellentCostText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });

    Label maxAcceptableCost = new Label(control, SWT.NONE);
    maxAcceptableCost.setText("Max Cost for Acceptable Classes:");
    maxAcceptableCostText = new Text(control, SWT.BORDER);
    GridData maxAcceptableCostGridData = new GridData();
    maxAcceptableCostGridData.widthHint = 50;
    maxAcceptableCostText.setLayoutData(maxAcceptableCostGridData);
    maxAcceptableCostText.setToolTipText("Cost Threshold which differenties Acceptable classes from"
        + " classes which need work. Classes which have a cost below this are Acceptable classes,"
        + " while classes which have a cost higher than this need significant refactoring.");
    maxAcceptableCostText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });
    
    Label maxClassesToShowInIssuesReport = new Label(control, SWT.NONE);
    maxClassesToShowInIssuesReport.setText("Max Classes In Report:");
    maxClassesToShowInIssuesReportText = new Text(control, SWT.BORDER);
    GridData maxClassesToShowInIssuesReportGridData = new GridData();
    maxClassesToShowInIssuesReportGridData.widthHint = 50;
    maxClassesToShowInIssuesReportText.setLayoutData(maxClassesToShowInIssuesReportGridData);
    maxClassesToShowInIssuesReportText.setToolTipText("The maximum number of classes to show in "
        + "the report. Only the top specified number of classes will be displayed in the "
        + "testability views.");
    maxClassesToShowInIssuesReportText.addKeyListener(new KeyListener() {
      public void keyPressed(KeyEvent e) {
      }

      public void keyReleased(KeyEvent e) {
        setTabDirty();
      }
    });
  }

  public String getName() {
    return "Testability";
  }

  public void initializeFrom(ILaunchConfiguration configuration) {
    setMessage("Create a configuration to launch a testability session.");
    try {
      String initProjectName =
          configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_PROJECT_NAME, "");
      if (initProjectName != null && initProjectName.length() > 0) {
        projectText.setText(initProjectName);
        IJavaProject javaProject = javaProjectHelper.getJavaProject(initProjectName);
        if (javaProject == null || !javaProject.exists()) {
          setErrorMessage(MessageFormat.format(
              "Project named {0} does not exist. Please choose another project.", initProjectName));
        }
      } else {
        projectText.setText("");
      }

      String initReportFolderName =
          configuration
              .getAttribute(TestabilityConstants.CONFIGURATION_ATTR_REPORT_FOLDER_NAME, "");
      if (initReportFolderName != null && initReportFolderName.length() > 0) {
        reportFolderText.setText(initReportFolderName);
      } else {
        reportFolderText.setText("");
      }

      initializeTextBoxesFromHistory(configuration);

      boolean isRunOnBuild = configuration.getAttribute(
          TestabilityConstants.CONFIGURATION_ATTR_RUN_ON_BUILD, false);
      runOnCompileCheckbox.setSelection(isRunOnBuild);
    } catch (CoreException e) {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("unchecked")
  private void initializeTextBoxesFromHistory(ILaunchConfiguration configuration)
      throws CoreException {
    int initCyclomaticCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_CYCLOMATIC_COST,
            TestabilityConstants.CYCLOMATIC_COST);
    cyclomaticCostText.setText(initCyclomaticCost + "");

    int initGlobalCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_GLOBAL_STATE_COST,
            TestabilityConstants.GLOBAL_STATE_COST);
    globalStateCostText.setText(initGlobalCost + "");

    int initMaxAcceptableCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_ACCEPTABLE_COST,
            TestabilityConstants.MAX_ACCEPTABLE_COST);
    maxAcceptableCostText.setText(initMaxAcceptableCost + "");

    int initMaxExcellentCost =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_EXCELLENT_COST,
            TestabilityConstants.MAX_EXCELLENT_COST);
    maxExcellentCostText.setText(initMaxExcellentCost + "");

    int initRecordingDepth =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_RECORDING_DEPTH,
            TestabilityConstants.RECORDING_DEPTH);
    recordingDepthText.setText(initRecordingDepth + "");
    
    int initMaxClassesInReport =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_CLASSES_IN_REPORT,
            TestabilityConstants.MAX_CLASSES_TO_SHOW_IN_ISSUES_REPORTER);
    maxClassesToShowInIssuesReportText.setText(initMaxClassesInReport + "");

    List<String> initWhitelist =
        configuration.getAttribute(TestabilityConstants.CONFIGURATION_ATTR_WHITELIST,
            TestabilityConstants.WHITELIST);
    whiteListList.getList().removeAll();
    if (initWhitelist.size() != 0) {
      whiteListList.add(initWhitelist.toArray());
    }
  }

  private void setTabDirty() {
    setDirty(true);
    updateLaunchConfigurationDialog();
  }

  @Override
  public boolean isValid(ILaunchConfiguration launchConfig) {
    String currentProjectName = projectText.getText();
    if (currentProjectName != null) {
      IJavaProject javaProject = getSelectedProject();
      if (javaProject == null || !javaProject.exists()) {
        setErrorMessage(MessageFormat.format(
            "Project named {0} does not exist. Please choose another project.",
            currentProjectName));
        return false;
      } else if (configurationHelper.isExistingLaunchConfigWithRunOnBuildOtherThanCurrent(
          currentProjectName, launchConfig.getName()) && runOnCompileCheckbox.getSelection()) {
        setErrorMessage(MessageFormat.format(
            "Project named {0} already has another active configuration with Run on every build "
                + "set. Please uncheck the box or remove it from the other configuration",
            currentProjectName));
        return false;
      } else {
        setErrorMessage(null);
        setMessage("Create a configuration to launch a testability session.");
        return true;
      }
    }
    return false;
  }

  public void performApply(ILaunchConfigurationWorkingCopy configuration) {
    IJavaProject project = getSelectedProject();
    if (project != null) {
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_PROJECT_NAME, project
          .getElementName());
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_REPORT_FOLDER_NAME,
          reportFolderText.getText());
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_CYCLOMATIC_COST, Integer
          .parseInt(cyclomaticCostText.getText()));
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_GLOBAL_STATE_COST, Integer
          .parseInt(globalStateCostText.getText()));
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_ACCEPTABLE_COST,
          Integer.parseInt(maxAcceptableCostText.getText()));
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_EXCELLENT_COST,
          Integer.parseInt(maxExcellentCostText.getText()));
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_CLASSES_IN_REPORT,
          Integer.parseInt(maxClassesToShowInIssuesReportText.getText()));
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_RECORDING_DEPTH, Integer
          .parseInt(recordingDepthText.getText()));
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_WHITELIST,
          Arrays.asList(whiteListList.getList().getItems()));
      configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_RUN_ON_BUILD,
          runOnCompileCheckbox.getSelection());
    }
  }

  private IJavaProject getSelectedProject() {
    String projectName = projectText.getText();
    return javaProjectHelper.getJavaProject(projectName);
  }

  public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_PROJECT_NAME, "");
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_REPORT_FOLDER_NAME, "");
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_RECORDING_DEPTH,
        TestabilityConstants.RECORDING_DEPTH);
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_CYCLOMATIC_COST,
        TestabilityConstants.CYCLOMATIC_COST);
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_GLOBAL_STATE_COST,
        TestabilityConstants.GLOBAL_STATE_COST);
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_EXCELLENT_COST,
        TestabilityConstants.MAX_EXCELLENT_COST);
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_ACCEPTABLE_COST,
        TestabilityConstants.MAX_ACCEPTABLE_COST);
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_MAX_CLASSES_IN_REPORT,
        TestabilityConstants.MAX_CLASSES_TO_SHOW_IN_ISSUES_REPORTER);
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_WHITELIST,
        TestabilityConstants.WHITELIST);
    configuration.setAttribute(TestabilityConstants.CONFIGURATION_ATTR_RUN_ON_BUILD,
        false);
  }

  private void setUpBrowseFolderDialog() {
    DirectoryDialog directoryDialog = new DirectoryDialog(getControl().getShell());
    String folder = directoryDialog.open();
    if (folder != null && folder.length() > 0) {
      reportFolderText.setText(folder);
      setTabDirty();
    }
  }
  
  private void setUpWhitelistPackagesDialog() {
    IJavaProject project = getSelectedProject();
    ElementTreeSelectionDialog dialog =
        new ElementTreeSelectionDialog(getControl().getShell(),
            new JavaElementLabelProvider(JavaElementLabelProvider.SHOW_BASICS),
            new JavaPackageElementContentProvider());
    dialog.setInput(project);
    dialog.addFilter(new ViewerFilter() {

      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (element instanceof IPackageFragment) {
          return !((IPackageFragment) element).getElementName().equals("");
        }
        if (element instanceof ICompilationUnit) {
          return false;
        }
        return true;
      }
      
    });
    dialog.setMessage("Choose packages to whitelist:");

    if (dialog.open() == Window.OK) {
      Object[] results = dialog.getResult();
      String[] stringArray = new String[results.length];
      for (int i = 0; i < results.length; i++) {
        if (results[i] instanceof IJavaElement) {
          stringArray[i] = ((IJavaElement) results[i]).getElementName();
        }
      }
      whiteListList.add(stringArray);
      setTabDirty();
    }
  }

  private void setUpBrowseProjectDialog() {
    ILabelProvider projectLabelProvider = new BrowseProjectLabelProvider();

    IJavaProject[] javaProjects = javaProjectHelper.getJavaProjects();

    ElementListSelectionDialog dialog =
        new ElementListSelectionDialog(getControl().getShell(), projectLabelProvider);
    dialog.setMessage("Choose a project to run testability on:");

    if (javaProjects != null) {
      dialog.setElements(javaProjects);
    }

    if (dialog.open() == Window.OK) {
      IJavaProject project = (IJavaProject) dialog.getFirstResult();
      projectText.setText(project.getElementName());
      setTabDirty();
    }
  }

  private class BrowseProjectLabelProvider extends LabelProvider {

    @Override
    public Image getImage(Object element) {
      if (element instanceof IJavaProject) {
        try {
          return Activator.getDefault().getImage("icons/projects.gif");
        } catch (ImageNotFoundException e) {
          logger.logException(e);
        }
      }
      return null;
    }

    @Override
    public String getText(Object element) {
      if (element instanceof IJavaProject) {
        return ((IJavaProject) element).getElementName();
      }
      return null;
    }
  }
}
