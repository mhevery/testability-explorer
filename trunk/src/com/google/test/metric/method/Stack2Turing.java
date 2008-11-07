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

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.test.metric.JavaType;
import com.google.test.metric.Variable;
import com.google.test.metric.collection.KeyedMultiStack;
import com.google.test.metric.collection.PopClosure;
import com.google.test.metric.method.op.stack.JSR;
import com.google.test.metric.method.op.stack.StackOperation;
import com.google.test.metric.method.op.turing.Operation;

public class Stack2Turing {

  private final Block rootBlock;
  private final List<Operation> operations = new ArrayList<Operation>();
  public KeyedMultiStack<Block, Variable> stack = new KeyedMultiStack<Block, Variable>();

  public Stack2Turing(Block block) {
    this.rootBlock = block;
  }

  public List<Operation> translate() {
    stack.init(rootBlock);
    translate(rootBlock, new ArrayList<Block>());
    return operations;
  }

  private Block translate(Block block, List<Block> parentProcessed) {
    List<Block> blocks = new ArrayList<Block>();
    List<Block> processed = parentProcessed;
    blocks.add(block);
    while (!blocks.isEmpty()) {
      block = blocks.remove(0);
      processed.add(block);
      for (StackOperation operation : block.getOperations()) {
        translateStackOperation(block, operation);
        if (operation instanceof JSR) {
          JSR jsr = (JSR) operation;
          Block jsrBlock = jsr.getBlock();
          stack.split(block, asList(jsrBlock));
          Block terminalBlock = translate(jsrBlock, processed);
          stack.join(asList(terminalBlock), block);
          processed.add(jsrBlock);
        }
      }
      List<Block> nextBlocks = new ArrayList<Block>(block
          .getNextBlocks());
      nextBlocks.removeAll(processed); // Don't visit already visited
      // blocks
      if (nextBlocks.size() > 0) {
        stack.split(block, nextBlocks);
      }
      blocks.addAll(nextBlocks);
      blocks.removeAll(processed);
    }
    // It appears that when exceptions are involved a method might have
    // paths where stacks are not emptied. So we can't assert this.
    // Verdict is still out.
    // stack.assertEmpty();
    return block;
  }

  private void translateStackOperation(Block block,
      final StackOperation operation) {
    stack.apply(block, new PopClosure<Block, Variable>() {
      @Override
      public List<Variable> pop(Block key, List<Variable> input) {
        List<Variable> variables = operation.apply(input);
        // For performance reasons the line is commented out.
        //assertValid(variables);
        Operation turingOp = operation.toOperation(input);
        if (turingOp != null) {
          operations.add(turingOp);
        }
        return variables;
      }

      @Override
      public int getSize() {
        return operation.getOperatorCount();
      }
    });
  }

  protected void assertValid(List<Variable> variables) {
    Iterator<Variable> iter = variables.iterator();
    while (iter.hasNext()) {
      final Variable variable = iter.next();
      if (JavaType.isDoubleSlot(variable.getType())) {
        Variable varNext = iter.hasNext() ? iter.next() : null;
        if (variable != varNext) {
          throw new IllegalStateException("Variable list '"
              + variables + "' contanins variable '" + variable
              + "' which is a double but the next "
              + "variable in the list is not a duplicate.");
        }
      }
    }
  }

}
