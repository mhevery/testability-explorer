/*
 * Copyright 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.test.metric;

import com.google.test.metric.JavaTestabilityModule.JavaWhiteListProvider;
import com.google.test.metric.ReportGeneratorProvider.ReportFormat;

import org.kohsuke.args4j.CmdLineException;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Arrays;

public class CommandLineConfigTest extends AutoFieldClearTestCase {

  private ByteArrayOutputStream out = new ByteArrayOutputStream();
  private ByteArrayOutputStream err = new ByteArrayOutputStream();
  private CommandLineConfig commandLineConfig;

  @Override
  public void setUp() {
    commandLineConfig = new CommandLineConfig(new PrintStream(out), new PrintStream(err));
  }

  public void testCreateSummaryReport2() throws Exception {
    commandLineConfig.printer = "summary";
    commandLineConfig.cp = "a";
    commandLineConfig.validate();
    assertEquals(ReportFormat.summary, commandLineConfig.format);
  }

  public void testCreateHtmlReport() throws Exception {
    commandLineConfig.printer = "html"; 
    commandLineConfig.cp = "a";
    commandLineConfig.validate();
    assertEquals(ReportFormat.html, commandLineConfig.format);
  }

  public void testCreateDetailReport() throws Exception {
    commandLineConfig.printer = "detail"; 
    commandLineConfig.cp = "a";
    commandLineConfig.validate();
    assertEquals(ReportFormat.detail, commandLineConfig.format);
  }
  
  public void testCreatePropertiesReport() throws Exception {
    commandLineConfig.printer = "props"; 
    commandLineConfig.cp = "a";
    commandLineConfig.validate();
    assertEquals(ReportFormat.props, commandLineConfig.format);
  }

  public void testCreateSourceReport() throws Exception {
    commandLineConfig.printer = "source"; 
    commandLineConfig.cp = "a";
    commandLineConfig.validate();
    assertEquals(ReportFormat.source, commandLineConfig.format);
  }

  public void testCreateXmlReport() throws Exception {
    commandLineConfig.printer = "xml"; 
    commandLineConfig.cp = "a";
    commandLineConfig.validate();
    JavaTestabilityModule module = new JavaTestabilityModule(commandLineConfig);
    assertEquals(ReportFormat.xml, module.getFormat());
  }

  public void testCreateNonexistantReportThrowsException() throws Exception {
    commandLineConfig.cp = "";
    commandLineConfig.printer = "i-dont-exist";
    try {
      commandLineConfig.validate();
      fail("CmdLineException exception expected but did not get thrown");
    } catch (CmdLineException expected) {
      assertTrue(expected.getMessage().startsWith("Don't understand"));
    }
  }
  
  public void testBuildTestabilityConfig() throws Exception {
    PrintStream errStream = new PrintStream(new ByteArrayOutputStream());
    commandLineConfig = new CommandLineConfig(null, errStream);
    commandLineConfig.entryList = Arrays.asList("com.example.io", "com.example.ext");
    commandLineConfig.cp = "fake/path";
    commandLineConfig.printDepth = 3;
    commandLineConfig.printer = "summary";
    commandLineConfig.validate();
    JavaTestabilityModule testabilityModule = new JavaTestabilityModule(commandLineConfig);
    assertEquals(2, testabilityModule.getEntryList().size());
    assertEquals(ReportFormat.summary, testabilityModule.getFormat());
    assertEquals(errStream, testabilityModule.getErr());
    assertEquals(3, testabilityModule.getPrintDepth());
  }

  public void testJavaWhitelistProvider() throws Exception {
    commandLineConfig.wl = "com.foo:org.bar";
    JavaWhiteListProvider provider = new JavaWhiteListProvider();
    provider.config = commandLineConfig;
    WhiteList whiteList = provider.get();
    assertTrue(whiteList.isClassWhiteListed("com.foo.Hash"));
    assertTrue(whiteList.isClassWhiteListed("org.bar.BiMap"));
    assertTrue(whiteList.isClassWhiteListed("java.lang"));
    assertFalse(whiteList.isClassWhiteListed("com.example"));
  }
  
}
