package com.google.test.metric;

import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpWhiteList implements WhiteList {

  private final List<Pattern> patterns = new ArrayList<Pattern>();

  public RegExpWhiteList(String... regexps) {
    for (String regExp : regexps) {
      patterns.add(compile(regExp));
    }
  }

  public boolean isClassWhiteListed(String className) {
    for (Pattern pattern : patterns) {
      Matcher matcher = pattern.matcher(className);
      if (matcher.find() && matcher.start() == 0) {
        return true;
      }
    }
    return false;
  }

  public void addPackage(String regexp) {
    patterns.add(compile(regexp));
  }

}
