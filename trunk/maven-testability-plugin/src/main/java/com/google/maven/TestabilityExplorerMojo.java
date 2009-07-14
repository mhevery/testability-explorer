package com.google.maven;

import com.google.inject.Guice;
import com.google.test.metric.JavaTestabilityRunner;
import com.google.test.metric.TestabilityModule;

import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.codehaus.doxia.site.renderer.SiteRenderer;

import java.io.File;
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
  String filter;

  /**
   * The output directory for the intermediate XML report.
   *
   * @parameter expression="${project.build.directory}"
   * @required
   */
  File targetDirectory;

  /**
   * The output directory for the final HTML report. Note that this parameter is only evaluated if the goal is run
   * directly from the command line or during the default lifecycle. If the goal is run indirectly as part of a site
   * generation, the output directory configured in the Maven Site Plugin is used instead.
   *
   * @parameter expression="${project.reporting.outputDirectory}"
   * @required
   */
  File outputDirectory;

  /**
   * Filename of the output file, without the extension
   *
   * @parameter default-value="testability"
   */
  String resultfile = "testability";

  /**
   * Where to write errors from execution
   *
   * @parameter
   */
  File errorfile;

  /**
   * Weight of cyclomatic complexity cost
   *
   * @parameter default-value=1
   */
  Integer cyclomatic;

  /**
   * Weight of global state cost
   *
   * @parameter default-value=10
   */
  Integer global;

  /**
   * Maximum recursion depth of printed result
   *
   * @parameter default-value=2
   */
  Integer printDepth;

  /**
   * Minimum cost to print a class metrics
   *
   * @parameter default-value=1
   */
  Integer minCost;

  /**
   * Max cost for a class to be called excellent
   *
   * @parameter default-value=50
   */
  Integer maxExcellentCost;

  /**
   * Max cost for a class to be called acceptable
   *
   * @parameter default-value=100
   */
  Integer maxAcceptableCost;

  /**
   * Print this many of the worst classes
   *
   * @parameter default-value=20
   */
  Integer worstOffenderCount;

  /**
   * Colon-delimited packages to whitelist
   *
   * @parameter default-value=" "
   */
  String whiteList;

  /**
   * Set the output format type, in addition to the HTML report.  Must be one of: "xml",
   * "summary", "source", "detail".
   * XML is required if the testability:check goal is being used.
   *
   * @parameter expression="${format}" default-value="xml"
   */
  String format = "xml";

  /**
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  MavenProject mavenProject;

  /**
   * @component
   */
  private SiteRenderer siteRenderer;

  private static final String BUNDLE_NAME = "testability";
  private static final String NAME_KEY = "report.testability.name";
  private static final String DESCRIPTION_KEY = "report.testability.description";

  @Override
  protected SiteRenderer getSiteRenderer() {
    return siteRenderer;
  }

  @Override
  protected String getOutputDirectory() {
    return outputDirectory.getAbsolutePath();
  }

  @Override
  protected MavenProject getProject() {
    return mavenProject;
  }

  @Override
  protected void executeReport(Locale locale) {
    if ("pom".equals(mavenProject.getPackaging())) {
      getLog().info(String.format("Not running testability explorer for project %s " +
          "because it is a \"pom\" packaging", mavenProject.getName()));
      return;
    }
    Guice.createInjector(new MavenConfigModule(this), new TestabilityModule()).
        getInstance(JavaTestabilityRunner.class).
        run();
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
