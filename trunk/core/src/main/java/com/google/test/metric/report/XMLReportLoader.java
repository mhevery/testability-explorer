package com.google.test.metric.report;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.Reader;

/**
 * Read an XML report into a DOM Document.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class XMLReportLoader {
  public Document loadXML(Reader reader) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = factory.newDocumentBuilder();
    return documentBuilder.parse(new InputSource(reader));
  }
}
