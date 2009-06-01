package com.google.test.metric.report;

import com.google.test.metric.ClassCost;
import freemarker.template.TemplateMethodModel;

/**
 * Base class for models that back reports.
 * @author alexeagle@google.com (Alex Eagle)
 */
public abstract class ReportModel {
  private TemplateMethodModel messageBundleModel;
  private TemplateMethodModel sourceLinker;

  public void setMessageBundle(TemplateMethodModel resourceBundleModel) {
    this.messageBundleModel = resourceBundleModel;
  }

  public void setSourceLinker(TemplateMethodModel sourceLinker) {
    this.sourceLinker = sourceLinker;
  }

  public TemplateMethodModel getMessage() {
    return messageBundleModel;
  }

  public TemplateMethodModel getSourceLink() {
    return sourceLinker;
  }

  public abstract void addClassCost(ClassCost classCost);
}
