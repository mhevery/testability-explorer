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
import com.google.test.metric.eclipse.ui.TestabilityReportView;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import java.io.File;
import java.net.MalformedURLException;

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
                + "/index.html");
          }
        } catch (PartInitException e) {
          logger.logException("Error initializing Testability View", e);
        } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    });
  }
}
