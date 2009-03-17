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

import com.google.common.collect.ForwardingMap;
import com.google.common.base.Supplier;

import java.util.*;

/**
 * Wrap a Map to provide lazy creation of its values.
 * @author alexeagle@google.com (Alex Eagle)
 */
public class LazyHashMap<K, V> extends ForwardingMap<K,V> {
  private final Map<K, V> delegate;
  private final Supplier<V> supplier;

  public LazyHashMap(Map<K, V> delegate, Supplier<V> supplier) {
    this.delegate = delegate;
    this.supplier = supplier;
  }

  protected Map<K, V> delegate() {
    return delegate;
  }

  @Override
  public V get(Object o) {
    V value = delegate.get(o);
    if (value == null) {
      value = supplier.get();
      delegate.put((K)o, value);
    }
    return value;
  }

  public static <K,V> Map<K, V> newLazyHashMap(Map<K, V> delegate, Supplier<V> supplier) {
    return new LazyHashMap<K,V>(delegate, supplier);
  }

}
