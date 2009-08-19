package com.google.test.metric.report;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.List;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Represents a report on the difference between two reports, suitable for
 * rendering. Also knows how to render itself using Freemarker.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class DiffReport {
  private Configuration cfg;
  private final Diff diff;
  private String oldSourceUrl;
  private String newSourceUrl;
  private String changelistUrl;

  public DiffReport(Diff diff, Configuration cfg) {
    this.diff = diff;
    diff.sort();
    this.cfg = cfg;
    
  }

  public void writeHtml(Writer out) throws IOException, TemplateException {
    Template template = cfg.getTemplate("diff.html");
    template.process(this, out);
  }

  public List<Diff.ClassDiff> getClassDiffs() {
    return diff.getClassDiffs();
  }

  public Date getCurrentTime() {
    return new Date();
  }

  public String getOldSourceUrl() {
    return oldSourceUrl;
  }

  public String getNewSourceUrl() {
    return newSourceUrl;
  }

  public void setOldSourceUrl(String oldSourceUrl) {
    this.oldSourceUrl = oldSourceUrl;
  }

  public void setNewSourceUrl(String newSourceUrl) {
    this.newSourceUrl = newSourceUrl;
  }

  public String getChangelistUrl() {
    return changelistUrl;
  }

  public void setChangelistUrl(String changelistUrl) {
    this.changelistUrl = changelistUrl;
  }

}
