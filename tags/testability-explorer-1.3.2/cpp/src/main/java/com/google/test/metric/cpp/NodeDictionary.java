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
package com.google.test.metric.cpp;

import com.google.test.metric.cpp.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class NodeDictionary {
  private final Map<String, Node> symbols = new HashMap<String, Node>();

  public void registerNode(String name, Node node) {
    symbols.put(name, node);
  }

  public Node lookupNode(String name) {
    return symbols.get(name);
  }
}