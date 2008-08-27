package com.google.test.metric.ast;

import com.google.test.metric.TestabilityContext;
import com.google.test.metric.Type;
import com.google.test.metric.Variable;
import com.google.test.metric.asm.Visibility;
import com.google.test.metric.ast.AbstractSyntaxTree.Node;
import com.google.test.metric.method.op.turing.Operation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Internal representation of a method.
 */
public class Method extends Node implements MethodInfo, MethodHandle {

  Node owner;
  String name;
  Visibility access;
  Type returnType;
  final List<Parameter> parameters = new LinkedList<Parameter>();
  final Map<String, LocalVariable> localVars = new HashMap<String, LocalVariable>();
  final List<Operation> operations = new LinkedList<Operation>();

  public Method(Node newOwner, String newName, Type newReturnType,
      Visibility newAccess) {

    this.owner = newOwner;
    this.name = newName;
    this.returnType = newReturnType == null ? Type.VOID : newReturnType;
    this.access = newAccess;

    if (owner instanceof Clazz) {
      Clazz clazz = (Clazz) owner;
      clazz.registerMethod(this);
    } else if (owner instanceof Module) {
      Module module = (Module) owner;
      module.registerMethod(this);
    } else {
      throw new IllegalArgumentException();
    }
  }

  public String getName() {
    return name;
  }

  public String getNameDesc() {
    return name;
  }

  public List<LocalVariableInfo> getLocalVariables() {
    List<LocalVariableInfo> result = new LinkedList<LocalVariableInfo>();
    for (LocalVariable l : localVars.values()) {
      result.add(l);
    }
    return result;
  }

  public List<ParameterInfo> getParameters() {
    List<ParameterInfo> result = new LinkedList<ParameterInfo>();
    for (Parameter p : parameters) {
      result.add(p);
    }
    return result;
  }

  @Override
  public String toString() {
    StringBuilder nameBuilder = new StringBuilder(name.substring(0, name
        .indexOf(")") + 1));
    nameBuilder.insert(0, returnType.toString() + " ");
    return nameBuilder.toString();
  }

  public void addParameter(Parameter parameter) {
    parameters.add(parameter);
  }

  public void addLocalVariable(LocalVariable var) {
    localVars.put(var.getName(), var);
  }

  public List<Operation> getOperations() {
    // TODO: this probably needs fixing
    return operations;
  }

  public void addOperations(List<Operation> operations) {
    //TODO: this probably needs fixing
    this.operations.addAll(operations);
  }

  public void computeMetric(TestabilityContext testabilityContext) {
    throw new UnsupportedOperationException();
  }

  public ClassInfo getClassInfo() {
    throw new UnsupportedOperationException();
  }

  public String getFullName() {
    throw new UnsupportedOperationException();
  }

  public Variable getMethodThis() {
    throw new UnsupportedOperationException();
  }

  public int getStartingLineNumber() {
    throw new UnsupportedOperationException();
  }

  public long getTestCost() {
  //TODO: this should probably not be here
    throw new UnsupportedOperationException();
  }

  public Visibility getVisibility() {
    throw new UnsupportedOperationException();
  }

  public boolean isConstructor() {
    throw new UnsupportedOperationException();
  }

  public boolean isStatic() {
    throw new UnsupportedOperationException();
  }

  public boolean canOverride() {
    throw new UnsupportedOperationException();
  }

  public boolean isInstance() {
    throw new UnsupportedOperationException();
  }

  public boolean isStaticConstructor() {
    throw new UnsupportedOperationException();
  }


  public long getNonRecursiveCyclomaticComplexity() {
  //TODO: this should probably not be here
    throw new UnsupportedOperationException();
  }
}