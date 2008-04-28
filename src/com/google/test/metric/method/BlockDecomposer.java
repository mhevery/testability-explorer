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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.Label;

import com.google.test.metric.Type;
import com.google.test.metric.method.op.stack.JSR;
import com.google.test.metric.method.op.stack.Return;
import com.google.test.metric.method.op.stack.StackOperation;
import com.google.test.metric.method.op.stack.Throw;
import com.google.test.metric.method.op.turing.Operation;

/**
 * @author misko@google.com <Misko Hevery>
 */
public class BlockDecomposer {

  /**
   * Method is broken down into one frame per bytecode in order to break it down
   * into blocks
   */
  private static class Frame {

    /**
     * Next frame (ie next bytecode)
     */
    private Frame next;
    /**
     * Operation in this frame
     */
    private final StackOperation operation;
    /**
     * Any label associated with this bytecode
     */
    private Label label;
    /**
     * Block which this bytecode ended up assigned to
     */
    public Block block;
    /**
     * True if this is first bytecode of a block (GOTO). If true then new block
     * will start here.
     */
    public boolean blockStartsHere;
    /**
     * If true then this will be last instruction in block and new block will
     * start on next frame.
     */
    public boolean blockEndsHere;
    /**
     * If not null current block will link with block that this field points to.
     */
    public List<Label> gotoLabels = new LinkedList<Label>();
    /**
     * This instruction ends execution of block (ie return, throw) If this is
     * true then this and subsequent blocks will not be linked
     */
    public boolean terminal;

    public Frame(Frame frame, StackOperation operation, Label label) {
      this.operation = operation;
      this.label = label;
      if (frame != null) {
        frame.addFrame(this);
      }
    }

    private void addFrame(Frame frame) {
      next = frame;
    }

    @Override
    public String toString() {
      ByteArrayOutputStream buf = new ByteArrayOutputStream();
      PrintStream out = new PrintStream(buf, true);
      out.printf("%-10s %-70s %-15s s=%-5b e=%-5b t=%-5b goto=%s", label, operation,
          block == null ? null : block.getId(), blockStartsHere, blockEndsHere,
          terminal, gotoLabels);
      return buf.toString();
    }

  }

  private final Map<Label, Frame> frames = new HashMap<Label, Frame>();
  private final Map<Label, Block> blocks = new HashMap<Label, Block>();
  private final List<Runnable> extraLinkSteps = new LinkedList<Runnable>();
  private Frame firstFrame;
  private Frame lastFrame;
  private Label lastLabel;
  private Block mainBlock;
  private int counter = 0;

  public void addOp(StackOperation operation) {
    lastFrame = new Frame(lastFrame, operation, lastLabel);
    if (operation instanceof Return || operation instanceof Throw) {
      lastFrame.terminal = true;
      lastFrame.blockEndsHere = true;
    }
    if (firstFrame == null) {
      firstFrame = lastFrame;
    }
    applyLastLabel();
  }

  private void applyLastLabel() {
    if (lastLabel != null) {
      frames.put(lastLabel, lastFrame);
      lastFrame.label = lastLabel;
      lastLabel = null;
    }
  }

  public void label(Label label) {
    if (lastLabel == null) {
      lastLabel = label;
    } else {
      throw new IllegalStateException("Multiple lables per line are not allowed.");
    }
  }

  public void unconditionalGoto(Label label) {
    lastFrame.blockEndsHere = true;
    lastFrame.gotoLabels.add(label);
    lastFrame.terminal = true;
    applyLastLabel();
  }

  public void conditionalGoto(Label label) {
    lastFrame.blockEndsHere = true;
    lastFrame.gotoLabels.add(label);
    applyLastLabel();
  }

  public void jumpSubroutine(Label label, int lineNumber) {
    Block subBlock = blocks.get(label);
    if (subBlock == null) {
      subBlock = new Block("sub_" + (counter++));
    }
    addOp(new JSR(lineNumber, subBlock));
    blocks.put(label, subBlock);
    applyLastLabel();
  }

  public void tryCatchBlock(Label start, final Label end, final Label handler,
      final String eType) {
    extraLinkSteps.add(new Runnable() {
      public void run() {
        Block endBlock = frames.get(end).block;
        Block handlerBlock = frames.get(handler).block;
        Type type = eType == null ? Type.fromClass(Throwable.class) : Type.fromJava(eType);
        handlerBlock.setExceptionHandler(-1, new Constant("?", type));
        endBlock.addNextBlock(handlerBlock);
      }
    });
  }

  public void tableSwitch(final Label dflt, final Label... labels) {
    lastFrame.terminal = true;
    lastFrame.blockEndsHere = true;
    lastFrame.gotoLabels.addAll(asList(labels));
    lastFrame.gotoLabels.add(dflt);
    applyLastLabel();
  }

  public void done() {
    if (firstFrame != null) {
      breakIntoBlocks();
      copyToBlocks();
      linkBlocks();
      for (Runnable extraLinkStep : extraLinkSteps) {
        extraLinkStep.run();
      }
      mainBlock = firstFrame.block;
    }
  }

  private void breakIntoBlocks() {
    Frame frame = firstFrame;
    while (frame != null) {
      for (Label label : frame.gotoLabels) {
        frames.get(label).blockStartsHere = true;
        frame.blockEndsHere = true;
      }
      if (blocks.containsKey(frame.label)) {
        frame.blockStartsHere = true;
      }
      frame = frame.next;
    }
  }

  private void copyToBlocks() {
    Frame frame = firstFrame;
    Block block = null;
    while (frame != null) {
      if (frame.blockStartsHere) {
        block = null;
      }
      Label frameLabel = frame.label;
      if (blocks.containsKey(frameLabel)) {
        block = blocks.get(frameLabel);
      }
      if (block == null) {
        block = new Block("block_" + (counter++));
      }
      frame.block = block;
      frame.block.addOp(frame.operation);
      if (frame.blockEndsHere) {
        block = null;
      }
      frame = frame.next;
    }
  }

  private void linkBlocks() {
    Frame previousFrame = firstFrame;
    Frame thisFrame = previousFrame.next;
    while (thisFrame != null) {
      Block previousBlock = previousFrame.block;
      Block thisBlock = thisFrame.block;
      if (previousBlock != thisBlock && !previousFrame.terminal) {
        previousBlock.addNextBlock(thisFrame.block);
      }
      for (Label label : previousFrame.gotoLabels) {
        previousBlock.addNextBlock(frames.get(label).block);
      }
      previousFrame = thisFrame;
      thisFrame = thisFrame.next;
    }
  }

  public List<Operation> getOperations() {
    if (mainBlock == null) {
      return Collections.emptyList();
    } else {
      return new Stack2Turing(mainBlock).translate();
    }
  }

  public Block getBlock(Label label) {
    return frames.get(label).block;
  }

  public Block getMainBlock() {
    return mainBlock;
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    Frame frame = firstFrame;
    while (frame != null) {
      buf.append(frame);
      buf.append("\n");
      frame = frame.next;
    }
    if (mainBlock != null) {
      frame = firstFrame;
      List<Block> processed = new LinkedList<Block>();
      while (frame!= null) {
        Block block = frame.block;
        if (!processed.contains(block)) {
          buf.append(block);
          processed.add(block);
        }
        frame = frame.next;
      }
    }
    return buf.toString();
  }

}
