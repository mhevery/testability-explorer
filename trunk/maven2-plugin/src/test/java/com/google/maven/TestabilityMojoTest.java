package com.google.maven;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.util.IOUtil;

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

    mojo.execute();
    File outputDir = (File) getVariableValueFromObject( mojo, "outputDirectory" );
    String resultFile = (String) getVariableValueFromObject( mojo, "resultfile" );
    assertEquals(new File(getBasedir() + "/target/testability" ).getAbsolutePath(),
        outputDir.getAbsolutePath());
    File results = new File(outputDir, resultFile);
    assertTrue(results.exists());
    assertTrue(IOUtil.toString(new FileReader(results)).contains("TestabilityExplorerMojo"));
  }
}
