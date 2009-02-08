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

public class Variable {
  private final String name;
  private final List<String> path = new ArrayList<String>();
  private final String qualifiedName;

  public Variable(String name) {
    this.name = parse(name);
    this.qualifiedName = createQualifiedName();
  }

  public Variable(String name, Node context) {
    processContext(context);
    this.name = parse(name);
    this.qualifiedName = createQualifiedName();
  }

  public String getName() {
    return name;
  }

  public String getQualifiedName() {
    return qualifiedName;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (!(obj instanceof Variable)) {
      return false;
    }
    Variable that = (Variable) obj;
    return qualifiedName.equals(that.qualifiedName);
  }

  private void processContext(Node context) {
    while (context.getParent() != null) {
      if (context.getParent() instanceof Namespace) {
        Namespace namespace = (Namespace) context.getParent();
        if (namespace.getName() != null) {
          path.add(0, namespace.getName());
        }
      }
      context = context.getParent();
    }
  }

  private String createQualifiedName() {
    StringBuilder builder = new StringBuilder();
    for (String s : path) {
      builder.append(s);
      builder.append("::");
    }
    builder.append(name);
    return builder.toString();
  }

  private String parse(String qualifiedName) {
    String[] names = qualifiedName.split("::");
    for (int i = 0; i < names.length - 1; ++i) {
      path.add(names[i]);
    }
    return names[names.length - 1];
  }
}
