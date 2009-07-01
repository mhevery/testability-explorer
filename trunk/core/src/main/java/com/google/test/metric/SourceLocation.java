// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class SourceLocation {

  private final String file;
  private final int lineNumber;

  public SourceLocation(String file, int lineNumber) {
    this.file = file;
    this.lineNumber = lineNumber;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public String getFile() {
    return file;
  }

  @Override
  public String toString() {
    if (file == null) {
      return String.valueOf(lineNumber);
    }
    return file + ":" + lineNumber;
  }
}
