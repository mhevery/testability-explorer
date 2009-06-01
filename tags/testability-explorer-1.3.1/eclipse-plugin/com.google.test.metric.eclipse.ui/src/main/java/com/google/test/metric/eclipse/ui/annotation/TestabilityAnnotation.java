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

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;

/**
 * Represents a testability annotation.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class TestabilityAnnotation extends Annotation {
  public static final String EXCELLENT_TESTABILITY = "com.google.test.metric.ui.excellentTestability";
  public static final String GOOD_TESTABILITY = "com.google.test.metric.ui.goodTestability";
  public static final String BAD_TESTABILITY = "com.google.test.metric.ui.badTestability";
  private final Position position;

  public TestabilityAnnotation(Position position, String type) {
    super(type, false, null);
    this.position = position;
  }

  public Position getPosition() {
    return position;
  }
}
