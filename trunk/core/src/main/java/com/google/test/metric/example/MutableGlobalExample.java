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
 * This class illustrates globally mutable state through a <em>non-final</em>
 * {@code public static} instance.
 *
 * <p>See also the documentation on {@link FinalGlobalExample}.
 */
public class MutableGlobalExample {

  public static class Gadget {

    /** This is final, so even if the Gadget object is global, this field
     * won't count as global mutable state. */
    private final String id;

    /** This is not final, so if Gadget is global, this field will count
     * as global mutable state. */
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

  /** This class has reference to global mutable state */
  public static class MutableGlobal {

    /** Holds a <em>non-final</em> instance to global state. This does
     * count as globally mutable state. */
    public static Gadget mutableInstance = new Gadget("Global", 1);
  }


  /* The rest are instance methods on {@code MutableGlobalExample} */

  public Gadget getInstance() {
    return MutableGlobal.mutableInstance;
  }

  /** The field {@code id} in Gadget is final, so accessing it is not
   * counted as a cost. (But other fields can be, and the mutableInstance
   * itself are global state). */
  public String getGlobalId() {
    return MutableGlobal.mutableInstance.getId();
  }

  public int getGlobalCount() {
    return MutableGlobal.mutableInstance.getCount();
  }

  public int globalIncrement() {
    return MutableGlobal.mutableInstance.increment();
  }
}
