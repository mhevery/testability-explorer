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
    Issue issue1 = new Issue(13, "myMethod1");
    Issue issue2 = new Issue(25, "myMethod2");
    issues.add(issue1);
    issues.add(issue2);
    IssuesToAnnotationTranslator translator = new IssuesToAnnotationTranslator();
    IDocument document = createMock(IDocument.class);
    IRegion region1 = createMock(IRegion.class);
    IRegion region2 = createMock(IRegion.class);
    expect(region1.getOffset()).andStubReturn(111);
    expect(region1.getLength()).andStubReturn(111);
    expect(region2.getOffset()).andStubReturn(111);
    expect(region2.getLength()).andStubReturn(111);
    expect(document.getLineInformation(issue1.getLineNumber()))
        .andReturn(region1);
    expect(document.getLineInformation(issue2.getLineNumber()))
        .andReturn(region2);
    replay(region1, region2, document);
    List<Annotation> annotations =
        translator.getAnnotations(issues, document);
    
    assertNotNull(annotations);
    assertEquals(2, annotations.size());
    verify(region1, region2, document);
  }
}
