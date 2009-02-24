package com.google.maven;

import com.google.test.metric.*;
import com.google.test.metric.ReportPrinterBuilder.ReportFormat;
import com.google.test.metric.report.*;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

/**
 * Executes the Testability Explorer with the specified parameters.
 *
 * @requiresDependencyResolution
 * @goal run
 * @phase test
 */
public class TestabilityExplorerMojo extends AbstractMojo {

  /**
   * The root package to inspect.
   *
   * @parameter
   * @required
   */
  private String filter;

  /**
   * Location where generated reports will be created.
   *
   * @parameter default-value="${project.build.directory}/testability"
   * @required
   */
  private File outputDirectory;

  /**
   * Filename of the output file
   *
   * @parameter
   */
  private String resultfile;

  /**
   * Where to write errors from execution
   *
   * @parameter
   */
  private File errorfile;

  /**
   * Weight of cyclomatic complexity cost
   *
   * @parameter default-value=1
   */
  private Integer cyclomatic;

  /**
   * Weight of global state cost
   *
   * @parameter default-value=10
   */
  private Integer global;

  /**
   * Maximum recursion depth of printed result
   *
   * @parameter default-value=2
   */
  private Integer printDepth;

  /**
   * Minimum cost to print a class metrics
   *
   * @parameter default-value=1
   */
  private Integer minCost;

  /**
   * Max cost for a class to be called excellent
   *
   * @parameter default-value=50
   */
  private Integer maxExcellentCost;

  /**
   * Max cost for a class to be called acceptable
   *
   * @parameter default-value=100
   */
  private Integer maxAcceptableCost;

  /**
   * Print this many of the worst classes
   *
   * @parameter default-value=20
   */
  private Integer worstOffenderCount;

  /**
   * Colon-delimited packages to whitelist
   *
   * @parameter default-value=" "
   */
  private String whiteList;

  /**
   * The type of report to create
   *
   * @parameter default-value="xml"
   */
  private String print;

  /**
   * @parameter default-value="${project}"
   * @required
   * @readonly
   */
  private MavenProject mavenProject;

  public void execute() throws MojoExecutionException, MojoFailureException {
    try {
      List<String> pathElements = mavenProject.getRuntimeClasspathElements();
      String[] paths = pathElements.toArray(new String[pathElements.size()]);
      ClassPath classPath = new ClassPathFactory().createFromPaths(paths);
      WhiteList packageWhiteList = new RegExpWhiteList(whiteList);
      List<String> entries = Arrays.asList(filter);
      Report report = new ReportPrinterBuilder(classPath, setOptions(), ReportFormat.valueOf(print),
          getResultPrintStream(), entries).build();
      TestabilityConfig config = new TestabilityConfig(entries, classPath, packageWhiteList,
          report, getErrorPrintStream(), printDepth);
      getLog().info("Running testability explorer");
      new TestabilityRunner(config).run();
    } catch (DependencyResolutionRequiredException e) {
      e.printStackTrace();
    }
  }

  private ReportOptions setOptions() {
    ReportOptions options = new ReportOptions();
    options.setCyclomaticMultiplier(cyclomatic);
    options.setGlobalMultiplier(global);
    options.setPrintDepth(printDepth);
    options.setMinCost(minCost);
    options.setWorstOffenderCount(worstOffenderCount);
    options.setMaxAcceptableCost(maxAcceptableCost);
    options.setMaxExcellentCost(maxExcellentCost);
    return options;
  }

  PrintStream getResultPrintStream() {
    if (resultfile != null) {
      try {
        if (!outputDirectory.exists()) {
          outputDirectory.mkdir();
        }
        return new PrintStream(new FileOutputStream(new File(outputDirectory, resultfile)));
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return System.out;
  }

  PrintStream getErrorPrintStream() {
    if (errorfile != null && errorfile.exists()) {
      try {
        return new PrintStream(new FileOutputStream(errorfile));
      } catch (FileNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return System.err;
  }
}