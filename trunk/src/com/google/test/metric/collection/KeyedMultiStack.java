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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class acts as a stack externally. The difference is that internally it
 * is implemented with several stacks growing in parallel. The stack starts as a
 * single stack when it is constructed. As it grows it can be split into
 * multiple keys. The keys can then be rejoined to a new key. Any push/pop
 * operations will be performed on one or more internal stacks depending on how
 * the stack was split and rejoined.
 *
 * This is useful when analyzing stack machines (such as Java JVM) which keep
 * variables on the stack but whose execution can split and rejoin on the level
 * of byte-codes.
 *
 *
 * @author mhevery@google.com <Misko Hevery>
 *
 * @param <KEY>
 *            Selector which decides which of the stacks are being pushed /
 *            popped
 * @param <VALUE>
 *            Value on stack.
 */
public class KeyedMultiStack<KEY, VALUE> {

  public static class ValueCompactor<VALUE> {
    public List<List<VALUE>> compact(List<List<VALUE>> pushValues) {
      return pushValues;
    }
  }

  public static class StackUnderflowException extends RuntimeException {
    private static final long serialVersionUID = 4649233306901482842L;
  }

  public static class KeyNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 4649233306901482842L;

    public <KEY> KeyNotFoundException(KEY key) {
      super("Key '" + key + "' not found.");
    }
  }

  private static class Entry<VALUE> {
    private final int depth;
    private final Set<Entry<VALUE>> parents;
    private final VALUE value;

    public Entry() {
      this.depth = -1;
      this.parents = null;
      this.value = null;
    }

    public Entry(Set<Entry<VALUE>> parents, VALUE value) {
      this.value = value;
      this.parents = parents;
      if (parents.size() == 0) {
        this.depth = 0;
      } else {
        this.depth = parents.iterator().next().depth + 1;
      }
    }

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("\n");
      toString(buf, "");
      return buf.toString();
    }

    private void toString(StringBuilder buf, String offset) {
      if (depth == -1) {
        return;
      }
      buf.append(offset);
      buf.append("#");
      buf.append(depth);
      buf.append("(");
      buf.append(value);
      buf.append(")\n");
      for (Entry<VALUE> parent : parents) {
        parent.toString(buf, offset + "  ");
      }
    }

    public Set<Entry<VALUE>> getParents() {
      if (depth == -1) {
        throw new StackUnderflowException();
      }
      return parents;
    }

  }

  @SuppressWarnings("unchecked")
  public static class Path<VALUE> {
    private VALUE[] elements = (VALUE[]) new Object[4];
    private int size = 0;
    public void add(VALUE value) {
      ensureSize();
      elements[size++] = value;
    }

    private void ensureSize() {
      if (elements.length == size) {
        VALUE[] newElements = (VALUE[]) new Object[elements.length * 2];
        System.arraycopy(elements, 0, newElements, 0, elements.length);
        elements = newElements;
      }
    }

    public List<VALUE> asList() {
      return Arrays.asList(elements).subList(0, size);
    }

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();
      int count = 0;
      for (VALUE value : elements) {
        if (count >= size) {
          break;
        }
        if (count > 0) {
          buf.append(" :: ");
        }
        buf.append(value);
        count++;
      }
      return buf.toString();
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(elements);
      result = prime * result + size;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      Path other = (Path) obj;
      if (!Arrays.equals(elements, other.elements)) {
        return false;
      }
      if (size != other.size) {
        return false;
      }
      return true;
    }
  }

  private final Entry<VALUE> root = new Entry<VALUE>();

  private final Map<KEY, Set<Entry<VALUE>>> head = new HashMap<KEY, Set<Entry<VALUE>>>();

  private final ValueCompactor<VALUE> pathCompactor;

  /**
   * @param key Initial key for the primordial stack.
   */
  public KeyedMultiStack(KEY key, ValueCompactor<VALUE> pathCompactor) {
    this(pathCompactor);
    init(key);
  }

  public KeyedMultiStack(ValueCompactor<VALUE> pathCompactor) {
    this.pathCompactor = pathCompactor;
  }

  public void init(KEY key) {
    head.clear();
    head.put(key, set(root));
  }

  private Set<Entry<VALUE>> getHead(KEY key) {
    if (!head.containsKey(key)) {
      throw new KeyNotFoundException(key);
    } else {
      return head.get(key);
    }
  }

  private Set<Entry<VALUE>> removeHead(KEY key) {
    if (!head.containsKey(key)) {
      throw new KeyNotFoundException(key);
    } else {
      return head.remove(key);
    }
  }

  @SuppressWarnings("unchecked")
  private Set<Entry<VALUE>> set(Entry<VALUE> entries) {
    return new HashSet<Entry<VALUE>>(asList(entries));
  }

  /**
   * Pop vale from the stack. The closure can be called more then once if there are parallel stacks
   * due to splits.
   *
   * @param key        key as stack selector
   * @param popClosure Closer which will be called once per each virtual stack.
   */
  public void apply(KEY key, PopClosure<KEY, VALUE> popClosure) {
    int popSize = popClosure.getSize();
    Set<Path<VALUE>> paths = fillPopPaths(getHead(key), popSize);
    popPaths(key, popSize);
    List<List<VALUE>> pushValues = new ArrayList<List<VALUE>>(paths.size());
    int pushSize = -1;
    for (Path<VALUE> path : paths) {
      List<VALUE> pushSet = popClosure.pop(key, path.asList());
      if (pushSize == -1) {
        pushSize = pushSet.size();
      } else if (pushSize != pushSet.size()) {
        throw new IllegalStateException(
            "All push pushes must be of same size.");
      }
      pushValues.add(pushSet);
    }
    pushValues = pathCompactor.compact(pushValues);
    if (pushSize > 0) {
      Set<Entry<VALUE>> parent = head.get(key);
      Set<Entry<VALUE>> newHead = new HashSet<Entry<VALUE>>();
      for (List<VALUE> values : pushValues) {
        Entry<VALUE> entry = null;
        for (VALUE value : values) {
          if (entry == null) {
            entry = new Entry<VALUE>(parent, value);
          } else {
            entry = new Entry<VALUE>(set(entry), value);
          }
        }
        newHead.add(entry);
      }
      head.put(key, newHead);
    }
  }

  private void popPaths(KEY key, int size) {
    if (size == 0) {
      return;
    }
    Set<Entry<VALUE>> newEntries = new HashSet<Entry<VALUE>>();
    for (Entry<VALUE> entry : getHead(key)) {
      newEntries.addAll(entry.getParents());
    }
    head.put(key, newEntries);
    popPaths(key, size - 1);
  }

  private Set<Path<VALUE>> fillPopPaths(Set<Entry<VALUE>> entries, int size) {
    Set<Path<VALUE>> paths = new HashSet<Path<VALUE>>();
    if (size == 0) {
      paths.add(new Path<VALUE>());
    } else {
      for (Entry<VALUE> entry : entries) {
        if (entry.depth < size - 1) {
          throw new StackUnderflowException();
        }
        for (Path<VALUE> path : fillPopPaths(entry.getParents(),
            size - 1)) {
          path.add(entry.value);
          paths.add(path);
        }
      }
    }
    return paths;
  }

  /**
   * Split the internal stacks to a new set of stacks
   *
   * @param key     Stack(s) to split.
   * @param subKeys New names for those stacks
   */
  public void split(KEY key, List<KEY> subKeys) {
    Set<Entry<VALUE>> entries = removeHead(key);
    for (KEY subKey : subKeys) {
      if (head.containsKey(subKey)) {
        Set<Entry<VALUE>> existingList = head.get(subKey);
        entries.addAll(existingList);
      }
    }
    assertSameDepth(entries);
    for (KEY subKey : subKeys) {
      head.put(subKey, entries);
    }
  }

  /**
   * Rejoin the stacks. This is not always possible since each stack may now be different, but it
   * will make sure that the new single key will now refer to all of the stacks treating them as
   * one. This will make the pop operation be applied to individual stacks.
   *
   * @param subKeys a list of keys for the old stacks to join.
   * @param newKey  new name for the stack
   */
  public void join(Collection<KEY> subKeys, KEY newKey) {
    Set<Entry<VALUE>> newHead = new HashSet<Entry<VALUE>>();
    for (KEY key : subKeys) {
      Set<Entry<VALUE>> entries = getHead(key);
      newHead.addAll(entries);
    }
    assertSameDepth(newHead);
    for (KEY key : subKeys) {
      removeHead(key);
    }
    head.put(newKey, newHead);
  }

  private void assertSameDepth(Collection<Entry<VALUE>> entries) {
    int expectedDepth = Integer.MIN_VALUE;
    for (Entry<VALUE> entry : entries) {
      if (expectedDepth == Integer.MIN_VALUE) {
        expectedDepth = entry.depth;
      } else if (expectedDepth != entry.depth) {
        throw new IllegalStateException(
            "Not all entries are at same depth. Can't join.");
      }
    }
  }

  void assertEmpty() {
    for (Collection<Entry<VALUE>> entries : head.values()) {
      for (Entry<VALUE> entry : entries) {
        if (entry.depth > -1) {
          throw new IllegalStateException("Stack not empty.");
        }
      }
    }
  }

  @Override
  public String toString() {
    return head.toString();
  }

}
