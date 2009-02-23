package com.google.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

/**
 * Test the TE maven plugin
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class TestabilityMojoTest extends AbstractMojoTestCase {
  public void testPluginPOM() throws Exception {
    final File file = getTestFile("src/test/resources/xmlTestability.xml");

    TestabilityExplorerMojo mojo = (TestabilityExplorerMojo) lookupMojo("run", file);
    assertNotNull(mojo);

    File outputDir = (File) getVariableValueFromObject( mojo, "outputDirectory" );
    String resultFile = (String) getVariableValueFromObject( mojo, "resultfile" );
    //assertEquals( new File( getBasedir() + "/target/reports/testability" ), outputDir );

    //mojo.execute();
    //assertTrue(new File(outputDir, resultFile).exists());

  }
}
