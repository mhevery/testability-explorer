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

import junit.framework.TestCase;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * Tests for {@link TestabilityAnnotationModel}.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityAnnotationModelTest extends TestCase {

  private TestabilityAnnotationModel model = new TestabilityAnnotationModel(null);

  public void testGetPositionOfTestabilityAnnotation() throws Exception {
    Annotation annotation = new TestabilityAnnotation(new Position(0), "something");
    assertNotNull(model.getPosition(annotation));
    assertEquals(0, model.getPosition(annotation).getOffset());
  }

  public void testGetPositionOfNonTestabilityAnnotation() throws Exception {
    Annotation annotation = new Annotation(true);
    assertNull(model.getPosition(annotation));
  }
}
