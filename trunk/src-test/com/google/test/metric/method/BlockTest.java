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

import java.util.List;

import junit.framework.TestCase;

import com.google.test.metric.FieldInfo;
import com.google.test.metric.JavaType;
import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.op.stack.GetField;
import com.google.test.metric.method.op.stack.Invoke;
import com.google.test.metric.method.op.stack.Load;
import com.google.test.metric.method.op.stack.PutField;
import com.google.test.metric.method.op.turing.MethodInvokation;
import com.google.test.metric.method.op.turing.Operation;

public class BlockTest extends TestCase {

  private static final Type OBJECT = JavaType.fromClass(Object.class);

  public void testBlockToString() throws Exception {
    Block block = new Block("1");
    assertEquals("Block[1]{\n}", block.toString());

    block.addOp(new Load(-1, var(1)));
    assertEquals("Block[1]{\n  load 1{java.lang.Object}\n}", block.toString());
  }

  public void testVariableStaticAssignment() throws Exception {
    Block block = new Block("1");
    block.addOp(new Load(-1, var(1)));
    block.addOp(new PutField(-1, new FieldInfo(null, "abc", OBJECT,
        false, true, false)));

    List<Operation> operations = new Stack2Turing(block).translate();
    assertEquals("[null.abc{java.lang.Object} <- 1{java.lang.Object}]", operations.toString());
  }

  public void testVariableAssignment() throws Exception {
    Block block = new Block("1");
    block.addOp(new Load(-1, var("this"))); // this
    block.addOp(new Load(-1, var(1)));
    block.addOp(new PutField(-1, new FieldInfo(null, "abc", OBJECT,
        false, false, false)));

    List<Operation> operations = new Stack2Turing(block).translate();
    assertEquals("[null.abc{java.lang.Object} <- 1{java.lang.Object}]", operations.toString());
  }

  public void testGetField() throws Exception {
    Block block = new Block("1");
    block.addOp(new GetField(-1, new FieldInfo(null, "src", OBJECT,
        false, true, false)));
    block.addOp(new PutField(-1, new FieldInfo(null, "dst", OBJECT,
        false, true, false)));

    List<Operation> operations = new Stack2Turing(block).translate();
    assertEquals("[null.dst{java.lang.Object} <- null.src{java.lang.Object}]", operations
        .toString());
  }

  public void testMethodInvocation() throws Exception {
    Block block = new Block("1");
    block.addOp(new Load(-1, var("methodThis"))); // this
    block.addOp(new GetField(-1, new FieldInfo(null, "p1", OBJECT,
        false, true, false)));
    block.addOp(new GetField(-1, new FieldInfo(null, "p2", OBJECT,
        false, true, false)));
    block.addOp(new Invoke(-1, null, "methodA", "(II)A", asList(JavaType.INT,
        JavaType.INT), false, OBJECT));
    block.addOp(new PutField(-1, new FieldInfo(null, "dst", OBJECT,
        false, true, false)));

    List<Operation> operations = new Stack2Turing(block).translate();
    assertEquals("[null.methodA(II)A, null.dst{java.lang.Object} <- ?{java.lang.Object}]",
        operations.toString());
  }

  private Variable var(Object value) {
    return new Constant(value, OBJECT);
  }

  public void testDiamondBlockArrangment() throws Exception {
    Block root = new Block("root");
    Block branchA = new Block("branchA");
    Block branchB = new Block("branchB");
    Block joined = new Block("joined");
    root.addNextBlock(branchA);
    root.addNextBlock(branchB);
    branchA.addNextBlock(joined);
    branchB.addNextBlock(joined);

    root.addOp(new Load(-1, var("this")));
    root.addOp(new Load(-1, var("root")));
    branchA.addOp(new Load(-1, var("A")));
    branchB.addOp(new Load(-1, var("B")));
    joined.addOp(new Load(-1, var("joined")));
    joined.addOp(new Invoke(-1, null, "m", "(III)V", asList(JavaType.INT,
        JavaType.INT, JavaType.INT), false, JavaType.VOID));

    List<Operation> operations = new Stack2Turing(root).translate();
    assertEquals(2, operations.size());
    MethodInvokation mB = (MethodInvokation) operations.get(0);
    MethodInvokation mA = (MethodInvokation) operations.get(1);
    // since we use hash order is non-deterministic
    if (mB.getParameters().get(1).toString().startsWith("A")) {
      MethodInvokation temp = mB;
      mB = mA;
      mA = temp;
    }

    assertEquals("[root{java.lang.Object}, A{java.lang.Object}, joined{java.lang.Object}]", mA
        .getParameters().toString());
    assertEquals("this{java.lang.Object}", mA.getMethodThis().toString());

    assertEquals("[root{java.lang.Object}, B{java.lang.Object}, joined{java.lang.Object}]", mB
        .getParameters().toString());
    assertEquals("this{java.lang.Object}", mB.getMethodThis().toString());
  }

}
