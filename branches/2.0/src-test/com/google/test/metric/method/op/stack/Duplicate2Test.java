package com.google.test.metric.method.op.stack;

import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.Constant;

import junit.framework.TestCase;

import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;

public class Duplicate2Test extends TestCase {

  public void testDup2Single() throws Exception {
    Duplicate2 dup2 = new Duplicate2(1, 0);
    assertEquals(2, dup2.getOperatorCount());
    Variable v1 = var(1);
    Variable v2 = var(2);
    List<Variable> out = dup2.apply(asList(v1, v2));
    assertList(out, v1, v2, v1, v2);
    assertEquals("duplicate2", dup2.toString());
  }

  public void testDup2Double() throws Exception {
    Duplicate2 dup2 = new Duplicate2(1, 0);
    assertEquals(2, dup2.getOperatorCount());
    Variable v1 = var(1l);
    List<Variable> out = dup2.apply(asList(v1, v1));
    assertList(out, v1, v1, v1, v1);
    assertEquals("duplicate2", dup2.toString());
  }

  public void testDup2Offset1Single() throws Exception {
    Duplicate2 dup2 = new Duplicate2(1, 1);
    assertEquals(3, dup2.getOperatorCount());
    Variable v1 = var(1);
    Variable v2 = var(2);
    Variable v3 = var(3);
    List<Variable> out = dup2.apply(asList(v3, v2, v1));
    assertList(out, v2, v1, v3, v2, v1);
    assertEquals("duplicate2_X1", dup2.toString());
  }

  public void testDup2Offset1Double() throws Exception {
    Duplicate2 dup2 = new Duplicate2(1, 1);
    assertEquals(3, dup2.getOperatorCount());
    Variable v1 = var(1l);
    Variable v2 = var(2);
    List<Variable> out = dup2.apply(asList(v2, v1, v1));
    assertList(out, v1, v1, v2, v1, v1);
    assertEquals("duplicate2_X1", dup2.toString());
  }

  public void testDup2Offset2Single() throws Exception {
    Duplicate2 dup2 = new Duplicate2(1, 2);
    assertEquals(4, dup2.getOperatorCount());
    Variable v1 = var(1);
    Variable v2 = var(2);
    Variable v3 = var(3);
    Variable v4 = var(4);
    List<Variable> out = dup2.apply(asList(v4, v3, v2, v1));
    assertList(out, v2, v1, v4, v3, v2, v1);
    assertEquals("duplicate2_X2", dup2.toString());
  }

  public void testDup2Offset3Double() throws Exception {
    Duplicate2 dup2 = new Duplicate2(1, 2);
    assertEquals(4, dup2.getOperatorCount());
    Variable v1 = var(1l);
    Variable v2 = var(2);
    Variable v3 = var(3);
    List<Variable> out = dup2.apply(asList(v3, v2, v1, v1));
    assertList(out, v1, v1, v3, v2, v1, v1);
    assertEquals("duplicate2_X2", dup2.toString());
  }

  private void assertList(List<Variable> actual, Variable... expected) {
    assertEquals(expected.length, actual.size());
    String error = "\nExpecting: " + Arrays.toString(expected)
        + "\n  Actual:" + actual + "\n";
    for (int i = 0; i < expected.length; i++) {
      assertEquals(error, expected[i], actual.get(i));
    }
  }

  private Variable var(Object value) {
    return new Constant(value, Type.fromClass(value.getClass()));
  }

}
