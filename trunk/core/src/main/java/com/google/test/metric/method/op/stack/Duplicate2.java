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
package com.google.test.metric.method.op.stack;

import java.util.ArrayList;
import java.util.List;

import com.google.test.metric.Variable;

public class Duplicate2 extends StackOperation {

  private final int offset;

  public Duplicate2(int lineNumber, int offset) {
    super(lineNumber);
    this.offset = offset;
  }

  @Override
  public int getOperatorCount() {
    return 2 + offset;
  }

  @Override
  public List<Variable> apply(List<Variable> input) {
    List<Variable> list = new ArrayList<Variable>();
    list.add(input.get(input.size() - 2));
    list.add(input.get(input.size() - 1));
    for (Variable in : input) {
      list.add(in);
    }
    return list;
  }

  @Override
  public String toString() {
    return "duplicate2" + (offset > 0 ? "_X" + offset : "");
  }

}
