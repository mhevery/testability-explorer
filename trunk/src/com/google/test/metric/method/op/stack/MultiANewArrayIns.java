package com.google.test.metric.method.op.stack;

import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.method.Constant;

import java.util.List;

public class MultiANewArrayIns extends StackOperation {

  private final Type clazz;
  private final int dims;

  public MultiANewArrayIns(int lineNumber, Type clazz, int dims) {
    super(lineNumber);
    this.clazz = clazz;
    this.dims = dims;
  }

  @Override
  public int getOperatorCount() {
    return 1;
  }

  @Override
  public List<Variable> apply(List<Variable> input) {
    Type array = clazz;
    for (int i = 0; i < dims; i++) {
      array = array.toArray();
    }
    return list(new Constant("?", array));
  }

  @Override
  public String toString() {
    return "MultiANewArray dims=" + dims;
  }

}
