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

/**
 * This class illustrates global state through using another class'
 * {@code public static final} instance. (That "other class" is
 * {@code FinalGlobal} below.
 *
 * <p>The purpose of testing this is to illustrate how global mutable state is
 * transitive, for non-fields.
 */
public class FinalGlobalExample {

  public static class Gadget {
    /**
     * This is important: it is final and is exposed through {@code getId}.
     * When it is accessed by {@code getGlobalId} below, observe that there is
     * not a Global State cost, since it is final. */
    private final String id;

    /**
     * Note that this is not final, and it is exposed through {@code getCount}.
     * When it is accessed through {@code getGlobalCount} below, there is a
     * Global State cost, since it is mutable global state. (Made global through
     * the transitive nature of the {@code FinalGlobal} class having a static
     * {@code Gadget} field.*/
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

  /** This class has reference to global state */
  public static class FinalGlobal {

    /** Holds a <em>final</em> instance to global state. This does not count as global
     * mutable state, but observe how what it references can be counted as globally
     * mutable state.
     */
    public static final Gadget finalInstance = new Gadget("Global", 1);
  }


  /* The rest are instance methods on {@code FinalGlobalExample} */

  public Gadget getInstance() {
    return FinalGlobal.finalInstance;
  }

  /** The field {@code id} in Gadget is final, so accessing it is not counted as a cost. */
  public String getGlobalId() {
    return FinalGlobal.finalInstance.getId();
  }

  public int getGlobalCount() {
    return FinalGlobal.finalInstance.getCount();
  }

  public int globalIncrement() {
    return FinalGlobal.finalInstance.increment();
  }
}
