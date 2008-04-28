package com.google.test.metric.method.op.stack;

import com.google.test.metric.ClassRepository;
import com.google.test.metric.MethodInfo;
import com.google.test.metric.method.op.turing.Operation;

import junit.framework.TestCase;

import java.util.Arrays;
import java.util.LinkedList;

public class StackOperationsTest extends TestCase {

  private void assertOperations(Class<?> clazz, String... expectedOps) {
    LinkedList<Operation> actualOps =
        new LinkedList<Operation>(methodForClass(clazz).getOperations());
    assertEquals("java.lang.Object.<init>()V", actualOps.remove(0).toString());
    String error = "\nExpected: " + Arrays.toString(expectedOps)
        + "\n   Actual: " + actualOps;
    assertEquals(error, expectedOps.length, actualOps.size());
    for (String expected : expectedOps) {
      assertEquals(error, expected, actualOps.remove(0).toString());
    }
  }

  private MethodInfo methodForClass(Class<?> clazz) {
    return new ClassRepository().getClass(clazz).getMethod("<init>()V");
  }

  private static class LoadClass {

    {
      this.hashCode();
    }
  }

  public void testLoad() throws Exception {
    assertOperations(LoadClass.class, "java.lang.Object.hashCode()I");
    assertEquals("load null", new Load(-1, null).toString());
  }

  private static class PopClass {

    {
      long a = 1l;
      double b = 2d;
      add(a, b);
    }
  }

  static double add(long a, double b) {
    return 3d;
  }

  public void testPop() throws Exception {
    assertOperations(PopClass.class,
        "a{long} <- 1{long}",
        "b{double} <- 2.0{double}",
        getClass().getName() + ".add(JD)D");
    assertEquals("pop", new Pop(-1, 1).toString());
  }

}
