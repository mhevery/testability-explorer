package com.google.test.metric;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.test.metric.report.ReportGenerator;
import com.google.test.metric.report.TextReportGenerator;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

public class TestabilityRunnerTest extends AutoFieldClearTestCase {
  /**
   * Directories to be used for testing that contains class files, for testing.
   * These are included in subversion so that any checkout will have a consistent
   * environment for testing.
   */
  public static final String CLASSES_FOR_TEST = "src/test/classes";

  /**
   * Directory root that contains one class with no external
   * dependencies.
   */
  public static final String CLASS_NO_EXTERNAL_DEPS = CLASSES_FOR_TEST + "/root1";

  /**
   * Directory root containing classes that extend from, and reference, external
   * classes outside of this directory.
   */
  public static final String CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES = CLASSES_FOR_TEST + "/root2";

  /**
   * Directory root containing classes extending from Object that reference
   * external classes outside of this directory.
   */
  public static final String CLASSES_EXTERNAL_DEPS_NO_SUPERCLASSES = CLASSES_FOR_TEST + "/root3";

  private ByteArrayOutputStream out = new ByteArrayOutputStream();
  private ByteArrayOutputStream err = new ByteArrayOutputStream();
  private List<String> allEntryList = Arrays.asList("");
  private ReportGenerator report = new TextReportGenerator(new PrintStream(out), new CostModel(), 0, 0, 0);
  private RegExpWhiteList whiteList = new RegExpWhiteList("java.");
  private PrintStream errStream = new PrintStream(err);

  public void testClassesNotInClasspath() throws Exception {
    JavaTestabilityRunner runner = runnerFor(CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES);
    runner.run();
    assertTrue(out.toString().length() > 0);
    final String errStr = err.toString();
    assertTrue(errStr, errStr.length() > 0);
    assertTrue(errStr, errStr.startsWith("WARNING: can not analyze class "));
    assertTrue(errStr, errStr.contains("'com.google.test.metric.ClassInfoTest'"));
    assertTrue(errStr, errStr.contains("'com.google.test.metric.x.SelfTest'"));
    assertTrue(errStr, errStr.contains("'com.google.test.metric.ClassRepositoryTestCase'"));
  }

  /*
   * The given classpath contains some classes from this project, but not all.
   * There are many references to classes that will not be in this test's -cp
   * classpath. This test verifies that when the ClassRepository encounters a
   * ClassNotFoundException, it continues nicely and prints the values for the
   * classes that it <em>does</em> find.
   */
  public void testIncompleteClasspath() throws Exception {
    JavaTestabilityRunner runner = runnerFor(CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES);
    runner.run();
    assertTrue(out.toString(), out.toString().length() > 0);
    assertTrue(err.toString(), err.toString().length() > 0);
  }

  /*
   * Tries calculating the cost for classes that reference other classes not in
   * the classpath.
   */
  public void testForWarningWhenClassesRecurseToIncludeClassesOutOfClasspath()
      throws Exception {
    JavaTestabilityRunner runner = runnerFor(CLASSES_EXTERNAL_DEPS_NO_SUPERCLASSES);
    runner.run();

    assertTrue(out.toString(), out.toString().length() > 0);
    assertTrue(err.toString(), err.toString().length() > 0);
    assertTrue(err.toString(), err.toString().startsWith("WARNING: class not found: "));
  }

  /*
   * Tries calculating the cost for classes that extend from another class,
   * which does not exist in the classpath.
   */
  public void testForWarningWhenClassExtendsFromClassOutOfClasspath()
      throws Exception {
    JavaTestabilityRunner runner = runnerFor(CLASSES_EXTERNAL_DEPS_AND_SUPERCLASSES);
    runner.run();

    assertTrue(out.toString().length() > 0);
    assertTrue(err.toString().length() > 0);
    assertTrue(err.toString().startsWith("WARNING: can not analyze class "));
  }


  private JavaTestabilityRunner runnerFor(String path) {
    ClassPath classPath = new ClassPathFactory().createFromPaths(path, "core/" + path);
    ClassRepository classRepository = new JavaClassRepository(classPath);
    MetricComputer computer = new MetricComputer(classRepository, errStream, whiteList, 0);
    return new JavaTestabilityRunner(report, classPath, classRepository, computer,
        allEntryList, whiteList, new PrintStream(err));
  }

}
