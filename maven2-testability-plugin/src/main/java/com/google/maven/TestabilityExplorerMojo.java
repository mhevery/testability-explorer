package com.google.maven;

import com.google.test.metric.*;
import com.google.test.metric.ReportPrinterBuilder.ReportFormat;
import com.google.test.metric.report.*;
import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.apache.maven.project.MavenProject;
import org.codehaus.doxia.site.renderer.SiteRenderer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Executes the Testability Explorer with the specified parameters.
 *
 * @goal testability
 * @description Generates a Testability Report when the site plugin is run.
 * @execute phase="compile"
 * @requiresDependencyResolution compile
 * @requiresProject
 */
public class TestabilityExplorerMojo extends AbstractMavenReport {

  /**
   * The root package to inspect.
   *
   * @parameter expression="."
   */
  private String filter;

  /**
   * The output directory for the intermediate XML report.
   *
   * @parameter expression="${project.build.directory}"
   * @required
   */
  private File targetDirectory;

  /**
   * The output directory for the final HTML report. Note that this parameter is only evaluated if the goal is run
   * directly from the command line or during the default lifecycle. If the goal is run indirectly as part of a site
   * generation, the output directory configured in the Maven Site Plugin is used instead.
   *
   * @parameter expression="${project.reporting.outputDirectory}"
   * @required
   */
  private File outputDirectory;

  /**
   * Filename of the output file, without the extension
   *
   * @parameter default-value="testability"
   */
  private String resultfile = "testability";

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
   * Set the output format type, in addition to the HTML report.  Must be one of: "xml",
   * "summary", "source", "detail".
   * XML is required if the testability:check goal is being used.
   *
   * @parameter expression="${format}" default-value="xml"
   */
  private String format = "xml";

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  private MavenProject mavenProject;

  /**
   * @component
   */
  private SiteRenderer siteRenderer;

  private static final String BUNDLE_NAME = "testability";
  private static final String NAME_KEY = "report.testability.name";
  private static final String DESCRIPTION_KEY = "report.testability.description";

  protected SiteRenderer getSiteRenderer() {
    return siteRenderer;
  }

  protected String getOutputDirectory() {
    return outputDirectory.getAbsolutePath();
  }

  protected MavenProject getProject() {
    return mavenProject;
  }

  protected void executeReport(Locale locale) throws MavenReportException {
    if ("pom".equals(mavenProject.getPackaging())) {
      getLog().info(String.format("Not running testability explorer for project %s " +
          "because it is a \"pom\" packaging", mavenProject.getName()));
      return;
    }

    ClassPath classPath = new ClassPathFactory().createFromPath(
        mavenProject.getBuild().getOutputDirectory());
    WhiteList packageWhiteList = new RegExpWhiteList(whiteList);
    List<String> entries = Arrays.asList(filter);
    ReportOptions reportOptions = setOptions();
    Report report = new ReportPrinterBuilder(classPath, reportOptions, ReportFormat.html,
        getResultPrintStream(ReportFormat.html), entries).build();
    if (!"html".equals(format)) {
      PrintStream resultPrintStream = getResultPrintStream(ReportFormat.valueOf(format));
      Report otherReport = new ReportPrinterBuilder(classPath, reportOptions,
          ReportFormat.valueOf(format), resultPrintStream, entries).build();
      report = new MultiReport(null, report, otherReport);
    }
    JavaTestabilityConfig config = new JavaTestabilityConfig(entries, classPath, packageWhiteList,
        report, getErrorPrintStream(), printDepth);
    new JavaTestabilityRunner(config).run();
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

  PrintStream getResultPrintStream(ReportFormat format) {
    File directory = (format == ReportFormat.html ? outputDirectory : targetDirectory);
    if (!directory.exists()) {
      directory.mkdirs();
    }
    try {
      String outFile = resultfile + "." + format.toString();
      return new PrintStream(new FileOutputStream(new File(directory, outFile)));
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
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

  public String getOutputName() {
    return resultfile;
  }

  public String getName(Locale locale) {
    return getProperty(locale, NAME_KEY);
  }

  private String getProperty(Locale locale, String key) {
    return ResourceBundle.getBundle(BUNDLE_NAME, locale).getString(key);
  }

  public String getDescription(Locale locale) {
    return getProperty(locale, DESCRIPTION_KEY);
  }
}
