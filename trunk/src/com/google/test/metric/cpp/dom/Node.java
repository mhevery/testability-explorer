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
import java.util.List;

/*
 * Base class for all C++ AST nodes.
 */
public class Node {
  private final List<Node> children = new ArrayList<Node>();

  @SuppressWarnings("unchecked")
  public <T> T getChild(int index) {
    return (T) children.get(index);
  }

  public void addChild(Node child) {
    children.add(child);
  }
}
