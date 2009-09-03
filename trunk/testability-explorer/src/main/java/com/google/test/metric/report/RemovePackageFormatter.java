// Copyright 2009 Google Inc. All Rights Reserved.

package com.google.test.metric.report;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class RemovePackageFormatter {
  private final Pattern pattern = Pattern.compile("([0-9A-Z\\$a-z_\\.]*)");

  /**
   * Shorten the name of a method, removing any package and class names of the method
   * and its parameters.
   * "t.n.e(p.e, a.b)" -> "e(e, b)"
   * @param name any identifier, such as a method name or type name
   * @return a shortened version
   */
  public String format(String name) {
    //TODO(alexeagle): make sure this doesn't cause ambiguity
    Matcher matcher = pattern.matcher(name);
    StringBuffer result = new StringBuffer();
    while (matcher.find()) {
      String identifier = matcher.group();
      String simple = identifier.substring(identifier.lastIndexOf(".") + 1);
      // Have to escape dollar signs, or they look like capture group identifiers
      simple = simple.replaceAll("\\$", "\\\\\\$");
      matcher.appendReplacement(result, simple);
    }
    return result.toString();
  }

}
