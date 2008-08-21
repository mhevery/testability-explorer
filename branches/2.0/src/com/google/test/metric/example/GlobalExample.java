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
package com.google.test.metric.example;

public class GlobalExample {

  public static class Gadget {
    private final String id;
    private int count;

    public Gadget(String id, int count) {
      this.id = id;
      this.count = count;
    }

    public String getId() {
      return id;
    }

    public int getCount() {
      return count;
    }

    public int increment() {
      return ++count;
    }
  }

  public static class Globals {
    public static final Gadget instance = new Gadget("Global", 1);
  }

  public Gadget getInstance() {
    return Globals.instance;
  }

  public String getGlobalId() {
    return Globals.instance.getId();
  }

  public int getGlobalCount() {
    return Globals.instance.getCount();
  }

  public int globalIncrement() {
    return Globals.instance.increment();
  }
}
