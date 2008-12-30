package com.google.test.metric.report;

import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.Reader;
import java.io.StringReader;

/**
 * Tests for {@link XMLReportLoader}.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class XMLReportLoaderTest extends TestCase {
  private XMLReportLoader loader;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    loader = new XMLReportLoader();
  }

  public void testReadBlank() throws Exception {
    try {
      loader.loadXML(new StringReader(""));
      fail("Should throw exception on empty document");
    } catch (SAXException e) {
      // expected
    }
  }

  public void testSingleClass() throws Exception {
    Reader in = new StringReader("<class></class>");
    Document classCosts = loader.loadXML(in);
    assertEquals(1, classCosts.getChildNodes().getLength());
    assertEquals("class", classCosts.getChildNodes().item(0).getNodeName());
  }

  public void testClassWithCost() throws Exception {
    Reader in = new StringReader("<class cost=\"12\" class=\"com.google.Foobar\"></class>");
    Document classCosts = loader.loadXML(in);
    assertEquals("12", classCosts.getElementsByTagName("class").
        item(0).getAttributes().getNamedItem("cost").getNodeValue());
    assertEquals("com.google.Foobar", classCosts.getElementsByTagName("class").
            item(0).getAttributes().getNamedItem("class").getNodeValue());

  }

  public void testMethods() throws Exception {
    Reader in = new StringReader("<class cost=\"12\">" +
        "<method cyclomatic=\"2\" global=\"2\" line=\"123\" "
        + "lod=\"0\" name=\"methodName\" overall=\"22\"></method></class>");
    Document classCosts = loader.loadXML(in);
    assertEquals("22", classCosts.getElementsByTagName("method").
        item(0).getAttributes().getNamedItem("overall").getNodeValue());
    assertEquals("methodName", classCosts.getElementsByTagName("method").
            item(0).getAttributes().getNamedItem("name").getNodeValue());
    assertEquals("2", classCosts.getElementsByTagName("method").
            item(0).getAttributes().getNamedItem("cyclomatic").getNodeValue());
    assertEquals("123", classCosts.getElementsByTagName("method").
            item(0).getAttributes().getNamedItem("line").getNodeValue());
    assertEquals("2", classCosts.getElementsByTagName("method").
            item(0).getAttributes().getNamedItem("global").getNodeValue());

  }
}
