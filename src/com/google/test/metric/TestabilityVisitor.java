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

import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.test.metric.ViolationCost.Reason;
import com.google.test.metric.method.Constant;
import com.google.test.metric.method.op.turing.Operation;

public class TestabilityVisitor {

  public class Frame {

    private final Frame parentFrame;
    private final MethodCost methodCost;
    private final LocalVariableState variableState;
    private Variable returnValue;

    public Frame(Frame parentFrame, VariableState globalVariables,
        MethodCost methodCost) {
      this.parentFrame = parentFrame;
      this.variableState = new LocalVariableState(globalVariables);
      this.methodCost = methodCost;
    }

    protected void addGlobalCost(int lineNumber, Variable variable) {
      methodCost.addCostSource(new GlobalCost(lineNumber, variable));
    }

    protected void addLoDCost(int lineNumber, MethodInfo method, int distance) {
      methodCost.addCostSource(new LoDViolation(lineNumber, method
          .getFullName(), distance));
    }

    protected void addMethodInvocationCost(int lineNumber, MethodCost to) {
      methodCost.addCostSource(new MethodInvokationCost(lineNumber, to,
          Reason.NON_OVERRIDABLE_METHOD_CALL));
    }

    private void applyMethodOperations(int lineNumber, MethodInfo toMethod,
        Variable methodThis, List<? extends Variable> parameters,
        Variable returnVariable) {
      if (parameters.size() != toMethod.getParameters().size()) {
        throw new IllegalStateException(
            "Argument count does not match method parameter count.");
      }
      int i = 0;
      for (Variable var : parameters) {
        assignParameter(toMethod, lineNumber,
            toMethod.getParameters().get(i++), parentFrame, var);
      }
      returnValue = null;
      for (Operation operation : toMethod.getOperations()) {
        operation.visit(this);
      }
      int thisCount = variableState.getLoDCount(methodThis);
      int distance = thisCount + 1;
      parentFrame.variableState.setLoDCount(returnVariable, distance);
      if (distance > 1) {
        parentFrame.addLoDCost(lineNumber, toMethod, distance);
      }
    }

    /**
     * If and only if the array is a static, then add it as a Global State Cost
     * for the {@code inMethod}.
     */
    public void assignArray(Variable array, Variable index, Variable value,
        int lineNumber) {
      if (variableState.isGlobal(array)) {
        addGlobalCost(lineNumber, array);
      }
    }

    /**
     * The method propagates the global property of a field onto any field it is
     * assigned to. The globality is propagated because global state is
     * transitive (static cling) So any modification on class which is
     * transitively global should also be penalized.
     * 
     * <p>
     * Note: <em>final</em> static fields are not added, because they are
     * assumed to be constants, thus this will miss some actual global state.
     * (The justification is that if costs were included for constants it would
     * penalize people for a good practice -- removing magic values from code).
     */
    public void assignField(Variable fieldInstance, FieldInfo field,
        Variable value, int lineNumber) {
      MethodCost inMethod = methodCost;
      assignVariable(inMethod, lineNumber, field, this, value);
      if (fieldInstance == null || variableState.isGlobal(fieldInstance)) {
        if (!field.isFinal()) {
          inMethod.addGlobalCost(lineNumber, fieldInstance);
        }
        variableState.setGlobal(field);
      }
    }

    public void assignLocal(int lineNumber, Variable destination,
        Variable source) {
      assignVariable(methodCost, lineNumber, destination, this, source);
    }

    public void assignParameter(MethodInfo inMethod, int lineNumber,
        Variable destination, Frame sourceFrame, Variable source) {
      MethodCost inMethodCost = getMethodCost(inMethod);
      assignVariable(inMethodCost, lineNumber, destination, sourceFrame, source);
    }

    public void assignReturnValue(MethodInfo inMethod, int lineNumber,
        Variable destination) {
      MethodCost inMethodCost = getMethodCost(inMethod);
      assignVariable(inMethodCost, lineNumber, destination, this, returnValue);
    }

    private void assignVariable(MethodCost inMethod, int lineNumber,
        Variable destination, Frame sourceFrame, Variable source) {
      if (sourceFrame.variableState.isInjectable(source)) {
        variableState.setInjectable(destination);
      }
      if (destination.isGlobal() || sourceFrame.variableState.isGlobal(source)) {
        variableState.setGlobal(destination);
        if (source instanceof LocalField && !source.isFinal()) {
          inMethod.addGlobalCost(lineNumber, source);
        }
      }
      variableState.setLoDCount(destination, sourceFrame.variableState
          .getLoDCount(source));
    }

    int getLoDCount(Variable variable) {
      return variableState.getLoDCount(variable);
    }

    public VariableState getVariableState() {
      return variableState;
    }

    public void recordMethodCall(String clazzName, int lineNumber,
        String methodName, Variable methodThis, List<Variable> parameters,
        Variable returnVariable) {
      try {
        if (whitelist.isClassWhiteListed(clazzName)) {
          return;
        }
        MethodInfo toMethod = classRepository.getClass(clazzName).getMethod(
            methodName);
        if (methodCosts.containsKey(toMethod)) {
          // Method already counted, skip (to prevent recursion)
          if (returnVariable != null) {
            int thisCount = variableState.getLoDCount(methodThis);
            int distance = thisCount + 1;
            variableState.setLoDCount(returnVariable, distance);
            if (distance > 1) {
              addLoDCost(lineNumber, toMethod, distance);
            }
          }
          return;
        } else if (toMethod.canOverride()
            && variableState.isInjectable(methodThis)) {
          // Method can be overridden / injectable
          recordOverridableMethodCall(lineNumber, toMethod, methodThis,
              returnVariable);
        } else {
          // Method can not be intercepted we have to add the cost
          // recursively
          recordNonOverridableMethodCall(lineNumber, toMethod, methodThis,
              parameters, returnVariable);
        }
      } catch (ClassNotFoundException e) {
        err.println(("WARNING: class not found: " + clazzName));
      } catch (MethodNotFoundException e) {
        err
            .println(("WARNING: method not found: " + e.getMethodName()
                + " in " + e.getClassInfo().getName()));
      }
    }

    private void recordNonOverridableMethodCall(int lineNumber,
        MethodInfo toMethod, Variable methodThis,
        List<? extends Variable> parameters, Variable returnVariable) {
      MethodCost to = getMethodCost(toMethod);
      if (methodCost == to) {
        // Prevent recursion.
        return;
      }
      addMethodInvocationCost(lineNumber, to);
      Frame currentFrame = new Frame(this, variableState
          .getGlobalVariableState(), to);
      if (toMethod.isInstance()) {
        currentFrame.assignParameter(toMethod, lineNumber, toMethod
            .getMethodThis(), currentFrame.parentFrame, methodThis);
      }
      currentFrame.applyMethodOperations(lineNumber, toMethod, methodThis,
          parameters, returnVariable);
      currentFrame.assignReturnValue(toMethod, lineNumber, returnVariable);
    }

    private void recordOverridableMethodCall(int lineNumber,
        MethodInfo toMethod, Variable methodThis, Variable returnVariable) {
      if (returnVariable != null) {
        variableState.setInjectable(returnVariable);
        setReturnValue(returnVariable);
      }
      if (returnVariable != null) {
        int thisCount = variableState.getLoDCount(methodThis);
        int distance = thisCount + 1;
        variableState.setLoDCount(returnVariable, distance);
        if (distance > 1) {
          addLoDCost(lineNumber, toMethod, distance);
        }
      }
    }

    void setInjectable(List<? extends Variable> parameters) {
      for (Variable variable : parameters) {
        variableState.setInjectable(variable);
      }
    }

    void setInjectable(MethodInfo method) {
      if (!method.isStatic()) {
        variableState.setInjectable(method.getMethodThis());
      }
      setInjectable(method.getParameters());
    }

    public void setReturnValue(Variable value) {
      boolean isWorse = variableState.isGlobal(value)
          && !variableState.isGlobal(returnValue);
      if (isWorse) {
        returnValue = value;
      }
    }

    @Override
    public String toString() {
      return "MethodCost: " + methodCost + "\n" + super.toString();
    }

  }

  // TODO: refactor me. The root frame needs to be of different class so that
  // we can remove all of the ifs in Frame
  private final VariableState globalVariables = new VariableState();
  private final ClassRepository classRepository;
  private final Map<MethodInfo, MethodCost> methodCosts = new HashMap<MethodInfo, MethodCost>();
  private final PrintStream err;
  private final WhiteList whitelist;

  public TestabilityVisitor(ClassRepository classRepository, PrintStream err,
      WhiteList whitelist) {
    this.classRepository = classRepository;
    this.err = err;
    this.whitelist = whitelist;
  }

  /**
   * Implicit costs are added to the {@code from} method's costs when it is
   * assumed that the costs must be incurred in order for the {@code from}
   * method to execute. Example:
   * 
   * <pre>
   * void fromMethod() {
   *   this.someObject.toMethod();
   * }
   * </pre>
   * <p>
   * We would add the implicit cost of the toMethod() to the fromMethod().
   * Implicit Costs consist of:
   * <ul>
   * <li>Cost of construction for the someObject field referenced in
   * fromMethod()</li>
   * <li>Static initialization blocks in someObject
   * </ul>
   * <li>The cost of calling all the methods starting with "set" on
   * someObject.</ul>
   * <li>Note that the same implicit costs apply for the class that has the
   * fromMethod. (Meaning a method will always have the implicit costs of the
   * containing class and super-classes at a minimum).</li> </ul>
   * 
   * @param from
   *          the method that we are adding the implicit cost upon.
   * @param to
   *          the method that is getting called by {@code from} and contributes
   *          cost transitively.
   * @param costSourceType
   *          the type of implicit cost to record, for giving the user
   *          information about why they have the costs they have.
   */
  public void applyImplicitCost(MethodInfo from, MethodInfo to,
      Reason costSourceType) {
    int lineNumber = to.getStartingLineNumber();
    MethodCost methodCost = getMethodCost(from);
    MethodCost toMethodCost = getMethodCost(to);
    methodCost.addMethodCost(lineNumber, toMethodCost, costSourceType);

    applyMethodOperations(to);
  }

  public Frame applyMethodOperations(MethodInfo method) {
    Frame currentFrame = new Frame(new Frame(null, globalVariables, null),
        globalVariables, getMethodCost(method));

    if (method.getMethodThis() != null) {
      currentFrame.variableState.setInjectable(method.getMethodThis());
    }
    currentFrame.setInjectable(method.getParameters());
    currentFrame.applyMethodOperations(-1, method, method.getMethodThis(),
        method.getParameters(), new Constant("rootReturn", JavaType.OBJECT));

    return currentFrame;
  }

  public VariableState getGlobalVariables() {
    return globalVariables;
  }

  /**
   * Looks up the MethodCost and returns the cached one, or a new one is created
   * for this method. Then link() is called. Note: this returns the linked
   * method cost only because some tests require linking (and don't go through
   * the usual route of ClassCost#link().
   */
  public MethodCost getLinkedMethodCost(MethodInfo method) {
    MethodCost cost = getMethodCost(method);
    cost.link();
    return cost;
  }

  MethodCost getMethodCost(MethodInfo method) {
    MethodCost methodCost = methodCosts.get(method);
    if (methodCost == null) {
      methodCost = new MethodCost(method.getFullName(), method
          .getStartingLineNumber());
      for (Integer lineNumberWithComplexity : method.getLinesOfComplexity()) {
        methodCost.addCyclomaticCost(lineNumberWithComplexity);
      }
      methodCosts.put(method, methodCost);
    }
    return methodCost;
  }

  // Temporary for testing
  Frame newFrame(Frame parent, MethodCost method) {
    return new Frame(parent, globalVariables, method);
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
    buf.append("\n==============\nROOT FRAME:\n" + globalVariables);
    return buf.toString();
  }

}
