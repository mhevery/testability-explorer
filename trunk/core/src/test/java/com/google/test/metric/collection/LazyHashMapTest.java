/*
 * Copyright 2009 Google Inc.
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
package com.google.test.metric.collection;

import junit.framework.TestCase;

import java.util.Map;
import java.util.HashMap;

import com.google.common.base.Supplier;

/**
 * @author alexeagle@google.com (Alex Eagle)
 */
public class LazyHashMapTest extends TestCase {
  public void testGetNonExistantValue() throws Exception {
    Supplier<Number> numberSupplier = new Supplier<Number>() {
      public Number get() {
        return 3;
      }
    };
    Map<String, Number> map = new LazyHashMap<String, Number>(numberSupplier);
    map.get("hello");
    assertEquals(3, map.get("hello"));
  }
}
