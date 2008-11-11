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

import com.google.test.metric.ViolationCost.Reason;
import com.google.test.metric.method.Constant;
import com.google.test.metric.method.op.turing.Operation;

public class TestabilityVisitor {

  public static class Frame extends ParentFrame {

    private final ParentFrame parentFrame;
    private final MethodCost methodCost;
    private final Cost direct = Cost.none();
    private final Cost indirect = Cost.none();
    private final MethodInfo method;
    private final HashMap<MethodInfo, MethodCost> methodCosts;
    private Variable returnValue;
    private final WhiteList whitelist;
    private final PrintStream err;
    private final ClassRepository classRepository;

    public Frame(PrintStream err, ClassRepository classRepository,
        ParentFrame parentFrame, WhiteList whitelist,
        VariableState globalVariables,
        HashMap<MethodInfo, MethodCost> methodCosts, MethodInfo method) {
      super(globalVariables);
      this.err = err;
      this.classRepository = classRepository;
      this.parentFrame = parentFrame;
      this.whitelist = whitelist;
      this.methodCosts = methodCosts;
      this.method = method;
      this.methodCost = getMethodCostCache(method);
      for (Integer lineNumberWithComplexity : method.getLinesOfComplexity()) {
        addCyclomaticCost(lineNumberWithComplexity);
      }
    }

    public Frame(PrintStream err, ClassRepository classRepository,
        WhiteList whitelist, VariableState globalVariables, MethodInfo method) {
      this(err, classRepository, new ParentFrame(globalVariables), whitelist,
          globalVariables, new HashMap<MethodInfo, MethodCost>(), method);
    }

    protected void addCyclomaticCost(int lineNumber) {
      Cost cyclomaticCost = Cost.cyclomatic(1);
      direct.add(cyclomaticCost);
      ViolationCost cost = new CyclomaticCost(lineNumber, cyclomaticCost);
      methodCost.addCostSource(cost);
    }

    protected void addGlobalCost(int lineNumber, Variable variable) {
      Cost globalCost = Cost.global(1);
      direct.add(globalCost);
      ViolationCost cost = new GlobalCost(lineNumber, variable, globalCost);
      methodCost.addCostSource(cost);
    }

    @Override
    protected void addLoDCost(int lineNumber, MethodInfo method, int distance) {
      Cost lodCost = Cost.lod(distance);
      direct.add(lodCost);
      ViolationCost cost = new LoDViolation(lineNumber, method.getFullName(),
          lodCost, distance);
      methodCost.addCostSource(cost);
    }

    protected void addMethodInvocationCost(int lineNumber, MethodCost to,
        Cost methodInvocationCost) {
      indirect.add(methodInvocationCost);
      ViolationCost cost = new MethodInvokationCost(lineNumber, to,
          Reason.NON_OVERRIDABLE_METHOD_CALL, methodInvocationCost);
      methodCost.addCostSource(cost);
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
     * @param implicitMethod
     *          the method that is getting called by {@code from} and
     *          contributes cost transitively.
     * @param costSourceType
     *          the type of implicit cost to record, for giving the user
     *          information about why they have the costs they have.
     * @return
     */
    public void applyImplicitCost(MethodInfo implicitMethod, Reason reason) {
      if (implicitMethod.getMethodThis() != null) {
        variableState.setInjectable(implicitMethod.getMethodThis());
      }
      setInjectable(implicitMethod.getParameters());
      Constant ret = new Constant("return", JavaType.OBJECT);
      int lineNumber = implicitMethod.getStartingLineNumber();
      Frame childFrame = new Frame(err, classRepository, this, whitelist,
          variableState.getGlobalVariableState(), methodCosts, implicitMethod);
      childFrame.recordMethodCall(lineNumber, implicitMethod, implicitMethod
          .getMethodThis(), implicitMethod.getParameters(), ret);
      addMethodInvocationCost(lineNumber, getMethodCostCache(implicitMethod),
          childFrame.getTotalCost());
    }

    public MethodCost applyMethodOperations() {
      if (method.getMethodThis() != null) {
        variableState.setInjectable(method.getMethodThis());
      }
      setInjectable(method.getParameters());
      Constant returnVariable = new Constant("rootReturn", JavaType.OBJECT);
      applyMethodOperations(-1, method, method.getMethodThis(), method
          .getParameters(), returnVariable);
      return methodCost;
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
        assignParameter(lineNumber, toMethod.getParameters().get(i++),
            parentFrame, var);
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
      assignVariable(field, lineNumber, this, value);
      if (fieldInstance == null || variableState.isGlobal(fieldInstance)) {
        if (!field.isFinal()) {
          addGlobalCost(lineNumber, fieldInstance);
        }
        variableState.setGlobal(field);
      }
    }

    public void assignLocal(int lineNumber, Variable destination,
        Variable source) {
      assignVariable(destination, lineNumber, this, source);
    }

    void assignParameter(int lineNumber, Variable destination,
        ParentFrame sourceFrame, Variable source) {
      assignVariable(destination, lineNumber, sourceFrame, source);
    }

    public void assignReturnValue(int lineNumber, Variable destination) {
      assignVariable(destination, lineNumber, this, returnValue);
    }

    private void assignVariable(Variable destination, int lineNumber,
        ParentFrame sourceFrame, Variable source) {
      if (sourceFrame.variableState.isInjectable(source)) {
        variableState.setInjectable(destination);
      }
      if (destination.isGlobal() || sourceFrame.variableState.isGlobal(source)) {
        variableState.setGlobal(destination);
        if (source instanceof LocalField && !source.isFinal()) {
          addGlobalCost(lineNumber, source);
        }
      }
      int loDCount = sourceFrame.variableState.getLoDCount(source);
      variableState.setLoDCount(destination, loDCount);
    }

    int getLoDCount(Variable variable) {
      return variableState.getLoDCount(variable);
    }

    public MethodCost getMethodCost() {
      return methodCost;
    }

    private MethodCost getMethodCostCache(MethodInfo method) {
      MethodCost methodCost = methodCosts.get(method);
      if (methodCost == null) {
        methodCost = new MethodCost(method.getFullName(), method
            .getStartingLineNumber());
        methodCosts.put(method, methodCost);
      }
      return methodCost;
    }

    public ParentFrame getParentFrame() {
      return parentFrame;
    }

    private Cost getTotalCost() {
      Cost totalCost = Cost.none();
      totalCost.add(direct);
      totalCost.add(indirect);
      return totalCost;
    }

    public VariableState getVariableState() {
      return variableState;
    }

    private void recordMethodCall(int lineNumber, MethodInfo toMethod,
        Variable methodThis, List<? extends Variable> parameters,
        Variable returnVariable) {
      if (toMethod.isInstance()) {
        assignParameter(lineNumber, toMethod.getMethodThis(), parentFrame,
            methodThis);
      }
      applyMethodOperations(lineNumber, toMethod, methodThis, parameters,
          returnVariable);
      assignReturnValue(lineNumber, returnVariable);
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
          Frame childFrame = new Frame(err, classRepository, this, whitelist,
              getGlobalVariables(), methodCosts, toMethod);
          childFrame.recordMethodCall(lineNumber, toMethod, methodThis,
              parameters, returnVariable);
          addMethodInvocationCost(lineNumber, getMethodCostCache(toMethod),
              childFrame.getTotalCost().copyNoLOD());
        }
      } catch (ClassNotFoundException e) {
        err.println("WARNING: class not found: " + clazzName);
      } catch (MethodNotFoundException e) {
        err.println("WARNING: method not found: " + e.getMethodName() + " in "
            + e.getClassInfo().getName());
      }
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

  public static class ParentFrame {
    protected final VariableState globalVariableState;
    protected final LocalVariableState variableState;

    public ParentFrame(VariableState globalVariableState) {
      this.globalVariableState = globalVariableState;
      this.variableState = new LocalVariableState(globalVariableState);
    }

    protected void addLoDCost(int lineNumber, MethodInfo toMethod, int distance) {
    }

    public VariableState getGlobalVariables() {
      return globalVariableState;
    }

  }

  // TODO: refactor me. The root frame needs to be of different class so that
  // we can remove all of the ifs in Frame
  private final VariableState globalVariables;
  private final ClassRepository classRepository;
  private final PrintStream err;
  private final WhiteList whitelist;

  public TestabilityVisitor(ClassRepository classRepository,
      VariableState variableState, PrintStream err, WhiteList whitelist) {
    this.classRepository = classRepository;
    this.globalVariables = variableState;
    this.err = err;
    this.whitelist = whitelist;
  }

  public Frame createFrame(MethodInfo method) {
    return new Frame(err, classRepository, whitelist, globalVariables, method);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("MethodCost:");
    buf.append("\n==============\nROOT FRAME:\n" + globalVariables);
    return buf.toString();
  }

}
