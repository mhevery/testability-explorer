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
package com.google.test.metric.method;

import static java.util.Collections.unmodifiableList;

import java.util.ArrayList;
import java.util.List;

import com.google.test.metric.method.op.stack.Load;
import com.google.test.metric.method.op.stack.StackOperation;

public class Block {

  private final String id;
  private final List<Block> previousBlocks = new ArrayList<Block>();
  private final List<StackOperation> operations = new ArrayList<StackOperation>();
  private final List<Block> nextBlocks = new ArrayList<Block>();
  private final boolean isTerminal = false;
  private Constant exception;

  public Block(String id) {
    this.id = id;
  }

  public void addNextBlock(Block nextBlock) {
    if (!nextBlocks.contains(nextBlock)) {
      nextBlocks.add(nextBlock);
      nextBlock.previousBlocks.add(this);
    }
  }

  public void addOp(StackOperation operation) {
    operations.add(operation);
  }

  public List<StackOperation> getOperations() {
    return operations;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append(exception == null ? "Block" : "ExceptionHandlerBlock");
    buf.append("[");
    buf.append(id);
    String sep = " -> ";
    for (Block next : nextBlocks) {
      buf.append(sep);
      buf.append(next.id);
      sep = ", ";
    }
    sep = " <- ";
    for (Block next : previousBlocks) {
      buf.append(sep);
      buf.append(next.id);
      sep = ", ";
    }
    buf.append("]{\n");
    for (StackOperation operation : operations) {
      buf.append("  ");
      buf.append(operation);
      buf.append("\n");
    }
    buf.append("}");
    return buf.toString();
  }

  public List<Block> getNextBlocks() {
    return unmodifiableList(nextBlocks);
  }

  public boolean isTerminal() {
    return isTerminal;
  }

  public String getId() {
    return id;
  }

  public void setExceptionHandler(int lineNumber, Constant exception) {
    if (this.exception == null) {
      operations.add(0, new Load(lineNumber, exception));
    }
    this.exception = exception;
  }

}
