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
package com.google.test.metric.eclipse.ui.internal.compilation;

import com.google.test.metric.eclipse.internal.util.Logger;
import com.google.test.metric.eclipse.internal.util.TestabilityConstants;
import com.google.test.metric.eclipse.internal.util.TestabilityLaunchConfigurationHelper;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.compiler.CompilationParticipant;

/**
 * Runs the testability explorer once compilation / build is finished.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityCompilationParticipant extends CompilationParticipant {

  private Logger logger = new Logger();
  private TestabilityLaunchConfigurationHelper configurationHelper =
      new TestabilityLaunchConfigurationHelper();

  public TestabilityCompilationParticipant() {
  }

  @Override
  public boolean isActive(IJavaProject project) {
    return configurationHelper.isExistingLaunchConfigurationWithRunOnBuild(
        project.getElementName());
  }

  @Override
  public void buildFinished(IJavaProject project) {
    super.buildFinished(project);
    ILaunchConfiguration launchConfiguration =
        configurationHelper.getLaunchConfiguration(project.getElementName());
    if (launchConfiguration != null) {
      try {
        ILaunch launch = launchConfiguration.launch(TestabilityConstants.TESTABILITY_MODE, null);
      } catch (CoreException e) {
        logger.logException(e);
      }
    }
  }
}
