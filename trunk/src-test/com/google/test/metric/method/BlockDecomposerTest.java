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

import static com.google.test.metric.Type.VOID;

import com.google.test.metric.ClassRepository;
import com.google.test.metric.LocalVariableInfo;
import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.op.stack.JSR;
import com.google.test.metric.method.op.stack.Load;
import com.google.test.metric.method.op.stack.RetSub;
import com.google.test.metric.method.op.stack.Return;
import com.google.test.metric.method.op.stack.StackOperation;
import com.google.test.metric.method.op.stack.Store;
import com.google.test.metric.method.op.stack.Throw;
import com.google.test.metric.method.op.turing.Operation;

import junit.framework.TestCase;

import org.objectweb.asm.Label;

import java.util.Arrays;
import java.util.List;

public class BlockDecomposerTest extends TestCase {


  public void testSimpleLinearMethod() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Return ret = new Return(0, VOID);
    decomposer.addOp(ret);
    decomposer.decomposeIntoBlocks();

    Block block = decomposer.getMainBlock();
    assertEquals(list(ret), block.getOperations());
  }

  public void testUnconditionalBackwardGoto() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Label label = new Label();
    Load l1 = load(1);
    Load l2 = load(2);
    decomposer.addOp(l1);
    decomposer.label(label);
    decomposer.addOp(l2);
    decomposer.unconditionalGoto(label);
    decomposer.decomposeIntoBlocks();

    Block main = decomposer.getMainBlock();
    assertEquals(list(l1), main.getOperations());

    assertEquals(1, main.getNextBlocks().size());
    Block next = main.getNextBlocks().get(0);
    assertEquals(list(l2), next.getOperations());
  }


  public void testUnconditionalForwardGoto() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Label label = new Label();
    Load l1 = load(1);
    Load l2 = load(2);
    decomposer.addOp(l1);
    decomposer.unconditionalGoto(label);
    decomposer.label(label);
    decomposer.addOp(l2);
    decomposer.decomposeIntoBlocks();

    Block main = decomposer.getMainBlock();
    assertEquals(list(l1), main.getOperations());

    assertEquals(1, main.getNextBlocks().size());
    Block next = main.getNextBlocks().get(0);
    assertEquals(list(l2), next.getOperations());
  }


  public void testConditionalBackwardGoto() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Label label = new Label();
    Load l1 = load(1);
    Load l2 = load(2);
    Load l3 = load(3);
    decomposer.addOp(l1);
    decomposer.label(label);
    decomposer.addOp(l2);
    decomposer.conditionalGoto(label);
    decomposer.addOp(l3);
    decomposer.decomposeIntoBlocks();

    Block main = decomposer.getMainBlock();
    Block nextTrue = decomposer.getBlock(label);
    Block nextFlase = nextTrue.getNextBlocks().get(0);
    assertEquals(list(l1), main.getOperations());
    assertEquals(list(l2), nextTrue.getOperations());
    assertEquals(list(l3), nextFlase.getOperations());
  }


  public void testConditionalForwardGoto() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Label label = new Label();
    Load l1 = load(1);
    Load l2 = load(2);
    Load l3 = load(3);
    decomposer.addOp(l1); // main
    decomposer.conditionalGoto(label);
    decomposer.addOp(l2); // nextFalse
    decomposer.label(label);
    decomposer.addOp(l3); // nextTrue
    decomposer.decomposeIntoBlocks();

    Block main = decomposer.getMainBlock();
    Block nextFlase = main.getNextBlocks().get(0);
    Block nextTrue = decomposer.getBlock(label);
    assertEquals(list(l1), main.getOperations());
    assertEquals(list(l2), nextFlase.getOperations());
    assertEquals(list(l3), nextTrue.getOperations());
  }



  private <T> List<T> list(T...items) {
    return Arrays.asList(items);
  }

  public void testIgnoreExtraLabels() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Load l1 = load(1);
    Load l2 = load(2);
    decomposer.label(new Label());
    decomposer.addOp(l1);
    decomposer.addOp(l2);
    decomposer.label(new Label());
    decomposer.decomposeIntoBlocks();

    assertEquals(list(l1, l2), decomposer.getMainBlock().getOperations());
  }

  /**
   * load 1
   * jsr mySub
   * load 2
   * return
   *
   * mySub:
   * load 3
   * return;
   */
  public void testJSR() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    decomposer.addOp(load(1));
    Label sub = new Label();
    decomposer.jumpSubroutine(sub, 0);
    decomposer.addOp(load(2));
    decomposer.addOp(new Return(0, VOID));
    decomposer.label(sub);
    decomposer.addOp(load(3));
    decomposer.addOp(new RetSub(0));
    decomposer.decomposeIntoBlocks();

    Block mainBlock = decomposer.getMainBlock();
    assertEquals(0, mainBlock.getNextBlocks().size());
    List<StackOperation> operations = mainBlock.getOperations();
    assertEquals("[load 1{int}, JSR sub_0, load 2{int}, return void]",
        operations.toString());
    JSR jsr = (JSR) operations.get(1);
    Block subBlock = jsr.getBlock();
    assertEquals("[load 3{int}, RETSUB]", subBlock.getOperations().toString());
  }

  private Load load(int value) {
    return new Load(0, new Constant(value, Type.INT));
  }

  public void testSwitch() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Load l1 = load(1);
    Label c1Label = new Label();
    Load c1 = load(2);
    Label defLabel = new Label();
    Load def = load(3);
    decomposer.addOp(l1);
    decomposer.tableSwitch(defLabel, c1Label);
    decomposer.label(c1Label);
    decomposer.addOp(c1);
    decomposer.label(defLabel);
    decomposer.addOp(def);
    decomposer.decomposeIntoBlocks();

    Block main = decomposer.getMainBlock();
    Block c1Block = decomposer.getBlock(c1Label);
    Block defBlock = decomposer.getBlock(defLabel);
    assertEquals(list(l1), main.getOperations());
    assertEquals(list(c1), c1Block.getOperations());
    assertEquals(list(def), defBlock.getOperations());
    assertEquals(list(c1Block, defBlock), main.getNextBlocks());
  }

  public void testTryCatchReturn() throws Exception {
    /*
     * try {        | label:lTry
     *   return 1;  | label:lTryEnd
     * } catch {    | label:lHandle
     *   return 2;
     * }
     */
    Label lTry = new Label();
    Load l1 = load(1);
    Load l2 = load(2);
    Return ret = new Return(1, Type.INT);
    Label lTryEnd = new Label();
    Label lHandle = new Label();

    BlockDecomposer decomposer = new BlockDecomposer();
    decomposer.tryCatchBlock(lTry, lTryEnd, lHandle, null);
    decomposer.label(lTry);
    decomposer.addOp(l1);
    decomposer.label(lTryEnd); // the catch label comes before the last instruction.
    decomposer.addOp(ret);
    decomposer.label(lHandle);
    decomposer.addOp(l2);
    decomposer.addOp(ret);
    decomposer.decomposeIntoBlocks();

    Block tryBlock = decomposer.getBlock(lTry);
    Block handleBlock = decomposer.getBlock(lHandle);
    assertEquals(list(), tryBlock.getNextBlocks());
    assertEquals(list(l1, ret), tryBlock.getOperations());
    assertEquals("[load ?{java.lang.Throwable}, load 2{int}, return int]",
        handleBlock.getOperations().toString());
  }

  public void testGetExceptionHandlerBlocks() throws Exception {
    /*
     * try {        | label:lTry
     *   a = 1;
     *   return 1;  | label:lTryEnd
     * } catch {    | label:lHandle
     *   a = 2;
     *   return 2;
     * }
     */
    Label lTry = new Label();
    Load l1 = load(1);
    Load l2 = load(2);
    Store store = new Store(-1, new Variable("a", Type.INT, false, false));
    Return ret = new Return(1, Type.INT);
    Label lTryEnd = new Label();
    Label lHandle = new Label();

    BlockDecomposer decomposer = new BlockDecomposer();
    decomposer.tryCatchBlock(lTry, lTryEnd, lHandle, null);
    decomposer.label(lTry);
    decomposer.addOp(l1);
    decomposer.addOp(store);
    decomposer.label(lTryEnd); // the catch label comes before the last instruction.
    decomposer.addOp(l1);
    decomposer.addOp(ret);
    decomposer.label(lHandle);
    decomposer.addOp(l2);
    decomposer.addOp(store);
    decomposer.addOp(l2);
    decomposer.addOp(ret);
    decomposer.decomposeIntoBlocks();

    Block mainBlock = decomposer.getMainBlock();
    assertEquals(0, mainBlock.getNextBlocks().size());
    assertEquals(list(l1, store, l1, ret), mainBlock.getOperations());

    Block catchBlock = decomposer.getBlock(lHandle);
    assertEquals(0, catchBlock.getNextBlocks().size());
    assertEquals(
        "[load ?{java.lang.Throwable}, load 2{int}, store a{int}, load 2{int}, return int]",
        catchBlock.getOperations().toString());

    assertEquals(
        "[a{int} <- 1{int}, return 1{int}, a{int} <- 2{int}, return 2{int}]",
        decomposer.getOperations().toString());
  }

  /**
   *   public static class TryCatchFinally {
   *   public void method() {
   *     int b = 1;
   *     try {
   *       b = 2;
   *       return;
   *     } catch (RuntimeException e) {
   *       b = 3;
   *     } finally {
   *       b = 4;
   *     }
   *     b = 5;
   *   }
   *  }
   *
   *
   *  public void method();
   *  Code:
   *   0:   iconst_1
   *   1:   istore_1
   *   2:   iconst_2
   *   3:   istore_1
   *   4:   goto    20
   *   7:   astore_2
   *   8:   iconst_3
   *   9:   istore_1
   *   10:  iconst_4
   *   11:  istore_1
   *   12:  goto    22
   *   15:  astore_3
   *   16:  iconst_4
   *   17:  istore_1
   *   18:  aload_3
   *   19:  athrow
   *   20:  iconst_4
   *   21:  istore_1
   *   22:  iconst_5
   *   23:  istore_1
   *   24:  return
   *  Exception table:
   *   from   to  target type
   *     2     4     7   Class java/lang/RuntimeException
   *     2    10    15   any
   */
  public void testTryCatchFinally() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Label tryStart = new Label();
    Label tryEnd = new Label();
    Label runtimeHandler = new Label();
    Label catchEnd = new Label();
    Label finallyHandler = new Label();
    Label l20 = new Label();
    Label l22 = new Label();
    Variable b = new LocalVariableInfo("b", Type.INT);
    Variable e = new LocalVariableInfo("e", Type.OBJECT);
    Variable any = new LocalVariableInfo("any", Type.OBJECT);
    decomposer.tryCatchBlock(tryStart, tryEnd, runtimeHandler, "java/lang/RuntimeException");
    decomposer.tryCatchBlock(tryStart, catchEnd, finallyHandler, null);

    /* 0*/ decomposer.addOp(new Load(0, new Constant(1, Type.INT)));
    /* 1*/ decomposer.addOp(new Store(1, b));
    decomposer.label(tryStart);
    /* 2*/ decomposer.addOp(new Load(2, new Constant(2, Type.INT)));
    decomposer.label(tryEnd);
    /* 3*/ decomposer.addOp(new Store(3, b));
    /* 4*/ decomposer.unconditionalGoto(l20);
    decomposer.label(runtimeHandler);
    /* 7*/ decomposer.addOp(new Store(7, e));
    /* 8*/ decomposer.addOp(new Load(8, new Constant(3, Type.INT)));
    /* 9*/ decomposer.addOp(new Store(9, b));
    decomposer.label(catchEnd);
    /*10*/ decomposer.addOp(new Load(10, new Constant(4, Type.INT)));
    /*11*/ decomposer.addOp(new Store(11, b));
    /*12*/ decomposer.unconditionalGoto(l22);
    decomposer.label(finallyHandler);
    /*15*/ decomposer.addOp(new Store(15, any));
    /*16*/ decomposer.addOp(new Load(16, new Constant(4, Type.INT)));
    /*17*/ decomposer.addOp(new Store(17, b));
    /*18*/ decomposer.addOp(new Load(18, any));
    /*19*/ decomposer.addOp(new Throw(19));
    decomposer.label(l20);
    /*20*/ decomposer.addOp(new Load(20, new Constant(4, Type.INT)));
    /*21*/ decomposer.addOp(new Store(21, b));
    decomposer.label(l22);
    /*22*/ decomposer.addOp(new Load(22, new Constant(4, Type.INT)));
    /*23*/ decomposer.addOp(new Store(23, b));
    /*24*/ decomposer.addOp(new Return(24, Type.VOID));

    decomposer.decomposeIntoBlocks();

    assertEquals("[load 1{int}, store b{int}, load 2{int}, store b{int}]",
        decomposer.getBlock(tryStart).getOperations().toString());
  }

  public void testMethodWithNothing() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    decomposer.decomposeIntoBlocks();
    List<Operation> operations = decomposer.getOperations();
    assertEquals(0, operations.size());
  }

  public void testTwoJsrSameLabel() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Label label = new Label();
    decomposer.jumpSubroutine(label, 0);
    decomposer.jumpSubroutine(label, 0);
    decomposer.decomposeIntoBlocks();

    Block main = decomposer.getMainBlock();
    assertEquals("[JSR sub_0, JSR sub_0]", main.getOperations().toString());
  }

  public void testMultipleHandlersSingleExceptionLoad() throws Exception {
    BlockDecomposer decomposer = new BlockDecomposer();
    Label start = new Label();
    Label end = new Label();
    Label handler = new Label();
    decomposer.tryCatchBlock(start, end, handler, null);
    decomposer.tryCatchBlock(start, end, handler, null);
    decomposer.label(start);
    decomposer.addOp(load(1));
    decomposer.label(end);
    decomposer.addOp(load(2));
    decomposer.label(handler);
    decomposer.addOp(load(3));
    decomposer.decomposeIntoBlocks();

    assertEquals(
        "[load ?{java.lang.Throwable}, load 1{int}, load 2{int}, load 3{int}]",
        decomposer.getMainBlock().getOperations().toString());
  }

  public static class TestClass {
    int value;
    int method() {
      value = 0;
      try {
        value = 1;
        return -1;
      } catch (NullPointerException e) {
        value = 2;
        return -2;
      } finally {
        value = 3;
      }

    }
  }

  public void testExperiment() throws Exception {
    ClassRepository repo = new ClassRepository();
    repo.getClass(TestClass.class);
  }

}
