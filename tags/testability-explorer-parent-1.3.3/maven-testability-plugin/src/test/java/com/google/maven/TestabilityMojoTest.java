package com.google.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.IOUtil;

import java.io.File;
import java.io.FileReader;

/**
 * Test the TE maven plugin
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestabilityMojoTest extends AbstractMojoTestCase {
  private TestabilityExplorerMojo mojo;

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    new File("target/testability.xml").delete();
    new File("target/testability/testability.html").delete();
  }

  public void testPluginPomWorksAndOutputsHtmlReportAndXmlReport() throws Exception {
    mojo = lookupMojoFromPom("xmlTestability.xml");
    assertNotNull(mojo);
    mojo.execute();

    File outputDir = (File) getVariableValueFromObject(mojo, "outputDirectory");
    String resultFile = (String) getVariableValueFromObject(mojo, "resultfile");
    File targetDir = (File) getVariableValueFromObject(mojo, "targetDirectory");

    File results = new File(outputDir, resultFile + ".html");
    assertTrue("HTML report should exist: " + results.getAbsolutePath(), results.exists());
    String content = IOUtil.toString(new FileReader(results));
    assertTrue("HTML report content: " + content, content.contains("TestabilityExplorerMojo"));

    results = new File(targetDir, resultFile + ".xml");
    assertTrue("XML report should exist: " + results.getAbsolutePath(), results.exists());
    content = IOUtil.toString(new FileReader(results));
    assertTrue("XML report content: " + content, content.contains("TestabilityExplorerMojo"));
  }

  public void testNonJarProject() throws Exception {
    mojo = lookupMojoFromPom("nonJarProject.xml");
    mojo.execute();
  }

  private TestabilityExplorerMojo lookupMojoFromPom(String pom) throws Exception {
    return (TestabilityExplorerMojo) lookupMojo("testability",
        getTestFile("src/test/resources/" + pom));
  }
}
