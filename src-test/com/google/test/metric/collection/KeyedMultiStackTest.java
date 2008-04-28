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
package com.google.test.metric.collection;

import junit.framework.TestCase;

import java.util.Arrays;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import java.util.List;

public class KeyedMultiStackTest extends TestCase {

  public static class Push extends PopClosure<String, Integer> {

    private List<Integer> items;

    public Push(Integer... integers) {
      items = Arrays.asList(integers);
    }

    @Override
    public int getSize() {
      return 0;
    }

    @Override
    public List<Integer> pop(String key, List<Integer> list) {
      return items;
    }

  }

  private final class LoggingClosure extends PopClosure<String, Integer> {

    private int size;

    public LoggingClosure(int size) {
      this.size = size;
    }

    @Override
    public List<Integer> pop(String key, List<Integer> value) {
      log += value;
      return emptyList();
    }

    @Override
    public int getSize() {
      return size;
    }
  }

  KeyedMultiStack<String, Integer> stack = new KeyedMultiStack<String, Integer>(
      "");

  String log = "";

  public void testBasicOperationsOnSingleDimension() throws Exception {
    stack.apply("", new Push(0));
    stack.apply("", new PopClosure<String, Integer>() {
      @Override
      public List<Integer> pop(String key, List<Integer> value) {
        assertEquals("", key);
        assertEquals(1, value.size());
        assertEquals(new Integer(0), value.get(0));
        log += value.get(0);
        return emptyList();
      }

      @Override
      public int getSize() {
        return 1;
      }
    });
    assertEquals("0", log);
    stack.assertEmpty();
  }
  
  public void testToString() throws Exception {
    assertNotNull(stack.toString());
  }

  public void testPushPushPopOnSplit() throws Exception {
    stack.apply("", new Push(0));
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(1));
    stack.apply("b", new Push(2));
    stack.apply("a", new LoggingClosure(2));
    stack.apply("b", new LoggingClosure(2));
    assertEquals("[0, 1][0, 2]", log);
  }

  public void testPushSplitPushJoinPOP() throws Exception {
    stack.apply("", new Push(0));
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(1));
    stack.apply("b", new Push(2));
    stack.join(asList("a", "b"), "c");
    stack.apply("c", new Push(3));
    stack.apply("c", new LoggingClosure(3));
    assertEquals("[0, 1, 3][0, 2, 3]", log);
  }

  public void testSplitAndJoinShouldCollapsMultipleStacksIfTheyAreOfSameContent()
      throws Exception {
    stack.apply("", new Push(0));
    stack.split("", asList("a", "b"));
    stack.join(asList("a", "b"), "");
    stack.apply("", new Push(1));
    stack.apply("", new LoggingClosure(2));
    assertEquals("[0, 1]", log);
  }

  public void testConcurentPushInPopClosure() throws Exception {
    stack.apply("", new Push(0));
    stack.apply("", new Push(1));
    stack.apply("", new PopClosure<String, Integer>() {
      @Override
      public List<Integer> pop(String key, List<Integer> value) {
        stack.apply(key, new Push(value.get(0) + 10));
        return emptyList();
      }

      @Override
      public int getSize() {
        return 1;
      }
    });
    stack.apply("", new LoggingClosure(2));
    assertEquals("[0, 11]", log);
  }

  public void testPopTooMuch() throws Exception {
    try {
      stack.apply("", new LoggingClosure(1));
      fail();
    } catch (KeyedMultiStack.StackUnderflowException e) {
    }
  }

  public void testUnknownKey() throws Exception {
    try {
      stack.apply("X", new Push());
      fail();
    } catch (KeyedMultiStack.KeyNotFoundException e) {
    }
  }

  public void testSplitUnknwonNamespace() throws Exception {
    try {
      stack.split("X", asList("A", "B"));
      fail();
    } catch (KeyedMultiStack.KeyNotFoundException e) {
    }
  }

  public void testJoinUnknownNamespace() throws Exception {
    try {
      stack.join(asList("B", "C"), "A");
      fail();
    } catch (KeyedMultiStack.KeyNotFoundException e) {
    }
  }

  public void testUnevenJoin() throws Exception {
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(0));
    try {
      stack.join(asList("a", "b"), "c");
      fail();
    } catch (IllegalStateException e) {
    }
  }

  public void testJoinThroughSlipt() throws Exception {
    stack.apply("", new Push(0));
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(1));
    stack.apply("b", new Push(2));
    stack.split("a", asList("join"));
    stack.split("b", asList("join"));
    stack.apply("join", new LoggingClosure(2));
    assertEquals("[0, 2][0, 1]", log);
  }

  public void testParalelPopAndPush() throws Exception {
    stack.apply("", new Push(0));
    stack.apply("", new Push(1));
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(2));
    stack.apply("b", new Push(3));
    stack.join(asList("a", "b"), "join");
    stack.apply("join", new PopClosure<String, Integer>() {
      @Override
      public List<Integer> pop(String key, List<Integer> list) {
        return asList(3, 4);
      }

      @Override
      public int getSize() {
        return 2;
      }
    });
    stack.apply("join", new LoggingClosure(3));
    assertEquals("[0, 3, 4][0, 3, 4]", log);
  }
}
