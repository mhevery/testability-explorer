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
package com.google.test.metric.eclipse.ui.annotation;

import com.google.test.metric.ClassCost;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.Issue;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

import java.util.ArrayList;
import java.util.List;

/**
 * Translator which knows how to convert a {@link ClassCost} to a 
 * {@link TestabilityAnnotation}.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class IssuesToAnnotationTranslator {
  
  public List<Annotation> getAnnotations(ClassIssues classIssues,
      IDocument document) throws BadLocationException {
    List<Annotation> annotations = new ArrayList<Annotation>();
    for (Issue issue : classIssues.getMostImportantIssues()) {
      // TODO(shyamseshadri): COULD BE OFF BY ONE. FIGURE OUT!!!
      IRegion region = document.getLineInformation(issue.getLineNumber());
      Position position = new Position(region.getOffset(), region.getLength());
      // TODO(shyamseshadri): FIGURE OUT LEVEL OF TESTABILITY
      TestabilityAnnotation annotation = new TestabilityAnnotation(position,
          TestabilityAnnotation.BAD_TESTABILITY);
      annotations.add(annotation);
    }
    return annotations;
  }
}
