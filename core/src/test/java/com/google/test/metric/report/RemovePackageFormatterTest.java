// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report;

import junit.framework.TestCase;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class RemovePackageFormatterTest extends TestCase {
  RemovePackageFormatter formatter = new RemovePackageFormatter();

  public void testShortFormatting() throws Exception {
    String shortened = formatter.format("int com.google.longpackagename.Foo.thing()");
    assertEquals("int thing()", shortened);
  }

  public void testShortFormattingWithParameters() throws Exception {
    String shortened = formatter.format("t.n.e(p.e, a.b)");
    assertEquals("e(e, b)", shortened);
  }

  public void testInnerClassMethodNamesShortFormatting() throws Exception {
    String shortened = formatter.format("String com.google.Outer$HasInner.computeString()");
    assertEquals("String computeString()", shortened);
  }

  public void testInnerClassConstructorMethod() throws Exception {
    String shortened = formatter.format("com.google.test.metric.example.ExpensiveConstructor."
        + "StaticWorkInTheConstructor$StaticHolder()");
    assertEquals("StaticWorkInTheConstructor$StaticHolder()", shortened);
  }

  public void testTypeNameShortening() throws Exception {
    String shortened = formatter.format("com.google.test.metric.example.ExpensiveConstructor."
        + "StaticWorkInTheConstructor$StaticHolder");
    assertEquals("StaticWorkInTheConstructor$StaticHolder", shortened);
  }
}
