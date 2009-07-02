package com.google.test.metric.report.issues;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Supplier;

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
