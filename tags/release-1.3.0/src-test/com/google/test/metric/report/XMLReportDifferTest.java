package com.google.test.metric.report;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;

/**
 * Tests for {@link com.google.test.metric.report.XMLReportDiffer}.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class XMLReportDifferTest extends TestCase {

  public void testDiffSameClass() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<a></a>");
    Document newDoc = makeTestDoc("<a></a>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertTrue(diff.getClassDiffs().isEmpty());
  }

  public void testRemovedClass() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Document newDoc = makeTestDoc("<a></a>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertEquals(1, diff.getClassDiffs().size());
    Diff.ClassDiff diff1 = diff.getClassDiffs().get(0);
    assertEquals("Foo", diff1.getClassName());
    assertEquals(12, diff1.getOldMetric().intValue());
    assertEquals(null, diff1.getNewMetric());
  }

  public void testAddedClass() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<a></a>");
    Document newDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertEquals(1, diff.getClassDiffs().size());
    Diff.ClassDiff diff1 = diff.getClassDiffs().get(0);
    assertEquals("Foo", diff1.getClassName());
    assertEquals(null, diff1.getOldMetric());
    assertEquals(12, diff1.getNewMetric().intValue());
  }

  public void testChangedClass() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<class class='Foo' cost='21'></class>");
    Document newDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertEquals(1, diff.getClassDiffs().size());
    Diff.ClassDiff diff1 = diff.getClassDiffs().get(0);
    assertEquals("Foo", diff1.getClassName());
    assertEquals(21, diff1.getOldMetric().intValue());
    assertEquals(12, diff1.getNewMetric().intValue());
  }

  public void testUnchangedClass() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Document newDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertEquals(0, diff.getClassDiffs().size());
  }

  public void testAddedMethod() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Document newDoc = makeTestDoc("<class class='Foo' cost='12'>" +
        "<method cyclomatic='135' global='10' line='233' lod='1' " +
        " name='execute()' overall='236'/></class>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertEquals(1, diff.getClassDiffs().size());
    Diff.ClassDiff classDiff = diff.getClassDiffs().get(0);
    assertEquals(1, classDiff.getMethodDiffs().size());
    Diff.MethodDiff methodDiff = classDiff.getMethodDiffs().get(0);
    assertEquals("execute()", methodDiff.getMethodName());
    assertEquals(null, methodDiff.getOldMetric());
    assertEquals(236, methodDiff.getNewMetric().intValue());
  }

  public void testRemovedMethod() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<class class='Foo' cost='12'>" +
        "<method cyclomatic='135' global='10' line='233' lod='1' " +
        " name='execute()' overall='236'/></class>");
    Document newDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertEquals(1, diff.getClassDiffs().size());
    Diff.ClassDiff classDiff = diff.getClassDiffs().get(0);
    assertEquals(1, classDiff.getMethodDiffs().size());
    Diff.MethodDiff methodDiff = classDiff.getMethodDiffs().get(0);
    assertEquals("execute()", methodDiff.getMethodName());
    assertEquals(236, methodDiff.getOldMetric().intValue());
    assertEquals(null, methodDiff.getNewMetric());
  }

  public void testChangedMethod() throws Exception {
    XMLReportDiffer differ = new XMLReportDiffer();
    Document oldDoc = makeTestDoc("<class class='Foo' cost='12'>" +
        "<method cyclomatic='135' global='10' line='233' lod='1' " +
        " name='execute()' overall='236'/></class>");
    Document newDoc = makeTestDoc("<class class='Foo' cost='12'></class>");
    Diff diff = differ.diff(oldDoc, newDoc);
    assertEquals(1, diff.getClassDiffs().size());
    Diff.ClassDiff classDiff = diff.getClassDiffs().get(0);
    assertEquals(1, classDiff.getMethodDiffs().size());
    Diff.MethodDiff methodDiff = classDiff.getMethodDiffs().get(0);
    assertEquals("execute()", methodDiff.getMethodName());
    assertEquals(236, methodDiff.getOldMetric().intValue());
    assertEquals(null, methodDiff.getNewMetric());
  }

  private Document makeTestDoc(String content)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(new InputSource(new StringReader(content)));
  }
}
