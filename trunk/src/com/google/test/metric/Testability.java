/*
 * Copyright 2007 Google Inc.
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

import static com.google.classpath.RegExpResourceFilter.ANY;
import static com.google.classpath.RegExpResourceFilter.ENDS_WITH_CLASS;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import com.google.classpath.ClassPath;
import com.google.classpath.ClassPathFactory;
import com.google.classpath.RegExpResourceFilter;
import com.google.test.metric.report.DetailHtmlReport;
import com.google.test.metric.report.DrillDownReport;
import com.google.test.metric.report.GradeCategories;
import com.google.test.metric.report.HtmlReport;
import com.google.test.metric.report.PropertiesReport;
import com.google.test.metric.report.Report;
import com.google.test.metric.report.SourceLinker;
import com.google.test.metric.report.SourceLoader;
import com.google.test.metric.report.SourceReport;
import com.google.test.metric.report.TextReport;
import com.google.test.metric.report.XMLReport;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

public class Testability {

  @Option(name = "-cp", usage = "colon delimited classpath to analyze (jars or directories)"
      + "\nEx. lib/one.jar:lib/two.jar")
  protected String cp = System.getProperty("java.class.path", ".");

  @Option(name = "-printDepth", usage = "Maximum depth to recurse and print costs of classes/methods "
      + "that the classes under analysis depend on. Defaults to 0.")
  int printDepth = 2;

  @Option(name = "-minCost", usage = "Minimum Total Class cost required to print that class' metrics.")
  int minCost = 1;

  @Option(name = "-maxExcellentCost", usage = "Maximum Total Class cost to be classify it as 'excellent'.")
  int maxExcellentCost = 50;

  @Option(name = "-worstOffenderCount", usage = "Print N number of worst offending classes.")
  public int worstOffenderCount = 20;

  @Option(name = "-maxAcceptableCost", usage = "Maximum Total Class cost to be classify it as 'acceptable'.")
  int maxAcceptableCost = 100;

  @Option(name = "-whitelist", usage = "colon delimited whitelisted packages that will not "
      + "count against you. Matches packages/classes starting with "
      + "given values. (Always whitelists java.*. RegExp OK.)")
  String wl = null;
  private RegExpWhiteList whitelist = new RegExpWhiteList();

  @Option(name = "-print", usage = "summary: (default) print package summary information.\n"
      + "html: print package summary information in html format.\n"
      + "source: write out annotated source into directory.\n"
      + "detail: print detail drill down information for each method call.\n"
      + "xml: print computer readable XML format.")
  String printer = "summary";

  @Option(name = "-srcFileLineUrl",
      usage = "template for urls linking to a specific LINE in a file in a web " +
          "source code repository.\n" +
          "Ex. -srcFileLineUrl http://code.repository/basepath/{path}&line={line}\n" +
          "Ex. -srcFileLineUrl http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}#{line}")
  String srcFileLineUrl = "";

  @Option(name = "-srcFileUrl",
      usage = "template for urls linking to a file in a web source code " +
         "repository.\nEx. -srcFileUrl http://code.repository/basepath/{path}\n" +
         "Ex. -srcFileUrl http://code.google.com/p/testability-explorer/source/browse/trunk/src/{path}")
  String srcFileUrl = "";

  @Option(name = "-maxMethodCount", usage = "max number of methods to print in html summary")
  int maxMethodCount = 10;

  @Option(name = "-maxLineCount", usage = "max number of lines in method to print in html summary")
  int maxLineCount = 10;

  @Option(name = "-cyclomatic", metaVar = "cyclomatic cost multiplier", usage = "When computing the overall cost of the method the "
      + "individual costs are added using weighted average. "
      + "This represents the weight of the cyclomatic cost.")
  double cyclomaticMultiplier = 1;

  @Option(name = "-global", metaVar = "global state cost multiplier", usage = "When computing the overall cost of the method the "
      + "individual costs are added using weighted average. "
      + "This represents the weight of the global state cost.")
  double globalMultiplier = 10;

  @Argument(metaVar = "classes and packages", usage = "Classes or packages to analyze. "
      + "Matches any class starting with these.\n"
      + "Ex. com.example.analyze.these com.google.and.these.packages "
      + "com.google.AClass", required = true)
  protected List<String> entryList = new ArrayList<String>();
  protected ClassPath classpath;

  private final PrintStream out;
  private final PrintStream err;

  private Report report;

  public Testability(PrintStream out, PrintStream err) {
    this.out = out;
    this.err = err;
    this.whitelist.addPackage("java.");
  }

  public static void main(String... args) {
    main(System.out, System.err, args);
  }

  public static void main(PrintStream out, PrintStream err, String... args) {
    Testability testability = new Testability(out, err);
    try {
      testability.parseArgs(args);
      testability.execute();
    } catch (CmdLineException ignored) {
    }
  }

  public void parseArgs(String... args) throws CmdLineException {
    CmdLineParser parser = new CmdLineParser(this);
    try {
      parser.parseArgument(args);
      if (entryList.isEmpty()) {
        throw new CmdLineException("No argument was given");
      } else {
        for (int i = 0; i < entryList.size(); i++) {
          entryList.set(i, entryList.get(i).replaceAll("/", "."));
        }
      }
    } catch (CmdLineException e) {
      err.println(e.getMessage() + "\n");
      parser.setUsageWidth(120);
      parser.printUsage(err);
      throw new CmdLineException("Exiting...");
    }
  }

  private void postParse() throws CmdLineException {

    ensureEntryListIsNotEmpty();

    whitelist = getWhiteList();
    classpath = getClassPath();
    report = getReportPrinter();
  }

  private Report getReportPrinter() throws CmdLineException {
    CostModel costModel = new CostModel(cyclomaticMultiplier, globalMultiplier);
    if (printer.equals("summary")) {
      report = new TextReport(out, costModel, maxExcellentCost,
          maxAcceptableCost, worstOffenderCount);
    } else if (printer.equals("html")) {
      SourceLinker linker = new SourceLinker(srcFileLineUrl, srcFileUrl);
      DetailHtmlReport detailHtmlReport = new DetailHtmlReport(out, costModel,
          linker, maxMethodCount, maxLineCount);
      report = new HtmlReport(out, costModel, maxExcellentCost,
          maxAcceptableCost, worstOffenderCount, detailHtmlReport);
    } else if (printer.equals("detail")) {
      report = new DrillDownReport(out, costModel, entryList, printDepth,
          minCost);
    } else if (printer.equals("props")) {
      report = new PropertiesReport(out, costModel, maxExcellentCost,
          maxAcceptableCost, worstOffenderCount);
    } else if (printer.equals("source")) {
      GradeCategories gradeCategories = new GradeCategories(maxExcellentCost,
          maxAcceptableCost);
      SourceLoader sourceLoader = new SourceLoader(classpath);
      report = new SourceReport(gradeCategories, sourceLoader, new File(
          "te-report"), costModel, new Date(), worstOffenderCount);
    } else if (printer.equals("xml")) {
      XMLSerializer xmlSerializer = new XMLSerializer();
      xmlSerializer.setOutputByteStream(out);
      OutputFormat format = new OutputFormat();
      format.setIndenting(true);
      xmlSerializer.setOutputFormat(format);
      report = new XMLReport(xmlSerializer, costModel, maxExcellentCost,
          maxAcceptableCost, worstOffenderCount);
    } else {
      throw new CmdLineException("Don't understand '-print' option '" + printer
          + "'");
    }

    return report;
  }

  private void ensureEntryListIsNotEmpty() {
    if (entryList.isEmpty()) {
      entryList.add("");
    }
  }

  private RegExpWhiteList getWhiteList() {
    for (String packageName : wl == null ? new String[] {} : wl.split("[,:]")) {
      whitelist.addPackage(packageName);
    }

    return whitelist;
  }

  private ClassPath getClassPath() {
    return new ClassPathFactory().createFromPath(cp);
  }

  public Report execute() throws CmdLineException {
    postParse();
    ClassRepository repository = new JavaClassRepository(classpath);
    MetricComputer computer = new MetricComputer(repository, err, whitelist,
        printDepth);
    SortedSet<String> classNames = new TreeSet<String>();
    RegExpResourceFilter resourceFilter = new RegExpResourceFilter(ANY,
        ENDS_WITH_CLASS);
    for (String entry : entryList) {
      if (entry.equals(".")) {
        entry = "";
      }
      classNames.addAll(asList(classpath.findResources(entry.replace(".", "/"),
          resourceFilter)));
    }
    report.printHeader();
    for (String resource : classNames) {
      String className = resource.replace(".class", "").replace("/", ".");
      try {
        if (!whitelist.isClassWhiteListed(className)) {
          ClassInfo clazz = repository.getClass(className);
          ClassCost classCost = computer.compute(clazz);
          report.addClassCost(classCost);
        }
      } catch (ClassNotFoundException e) {
        err.println("WARNING: can not analyze class '" + className
            + "' since class '" + e.getClassName() + "' was not found.");
      }
    }
    report.printFooter();
    return report;
  }
}
