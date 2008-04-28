package com.google.test.metric;

import junit.framework.TestCase;

public class RegExpListTest extends TestCase {

  public void testPositiveHitInWhiteList() throws Exception {
    WhiteList whiteList = new RegExpWhiteList("java.");
    assertTrue(whiteList.isClassWhiteListed("java.lang.String"));
    assertFalse(whiteList.isClassWhiteListed("com.company.String"));
  }

  public void testRegExp() throws Exception {
    WhiteList whiteList = new RegExpWhiteList(".*String");
    assertTrue(whiteList.isClassWhiteListed("java.lang.String"));
    assertTrue(whiteList.isClassWhiteListed("com.company.String"));
  }

  public void testRegExp2() throws Exception {
    WhiteList whiteList = new RegExpWhiteList("String");
    assertFalse(whiteList.isClassWhiteListed("java.lang.String"));
    assertFalse(whiteList.isClassWhiteListed("com.company.String"));
  }

}
