package com.google.test.metric.report;

import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import com.google.test.metric.ReportGeneratorBuilder;

import freemarker.template.Configuration;

/**
 * Tests for HTML generation of the Diff report
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class DiffReportTest extends TestCase {
  private Writer out;
  private Configuration cfg;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    out = new StringWriter();
    cfg = new Configuration();
    cfg.setTemplateLoader(new ClassPathTemplateLoader(ReportGeneratorBuilder.PREFIX));
  }

  @SuppressWarnings("unchecked")
  public void testNoDiffs() throws Exception {
    Diff diff = new Diff(Collections.EMPTY_LIST);
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("<html>", out.toString());
  }

  public void testSourceLinks() throws Exception {
    Diff diff = new Diff(Arrays.asList(new Diff.ClassDiff("com.Foo", 123, 456)));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.setOldSourceUrl("link1/{path}.html");
    diffReport.setNewSourceUrl("link2/{path}.html");
    diffReport.writeHtml(out);
    assertContains("link1/com.Foo.html", out.toString());
    assertContains("link2/com.Foo.html", out.toString());

  }

  public void testClassCostIncreased() throws Exception {
    Diff diff = new Diff(Arrays.asList(new Diff.ClassDiff("Foo", 123, 456)));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("Foo", out.toString());
    assertContains("123", out.toString());
    assertContains("456", out.toString());
    assertContains("class=\"worse", out.toString());

  }

  public void testClassCostDecreased() throws Exception {
    Diff diff = new Diff(Arrays.asList(new Diff.ClassDiff("Foo", 456, 123)));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("Foo", out.toString());
    assertContains("123", out.toString());
    assertContains("456", out.toString());
    assertContains("class=\"better", out.toString());
  }

  public void testClassRemoved() throws Exception {
    Diff diff = new Diff(Arrays.asList(new Diff.ClassDiff("Foo", 456, null)));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("Foo", out.toString());
    assertContains("456", out.toString());
    assertContains("class=\"better", out.toString());
  }

  public void testClassAdded() throws Exception {
    Diff diff = new Diff(Arrays.asList(new Diff.ClassDiff("Foo", null, 456)));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("Foo", out.toString());
    assertContains("456", out.toString());
    assertContains("class=\"worse", out.toString());
  }

  public void testMethodRemoved() throws Exception {
    Diff.MethodDiff methodDiff = new Diff.MethodDiff("doThing", 123, null);
    Diff.ClassDiff classDiff = new Diff.ClassDiff("Foo", 1, 1, Arrays.asList(methodDiff));
    Diff diff = new Diff(Arrays.asList(classDiff));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("doThing", out.toString());
    assertContains("123", out.toString());
    assertContains("class=\"better", out.toString());
  }

  public void testMethodAdded() throws Exception {
    Diff.MethodDiff methodDiff = new Diff.MethodDiff("doThing", null, 123);
    Diff.ClassDiff classDiff = new Diff.ClassDiff("Foo", 1, 1, Arrays.asList(methodDiff));
    Diff diff = new Diff(Arrays.asList(classDiff));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("doThing", out.toString());
    assertContains("123", out.toString());
    assertContains("class=\"worse", out.toString());
  }

  public void testMethodCostIncreased() throws Exception {
    Diff.MethodDiff methodDiff = new Diff.MethodDiff("doThing", 123, 456);
    Diff.ClassDiff classDiff = new Diff.ClassDiff("Foo", 1, 1, Arrays.asList(methodDiff));
    Diff diff = new Diff(Arrays.asList(classDiff));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("doThing", out.toString());
    assertContains("123", out.toString());
    assertContains("456", out.toString());
    assertContains("class=\"worse", out.toString());
  }

  public void testMethodCostDecreased() throws Exception {
    Diff.MethodDiff methodDiff = new Diff.MethodDiff("doThing", 456, 123);
    Diff.ClassDiff classDiff = new Diff.ClassDiff("Foo", 1, 1, Arrays.asList(methodDiff));
    Diff diff = new Diff(Arrays.asList(classDiff));
    DiffReport diffReport = new DiffReport(diff, cfg);
    diffReport.writeHtml(out);
    assertContains("doThing", out.toString());
    assertContains("123", out.toString());
    assertContains("456", out.toString());
    assertContains("class=\"better", out.toString());
  }

  private void assertContains(String expected, String actual) {
    assertTrue(String.format("[%s] expected to contain [%s]", actual, expected),
        actual.contains(expected));
  }
}
