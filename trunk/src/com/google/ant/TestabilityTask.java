/*
 * Copyright 2007 Google Inc.
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

package com.google.ant;

import com.google.test.metric.Testability;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class TestabilityTask extends Task {

    private TaskModel model = new TaskModel();

    public void setMaxAcceptableCost(int cost) {
        model.setMaxAcceptableCost(cost);
    }

    public void setMaxExcellentCost(int cost) {
        model.setMaxExcellentCost(cost);
    }

    public void setMinCost(int cost) {
        model.setMinCost(cost);
    }

    public void setPrint(String printVal) {
        model.setPrint(printVal);
    }

    public void setPrintDepth(int depth) {
        model.setPrintDepth(depth);
    }

    public void setWhiteList(String whiteListVal) {
        model.setWhiteList(whiteListVal);
    }

    public void setWorsOffenderCount(int count) {
        model.setWorsOffenderCount(count);
    }

    public void setResultFile(String resultFile) {
        model.setResultFile(resultFile);
    }

    public void setErrorFile(String errorFile) {
        model.setErrorFile(errorFile);
    }

    public void setFilter(String filter) {
        model.setFilter(filter);
    }

    public void setFailProperty(String property) {
        model.setFailProperty(property);
    }

    public void addFileSet(FileSet fs) {
        model.addFileSet(fs);
    }

    public void execute() throws BuildException {
        List<String> validationMessages = new ArrayList<String>();
        boolean allOk = model.validate(validationMessages);

        String cp = model.getClassPath();
        PrintStream out = model.getResultPrintStream();
        PrintStream err = model.getErrorPrintStream();

        if (allOk) {
            logValidationMessages(validationMessages);
            log("INFO: Testability Starting ...", Project.MSG_VERBOSE);
            Testability.main(out, err, model.getFilter(), "-cp", cp);
            log("INFO: Testability Done", Project.MSG_VERBOSE);
        }
        else {
            logValidationMessages(validationMessages);
            failBuild(validationMessages);
        }

        checkResultsAreOk();
    }

    private void checkResultsAreOk() {
//        if (false) {
//            failBuild("The avg of violations per ncss is to high - <failvalue>: " + 0);
//        }
    }

    private void failBuild(List<String> messages) {

        if (model.isFailPropertySet())
        {
            getProject().setNewProperty(model.getFailProperty(), "true");
        }
        else
        {
            String flattened = "";

            for (String message : messages) {
                flattened += message + ". ";
            }

            throw new BuildException(flattened);
        }
    }

    private void logValidationMessages(List<String> messages) {
        for (String message: messages) {
            log("CAUTION: " + message, Project.MSG_VERBOSE);
        }
    }
}
