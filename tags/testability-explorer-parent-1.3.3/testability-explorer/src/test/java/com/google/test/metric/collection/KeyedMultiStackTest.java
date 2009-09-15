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

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import junit.framework.TestCase;

public class KeyedMultiStackTest extends TestCase {

  public static class Push extends PopClosure<String, Integer> {

    private final List<Integer> items;

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

  private class NoopClosure extends PopClosure<String, Integer> {

    private final int size;

    public NoopClosure(int size) {
      this.size = size;
    }

    @Override
    public int getSize() {
      return size;
    }

    @Override
    public List<Integer> pop(String key, List<Integer> list) {
      return emptyList();
    }

  }
  private class LoggingClosure extends PopClosure<String, Integer> {

    private final int size;

    public LoggingClosure(int size) {
      this.size = size;
    }

    @Override
    public List<Integer> pop(String key, List<Integer> value) {
      log.add(value.toString());
      return emptyList();
    }

    @Override
    public int getSize() {
      return size;
    }
  }

  KeyedMultiStack<String, Integer> stack = new KeyedMultiStack<String, Integer>(
      "", new KeyedMultiStack.ValueCompactor<Integer>());

  Set<String> log = new TreeSet<String>();

  public void testBasicOperationsOnSingleDimension() throws Exception {
    stack.apply("", new Push(0));
    stack.apply("", new PopClosure<String, Integer>() {
      @Override
      public List<Integer> pop(String key, List<Integer> value) {
        assertEquals("", key);
        assertEquals(1, value.size());
        assertEquals(new Integer(0), value.get(0));
        log.add(value.get(0).toString());
        return emptyList();
      }

      @Override
      public int getSize() {
        return 1;
      }
    });
    assertEquals("[0]", log.toString());
    stack.assertEmpty();
  }

  public void testToString() throws Exception {
    stack.apply("", new Push(0));
    assertNotNull(stack.toString());
  }

  public void testPushPushPopOnSplit() throws Exception {
    stack.apply("", new Push(0));
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(1));
    stack.apply("b", new Push(2));
    stack.apply("a", new LoggingClosure(2));
    stack.apply("b", new LoggingClosure(2));
    assertEquals("[[0, 1], [0, 2]]", log.toString());
  }

  public void testPushSplitPushJoinPOP() throws Exception {
    stack.apply("", new Push(0));
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(1));
    stack.apply("b", new Push(2));
    stack.join(asList("a", "b"), "c");
    stack.apply("c", new Push(3));
    stack.apply("c", new LoggingClosure(3));
    assertEquals("[[0, 1, 3], [0, 2, 3]]", log.toString());
  }

  public void testSplitAndJoinShouldCollapsMultipleStacksIfTheyAreOfSameContent()
      throws Exception {
    stack.apply("", new Push(0));
    stack.split("", asList("a", "b"));
    stack.join(asList("a", "b"), "");
    stack.apply("", new Push(1));
    stack.apply("", new LoggingClosure(2));
    assertEquals("[[0, 1]]", log.toString());
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
    assertEquals("[[0, 11]]", log.toString());
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
    assertEquals("[[0, 1], [0, 2]]", log.toString());
  }

  public void testParalelPopAndPush() throws Exception {
    stack.apply("", new Push(0));
    stack.apply("", new Push(1));
    stack.split("", asList("a", "b"));
    stack.apply("a", new Push(2));
    stack.apply("b", new Push(3));
    stack.join(asList("a", "b"), "join");
    stack.apply("join", new PopClosure<String, Integer>() {
      int id = 3;
      @Override
      public List<Integer> pop(String key, List<Integer> list) {
        return asList(id++, id++);
      }

      @Override
      public int getSize() {
        return 2;
      }
    });
    stack.apply("join", new LoggingClosure(3));
    assertEquals("[[0, 3, 4], [0, 5, 6]]", log.toString());
  }

  public void testPathEnsureSize() throws Exception {
    KeyedMultiStack.Path<String> path = new KeyedMultiStack.Path<String>();
    path.add("A");
    path.add("B");
    path.add("C");
    path.add("D");
    path.add("E");
    path.add("F");
    path.add("G");
    assertEquals("A :: B :: C :: D :: E :: F :: G", path.toString());
  }

  public void testPathHashCode() throws Exception {
    KeyedMultiStack.Path<String> p1 = new KeyedMultiStack.Path<String>();
    KeyedMultiStack.Path<String> p2 = new KeyedMultiStack.Path<String>();
    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
  }

  public void testPopTooSlowForVeryLargeSets() throws Exception {
    long start = System.currentTimeMillis();
    int counter = 0;
    String[] subKeys = {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p"};
    stack.split("", asList(subKeys));
    for (String key : subKeys) {
      stack.apply(key, new Push(counter++));
    }
    stack.join(asList(subKeys), "L1");

    stack.split("L1", asList(subKeys));
    for (String key : subKeys) {
      stack.apply(key, new Push(counter++));
    }
    stack.join(asList(subKeys), "L2");

    stack.split("L2", asList(subKeys));
    for (String key : subKeys) {
      stack.apply(key, new Push(counter++));
    }
    stack.join(asList(subKeys), "L3");

    stack.apply("L3", new NoopClosure(3));
    long duration = System.currentTimeMillis() - start;
    assertTrue("Duration: " + duration, duration < 90);
  }
}
