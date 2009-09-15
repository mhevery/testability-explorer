package com.google.test.metric;

import junit.framework.TestCase;

public class JavaTypeTest extends TestCase {

  public void testParseReturnType() throws Exception {
    assertEquals(JavaType.VOID, JavaType.fromDescReturn("()V"));
    assertEquals(JavaType.INT, JavaType.fromDescReturn("(ILabc;I)I"));
  }

  public void testParseParameters() throws Exception {
    assertEquals(0, JavaType.fromDescParameters("()V").size());
    assertEquals(1, JavaType.fromDescParameters("(I)V").size());
    assertEquals(JavaType.INT, JavaType.fromDescParameters("(I)V").get(0));
    assertEquals(JavaType.INT, JavaType.fromDescParameters("(ILLab;I)V").get(0));
    assertEquals(JavaType.fromJava("Lab"), JavaType.fromDescParameters("(ILLab;I)V").get(1));
    assertEquals(JavaType.fromJava("Lab").toArray(), JavaType.fromDescParameters("(I[LLab;I)V").get(1));
    assertEquals(JavaType.INT, JavaType.fromDescParameters("(ILLab;I)V").get(2));
  }

  public void testParseParametersWithArray() throws Exception {
    assertEquals(3, JavaType.fromDescParameters("([BII)V").size());
    assertEquals(JavaType.BYTE.toArray(), JavaType.fromDescParameters("([BII)V").get(0));
    assertEquals(JavaType.INT, JavaType.fromDescParameters("([BII)V").get(1));
    assertEquals(JavaType.INT, JavaType.fromDescParameters("([BII)V").get(2));

    assertEquals(JavaType.INT.toArray(), JavaType.fromDescParameters("(I[I)V").get(1));
  }

}
