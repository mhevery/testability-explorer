package com.google.test.metric;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.kohsuke.args4j.CmdLineException;

import com.google.test.metric.TestabilityTest.WatchedOutputStream;
import com.google.test.metric.report.DrillDownReport;
import com.google.test.metric.report.html.HtmlReport;
import com.google.test.metric.report.PropertiesReport;
import com.google.test.metric.report.SourceReport;
import com.google.test.metric.report.TextReport;
import com.google.test.metric.report.XMLReport;

public class CommandLineConfigTest extends AutoFieldClearTestCase {

  private WatchedOutputStream out = new WatchedOutputStream();
  private WatchedOutputStream err = new WatchedOutputStream();
  private CommandLineConfig commandLineConfig;

  public void setUp() {
    commandLineConfig = new CommandLineConfig(new PrintStream(out), new PrintStream(err));
  }

  public void testCreateSummaryReport2() throws Exception {
    commandLineConfig.printer = "summary"; 
    JavaTestabilityConfig config = commandLineConfig.buildTestabilityConfig();
    assertEquals(TextReport.class, config.getReport().getClass());
  }

  public void testCreateHtmlReport() throws Exception {
    commandLineConfig.printer = "html"; 
    JavaTestabilityConfig config = commandLineConfig.buildTestabilityConfig();
    assertEquals(HtmlReport.class, config.getReport().getClass());
  }

  public void testCreateDetailReport() throws Exception {
    commandLineConfig.printer = "detail"; 
    JavaTestabilityConfig config = commandLineConfig.buildTestabilityConfig();
    assertEquals(DrillDownReport.class, config.getReport().getClass());
  }
  
  public void testCreatePropertiesReport() throws Exception {
    commandLineConfig.printer = "props"; 
    JavaTestabilityConfig config = commandLineConfig.buildTestabilityConfig();
    assertEquals(PropertiesReport.class, config.getReport().getClass());
  }

  public void testCreateSourceReport() throws Exception {
    commandLineConfig.printer = "source"; 
    JavaTestabilityConfig config = commandLineConfig.buildTestabilityConfig();
    assertEquals(SourceReport.class, config.getReport().getClass());
  }

  public void testCreateXmlReport() throws Exception {
    commandLineConfig.printer = "xml"; 
    JavaTestabilityConfig config = commandLineConfig.buildTestabilityConfig();
    assertEquals(XMLReport.class, config.getReport().getClass());
  }

  public void testCreateNonexistantReportThrowsException() throws Exception {
    commandLineConfig.printer = "i-dont-exist";
    try {
      commandLineConfig.buildTestabilityConfig();
      fail("CmdLineException exception expected but did not get thrown");
    } catch (CmdLineException expected) {
      assertTrue(expected.getMessage().startsWith("Don't understand"));
    }
  }
  
  
  public void testBuildTestabilityConfig() throws Exception {
    PrintStream errStream = new PrintStream(new WatchedOutputStream());
    commandLineConfig = new CommandLineConfig(null, errStream);
    commandLineConfig.entryList = Arrays.asList("com.example.io", "com.example.ext");
    commandLineConfig.cp = "fake/path";
    commandLineConfig.printDepth = 3;
    commandLineConfig.printer = "summary";
    commandLineConfig.wl = "com.foo:org.bar";
    JavaTestabilityConfig testabilityConfig = commandLineConfig.buildTestabilityConfig();
    assertEquals(2, testabilityConfig.getEntryList().size());
    
    assertTrue(testabilityConfig.getWhitelist().isClassWhiteListed("com.foo.Hash"));
    assertTrue(testabilityConfig.getWhitelist().isClassWhiteListed("org.bar.BiMap"));
    assertTrue(testabilityConfig.getWhitelist().isClassWhiteListed("java.lang"));
    assertFalse(testabilityConfig.getWhitelist().isClassWhiteListed("com.example"));
    
    assertEquals(TextReport.class, testabilityConfig.getReport().getClass());
    
    assertEquals(errStream, testabilityConfig.getErr());
    
    assertEquals(3, testabilityConfig.getPrintDepth());
    
    assertNotNull(testabilityConfig.getClassPath());
  }
  
  @SuppressWarnings("serial")
  public void testConvertEntryListValues() throws Exception {
    commandLineConfig.entryList = 
        new ArrayList<String>() {{ add("com/example/one com/example/two"); }};
    JavaTestabilityConfig config = commandLineConfig.buildTestabilityConfig();
    assertEquals(Arrays.<String>asList("com.example.one", "com.example.two"), config.getEntryList());
  }
   
}
