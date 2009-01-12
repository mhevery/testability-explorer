/*
 * Copyright 2008 Google Inc.
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
package com.google.test.metric.cpp.dom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class NodeList implements Iterable<Node> {

  private final List<Node> nodes = new ArrayList<Node>();

  public void add(Node node) {
    nodes.add(node);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(int index) {
    return (T) nodes.get(index);
  }

  public Iterator<Node> iterator() {
    return nodes.iterator();
  }

  public int size() {
    return nodes.size();
  }
}
