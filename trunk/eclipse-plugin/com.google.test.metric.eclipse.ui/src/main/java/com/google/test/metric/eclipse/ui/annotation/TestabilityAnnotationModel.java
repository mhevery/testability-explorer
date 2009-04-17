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

import com.google.test.metric.eclipse.internal.util.Logger;
import com.google.test.metric.report.issues.ClassIssues;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the model backing the testability annotations.
 *
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityAnnotationModel implements IAnnotationModel {
  private List<IAnnotationModelListener> listeners =
      new ArrayList<IAnnotationModelListener>();
  private final Logger logger = new Logger();
  private List<Annotation> annotations;
  private final ClassIssues classIssues;
  private final IssuesToAnnotationTranslator translator =
      new IssuesToAnnotationTranslator();

  public TestabilityAnnotationModel(ClassIssues classIssues) {
    this.classIssues = classIssues;
  }

  /**
   * Unsupported, because addition of annotations are handled by the model itself in the
   * {@link #connect(IDocument)}.
   */
  public void addAnnotation(Annotation annotation, Position position) {
    throw new UnsupportedOperationException();
  }

  public void addAnnotationModelListener(IAnnotationModelListener listener) {
    listeners.add(listener);
    notifyAllListeners();
  }

  public void connect(IDocument document) {
    try {
      annotations = translator.getAnnotations(classIssues, document);
      for (Annotation annotation : annotations) {
        TestabilityAnnotation testabilityAnnotation = (TestabilityAnnotation) annotation;
        document.addPosition(testabilityAnnotation.getPosition());
      }
      notifyAllListeners();
    } catch (BadLocationException e) {
      logger.logException(e);
    }
  }

  private void notifyAllListeners() {
    AnnotationModelEvent event = new AnnotationModelEvent(this, false);
    for (Annotation annotation : annotations) {
      event.annotationAdded(annotation);
    }
    for (IAnnotationModelListener listener : listeners) {
      if (listener instanceof IAnnotationModelListenerExtension) {
        ((IAnnotationModelListenerExtension) listener).modelChanged(event); 
      } else {
        listener.modelChanged(this);
      }
    }
  }

  public void disconnect(IDocument document) {
    annotations.clear();
    notifyAllListeners();
  }

  public Iterator<?> getAnnotationIterator() {
    return annotations.iterator();
  }

  public Position getPosition(Annotation annotation) {
    if (annotation instanceof TestabilityAnnotation) {
      return ((TestabilityAnnotation)annotation).getPosition();
    }
    return null;
  }

  /**
   * Unsupported, because removal of annotations are handled by the model itself in the
   * {@link #disconnect(IDocument)}.
   */
  public void removeAnnotation(Annotation annotation) {
    throw new UnsupportedOperationException();
  }

  public void removeAnnotationModelListener(IAnnotationModelListener listener) {
    listeners.remove(listener);
  }
}
