package com.google.test.metric;

import junit.framework.TestCase;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;

/**
 * Tests for the JavaClassRepository
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JavaClassRepositoryTest extends TestCase {

  ClassPath cp = new ClassPathFactory().createFromJVM();
  ClassRepository repository = new JavaClassRepository(cp);

  /**
   * This  is a regression test without an assert. The failure condition we are
   * testing for is that the method call takes forever (high polynomial time)
   * in revision 180 and before. So we just want to be sure this test runs
   * in a reasonable amount of time.
   * @throws Exception
   */
  public void testParseFinishes() throws Exception {
    repository.getClass(DeeplyNestedIfStatements.class.getName());
    assertTrue(true);
  }

  private static class DeeplyNestedIfStatements {
    @SuppressWarnings("unused")
	private static void nested(boolean x) {
        int num =
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0) +
          (x ? 1 : 0);
    }
  }
}
