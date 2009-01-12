/*
 * Copyright 2007 Google Inc.
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
package com.google.test.metric.report;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class GoogleChartAPI {

  public static final String BASE_URL = "http://chart.apis.google.com/chart";
  protected final Map<String, String> keys = new HashMap<String, String>();
  public static final String GREEN = "00AA00";
  public static final String YELLOW = "FFFF00";
  public static final String RED = "D22222";
  private int width = 100;
  private int height = 100;

  public Map<String,String> getMap() {
    return keys;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(BASE_URL);
    String seperator = "?";
    for (String key : new TreeSet<String>(keys.keySet())) {
      String value = keys.get(key);
      buf.append(seperator);
      buf.append(key);
      buf.append("=");
      buf.append(value.replace(' ', '+'));
      seperator = "&";
    }
    return buf.toString();
  }

  protected String toList(String separator, String... items) {
    StringBuilder buf = new StringBuilder();
    String sep = "";
    for (String label : items) {
      buf.append(sep);
      buf.append(label);
      sep = separator;
    }
    return buf.toString();
  }

  protected String toList(String separator, int... items) {
    StringBuilder buf = new StringBuilder();
    String sep = "";
    for (int label : items) {
      buf.append(sep);
      buf.append(label);
      sep = separator;
    }
    return buf.toString();
  }

  protected String encodeT(int... values) {
    StringBuilder buf = new StringBuilder();
    String separator = "t:";
    for (int i : values) {
      buf.append(separator);
      buf.append(Integer.toString(i));
      separator = ",";
    }
    return buf.toString();
  }

  public void setSize(int width, int height) {
    this.width = width;
    this.height = height;
    keys.put("chs", width + "x" + height);
  }

  public void setTitle(String title) {
    keys.put("chtt", title);
  }

  public void setItemLabel(String... labels) {
    keys.put("chl", toList("|", labels));
  }

  public void setChartLabel(String... labels) {
    keys.put("chdl", toList("|", labels));
  }

  public void setColors(String... colors) {
    keys.put("chco", toList(",", colors));
  }

  public void setValues(int ... values) {
    keys.put("chd", encodeT(values));
  }

  public void setValues(int[]...values) {
    StringBuilder buf = new StringBuilder();
    String seperator = "s:";
    for (int[] dataSetValues : values) {
      buf.append(seperator);
      for (int value : dataSetValues) {
        buf.append(encodeS(value));
      }
      seperator = ",";
    }
    keys.put("chd", buf.toString());
  }

  protected char encodeS(int value) {
    if (value <= 0) {
      return 'A';
    } else if (value <= 25) {
      return (char) ('A' + value);
    } else if (value <= 51) {
      return (char) ('a' + value - 26);
    } else if (value <= 61) {
      return (char) ('0' + value - 52);
    } else {
      return '9';
    }
  }

  public String getHtml() {
    return String.format("<img src='%s' width='%d' height='%d'/>", toString(), width, height);
  }

}
