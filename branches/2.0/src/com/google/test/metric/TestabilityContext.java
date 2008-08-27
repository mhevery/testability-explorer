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
package com.google.test.metric;

import com.google.test.metric.ast.AbstractSyntaxTree;
import com.google.test.metric.ast.FieldHandle;
import com.google.test.metric.ast.MethodInfo;
import com.google.test.metric.ast.MockVisitor;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestabilityContext {

  private final Set<Variable> injectables = new HashSet<Variable>();
  private final Set<Variable> statics = new HashSet<Variable>();
  private final ClassRepository classRepository;
  private final Map<MethodInfo, MethodCost> methodCosts = new HashMap<MethodInfo, MethodCost>();
  private final PrintStream err;
  private final WhiteList whitelist;
  private final CostModel linkContext;
  private Variable returnValue;
//  private final GrayList graylist;

  public TestabilityContext(ClassRepository classRepository, PrintStream err,
    WhiteList whitelist, CostModel linkContext) {
    this.classRepository = classRepository;
    this.err = err;
//    this.graylist = null;
    this.linkContext = linkContext;
    this.whitelist = whitelist;
  }

  public MethodInfo getMethod(String clazzName, String methodName) {
    AbstractSyntaxTree ast = new AbstractSyntaxTree();
    JavaParser repo = new JavaParser(ast);
    repo.getClass(Object.class); // pre-cache for easier debugging.
    repo.getClass(clazzName);
    MockVisitor v = new MockVisitor();
    ast.accept(v);
    return v.getClassInfo(clazzName).getMethod(methodName);

    //return classRepository.getClass(clazzName).getMethod(methodName);
  }

  public boolean methodAlreadyVisited(MethodInfo method) {
    return methodCosts.containsKey(method);
  }

  public void recordMethodCall(MethodInfo fromMethod, int fromLineNumber,
      MethodInfo toMethod) {
    MethodCost from = getMethodCost(fromMethod);
    MethodCost to = getMethodCost(toMethod);
    if (from != to) {
      from.addMethodCost(fromLineNumber, to);
      toMethod.computeMetric(this);
    }
  }

  public MethodCost getLinkedMethodCost(MethodInfo method) {
    MethodCost cost = getMethodCost(method);
    cost.link(linkContext);
    return cost;
  }

  MethodCost getMethodCost(MethodInfo method) {
    MethodCost methodCost = methodCosts.get(method);
    if (methodCost == null) {
      methodCost = new MethodCost(method.getFullName(), method.getStartingLineNumber(), method.getTestCost());
      methodCosts.put(method, methodCost);
    }
    return methodCost;
  }

  public void implicitCost(MethodInfo from, MethodInfo to) {
    int line = to.getStartingLineNumber();
    getMethodCost(from).addMethodCost(line, getMethodCost(to));
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("MethodCost:");
    for (MethodCost cost : methodCosts.values()) {
      buf.append("  ");
      buf.append(cost);
      buf.append("\n");
    }
    buf.append("\nInjectables:");
    for (Variable var : injectables) {
      buf.append("\n   ");
      buf.append(var);
    }
    buf.append("\nGlobals:");
    for (Variable var : statics) {
      buf.append("\n   ");
      buf.append(var);
    }
    return buf.toString();
  }

  public void setInjectable(List<? extends Variable> parameters) {
    for (Variable variable : parameters) {
      setInjectable(variable);
    }
  }

  public void setInjectable(MethodInfo method) {
    if (method.getMethodThis() != null) {
      setInjectable(method.getMethodThis());
    }
    setInjectable(method.getParameters());
  }

  public void localAssignment(MethodInfo inMethod, int lineNumber,
      Variable destination, Variable source) {
    if (isInjectable(source)) {
      setInjectable(destination);
    }
    if (destination.isGlobal() || isGlobal(source)) {
      setGlobal(destination);
      if (source instanceof LocalField && !source.isFinal()) {
        getMethodCost(inMethod).addGlobalCost(lineNumber, source);
      }
    }
  }

  public boolean isGlobal(Variable var) {
    if (var instanceof LocalField) {
      LocalField field = (LocalField) var;
      return isGlobal(field.getInstance()) || isGlobal(field.getField());
    }
    return var != null && (var.isGlobal() || statics.contains(var));
  }

  public void fieldAssignment(Variable fieldInstance, FieldHandle field,
      Variable value, MethodInfo inMethod, int lineNumber) {
    localAssignment(inMethod, lineNumber, field, value);
    if (fieldInstance == null || statics.contains(fieldInstance)) {
      if (!field.isFinal()) {
        getMethodCost(inMethod).addGlobalCost(lineNumber, fieldInstance);
      }
      statics.add(field);
    }
  }

  public void arrayAssignment(Variable array, Variable index, Variable value,
      MethodInfo inMethod, int lineNumber) {
    if (statics.contains(array)) {
      getMethodCost(inMethod).addGlobalCost(lineNumber, array);
    }
  }

  public void setGlobal(Variable var) {
    statics.add(var);
  }

  public boolean isInjectable(Variable var) {
    if (var instanceof LocalField) {
      return isInjectable(((LocalField)var).getField());
    }
    return injectables.contains(var);
  }

  public void setInjectable(Variable var) {
    injectables.add(var);
  }

  public void reportError(String errorMessage) {
    err.println(errorMessage);
  }

  public WhiteList getWhitelist() {
    return whitelist;
  }

  public boolean isClassWhiteListed(String className) {
    return whitelist.isClassWhiteListed(className);
  }

  public void setReturnValue(Variable value) {
    this.returnValue = value;
  }

  public Variable getReturnValue() {
    return returnValue;
  }
}
