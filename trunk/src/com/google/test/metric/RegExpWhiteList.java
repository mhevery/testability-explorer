package com.google.test.metric;

import static java.util.regex.Pattern.compile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExpWhiteList implements WhiteList {

  class Predicate {
    private final Pattern pattern;

    public Predicate(String regExp) {
      pattern = compile(regExp);
    }

    boolean isClassWhitelisted(String className){
      Matcher matcher = pattern.matcher(className);
      return matcher.find() && matcher.start() == 0;
    }
  }

  class NotPredicate extends Predicate{

    public NotPredicate(String regExp) {
      super(regExp);
    }

    @Override
    boolean isClassWhitelisted(String className) {
      return ! super.isClassWhitelisted(className);
    }

  }

  private final List<Predicate> patterns = new ArrayList<Predicate>();

  public RegExpWhiteList(String... regexps) {
    for (String regExp : regexps) {
      if (regExp.startsWith("!")) {
        patterns.add(new NotPredicate(regExp.substring(1)));
      } else {
        patterns.add(new Predicate(regExp));
      }
    }
  }

  public boolean isClassWhiteListed(String className) {
    for (Predicate predicate : patterns) {
      if (predicate.isClassWhitelisted(className)) {
        return true;
      }
    }
    return false;
  }

  public void addPackage(String regexp) {
    patterns.add(new Predicate(regexp));
  }

}
