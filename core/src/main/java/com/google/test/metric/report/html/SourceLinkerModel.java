package com.google.test.metric.report.html;

import freemarker.template.TemplateModelException;
import freemarker.template.TemplateMethodModel;
import com.google.test.metric.report.SourceLinker;

import java.util.List;

/**
 * Wrap the {@link com.google.test.metric.report.SourceLinker#buildLineLink(String, int, String)}
 * method as a Freemarker method.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class SourceLinkerModel implements TemplateMethodModel {
  private final SourceLinker linker;

  public SourceLinkerModel(SourceLinker linker) {
    this.linker = linker;
  }

  public Object exec(List arguments) throws TemplateModelException {
    String file = (String) arguments.get(0);
    int lineNumber = Integer.parseInt((String) arguments.get(1));
    String anchorText = (String) arguments.get(2);
    return linker.buildLineLink(file, lineNumber, anchorText);
  }
}
