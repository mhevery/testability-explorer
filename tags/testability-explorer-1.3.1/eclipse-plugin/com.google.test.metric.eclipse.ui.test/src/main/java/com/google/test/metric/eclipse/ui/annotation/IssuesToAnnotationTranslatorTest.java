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
package com.google.test.metric.eclipse.ui.annotation;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import com.google.test.metric.report.ReportOptions;
import com.google.test.metric.report.issues.ClassIssues;
import com.google.test.metric.report.issues.Issue;

import junit.framework.TestCase;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.source.Annotation;

import java.util.LinkedList;
import java.util.List;

/**
 * Tests for {@link IssuesToAnnotationTranslator}.
 * 
 * @author shyamseshadri@google.com (Shyam Seshadri)
 */
public class IssuesToAnnotationTranslatorTest extends TestCase {

  public void testGetAnnotations() throws Exception {
    ClassIssues issues = new ClassIssues("SomeClass", 12, new LinkedList<Issue>());
    Issue issue1 = new Issue(13, null);
    Issue issue2 = new Issue(25, null);
    Issue issue3 = new Issue(7, null);
    issues.add(issue1);
    issues.add(issue2);
    issues.add(issue3);
    ReportOptions options = new ReportOptions();
    options.setMaxAcceptableCost(10);
    options.setMaxExcellentCost(15);
    
    IssuesToAnnotationTranslator translator = new IssuesToAnnotationTranslator(options);
    IDocument document = createMock(IDocument.class);
    IRegion region1 = createMock(IRegion.class);
    expect(region1.getOffset()).andStubReturn(111);
    expect(region1.getLength()).andStubReturn(111);
    expect(document.getLineInformation(issue1.getLineNumber()))
        .andReturn(region1);
    expect(document.getLineInformation(issue2.getLineNumber()))
        .andReturn(region1);
    expect(document.getLineInformation(issue3.getLineNumber()))
        .andReturn(region1);
    replay(region1, document);

    List<Annotation> annotations =
        translator.getAnnotations(issues, document);
    
    assertNotNull(annotations);
    assertEquals(3, annotations.size());
    assertEquals(TestabilityAnnotation.GOOD_TESTABILITY, annotations.get(0).getType());
    assertEquals(TestabilityAnnotation.BAD_TESTABILITY, annotations.get(1).getType());
    assertEquals(TestabilityAnnotation.EXCELLENT_TESTABILITY, annotations.get(2).getType());
    verify(region1, document);
  }
}
