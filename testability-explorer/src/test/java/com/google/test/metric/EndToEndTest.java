// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric;

import junit.framework.TestCase;

/**
 * Run the actual testability explorer on itself. This is the largest integration test we have.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class EndToEndTest extends TestCase {
  public void testTestabilityExplorer() throws Exception {
    Testability.main("com.google.test.metric");
  }
}
