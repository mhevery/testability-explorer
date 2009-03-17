package com.google.test.metric.report.issues;

import com.google.common.base.Supplier;

import java.util.List;
import java.util.LinkedList;

/**
 * Simple supplier that returns empty lists of issues.
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class IssuesListFactory implements Supplier<List<Issue>> {
  public List<Issue> get() {
    return new LinkedList<Issue>();
  }
}
