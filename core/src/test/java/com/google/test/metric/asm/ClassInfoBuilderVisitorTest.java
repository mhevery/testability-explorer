// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.asm;

import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.ClassInfo;

import junit.framework.TestCase;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class ClassInfoBuilderVisitorTest extends TestCase {
  ClassInfoBuilderVisitor visitor = new ClassInfoBuilderVisitor(null);

  public void testGetOriginalFilePath() {
    assertEquals("java/lang/String.java", visitor.guessSourceFileName("java.lang.String"));
  }

  public void testPathInnerClassIsStripped() throws Exception {
    assertEquals("com/google/Foo.java", visitor.guessSourceFileName("com.google.Foo$1"));
  }

  public void testLoadingThisClassGivesRightFilename() throws Exception {
    ClassInfo aClass = new JavaClassRepository().getClass(this.getClass().getName());
    assertEquals("com/google/test/metric/asm/ClassInfoBuilderVisitorTest.java", aClass.getFileName());
  }
}
