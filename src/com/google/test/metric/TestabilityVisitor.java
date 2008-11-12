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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.test.metric.ViolationCost.Reason;
import com.google.test.metric.method.Constant;
import com.google.test.metric.method.op.turing.Operation;

public class TestabilityVisitor {

  public static class CostRecordingFrame extends Frame {

    private final MethodCost methodCost;
    private final Map<MethodInfo, MethodCost> methodCosts;
    private final int remainingDepth;

    public CostRecordingFrame(PrintStream err, ClassRepository classRepository,
        ParentFrame parentFrame, WhiteList whitelist,
        VariableState globalVariables, Map<MethodInfo, MethodCost> methodCosts,
        Set<MethodInfo> alreadyVisited, MethodInfo method, int remainingDepth) {
      super(err, classRepository, parentFrame, whitelist, globalVariables,
          alreadyVisited, method);
      this.methodCosts = methodCosts;
      this.remainingDepth = remainingDepth;
      this.methodCost = getMethodCostCache(method);
    }

    public CostRecordingFrame(PrintStream err, ClassRepository classRepository,
        WhiteList whitelist, VariableState globalVariables, MethodInfo method,
        int remainingDepth) {
      this(err, classRepository, new ParentFrame(globalVariables), whitelist,
          globalVariables, new HashMap<MethodInfo, MethodCost>(),
          new HashSet<MethodInfo>(), method, remainingDepth);
    }

    @Override
    protected void addCyclomaticCost(int lineNumber) {
      super.addCyclomaticCost(lineNumber);
      ViolationCost cost = new CyclomaticCost(lineNumber, Cost.cyclomatic(1));
      methodCost.addCostSource(cost);
    }

    @Override
    protected void addGlobalCost(int lineNumber, Variable variable) {
      super.addGlobalCost(lineNumber, variable);
      ViolationCost cost = new GlobalCost(lineNumber, variable, Cost.global(1));
      methodCost.addCostSource(cost);
    }

    @Override
    protected void addLoDCost(int lineNumber, MethodInfo method, int distance) {
      super.addLoDCost(lineNumber, method, distance);
      ViolationCost cost = new LoDViolation(lineNumber, method.getFullName(),
          Cost.lod(distance), distance);
      methodCost.addCostSource(cost);
    }

    @Override
    protected void addMethodInvocationCost(int lineNumber, MethodInfo to,
        Cost methodInvocationCost) {
      super.addMethodInvocationCost(lineNumber, to, methodInvocationCost);
      ViolationCost cost = new MethodInvokationCost(lineNumber,
          getMethodCostCache(to), Reason.NON_OVERRIDABLE_METHOD_CALL,
          methodInvocationCost);
      methodCost.addCostSource(cost);
    }

    public MethodCost getMethodCost() {
      return methodCost;
    }

    protected MethodCost getMethodCostCache(MethodInfo method) {
      MethodCost methodCost = methodCosts.get(method);
      if (methodCost == null) {
        methodCost = new MethodCost(method.getFullName(), method
            .getStartingLineNumber());
        methodCosts.put(method, methodCost);
      }
      return methodCost;
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
      recordNonOveridableMethodCall(lineNumber, implicitMethod, implicitMethod
          .getMethodThis(), implicitMethod.getParameters(), ret);
    }

    public MethodCost applyMethodOperations() {
      for (Integer lineNumberWithComplexity : method.getLinesOfComplexity()) {
        addCyclomaticCost(lineNumberWithComplexity);
      }
      if (method.getMethodThis() != null) {
        variableState.setInjectable(method.getMethodThis());
      }
      setInjectable(method.getParameters());
      Constant returnVariable = new Constant("rootReturn", JavaType.OBJECT);
      applyMethodOperations(-1, method, method.getMethodThis(), method
          .getParameters(), returnVariable);
      return methodCost;
    }

    @Override
    protected Frame createChildFrame(MethodInfo method) {
      if (remainingDepth == 0) {
        return super.createChildFrame(method);
      } else {
        return new CostRecordingFrame(err, classRepository, this, whitelist,
            globalVariableState, methodCosts, alreadyVisited, method,
            remainingDepth - 1);
      }
    }

    @Override
    void assignVariable(Variable destination, int lineNumber,
        ParentFrame sourceFrame, Variable source) {
      super.assignVariable(destination, lineNumber, sourceFrame, source);
      int loDCount = sourceFrame.variableState.getLoDCount(source);
      variableState.setLoDCount(destination, loDCount);
    }

    @Override
    protected void incrementLoD(int lineNumber, MethodInfo toMethod,
        Variable destination, Variable source, ParentFrame destinationFrame) {
      if (source != null) {
        int thisCount = variableState.getLoDCount(destination);
        int distance = thisCount + 1;
        destinationFrame.variableState.setLoDCount(source, distance);
        if (distance > 1) {
          destinationFrame.addLoDCost(lineNumber, toMethod, distance);
        }
      }
    }
  }

  public static class Frame extends ParentFrame {

    protected final ParentFrame parentFrame;
    protected final Cost direct = new Cost();
    protected final Cost indirect = new Cost();
    protected final MethodInfo method;
    protected Variable returnValue;
    protected final WhiteList whitelist;
    protected final PrintStream err;
    protected final ClassRepository classRepository;
    protected final Set<MethodInfo> alreadyVisited;

    public Frame(PrintStream err, ClassRepository classRepository,
        ParentFrame parentFrame, WhiteList whitelist,
        VariableState globalVariables, Set<MethodInfo> alreadyVisited,
        MethodInfo method) {
      super(globalVariables);
      this.err = err;
      this.classRepository = classRepository;
      this.parentFrame = parentFrame;
      this.whitelist = whitelist;
      this.alreadyVisited = alreadyVisited;
      this.method = method;
      alreadyVisited.add(method);
    }

    protected void addCyclomaticCost(int lineNumber) {
      direct.addCyclomaticCost(1);
    }

    protected void addGlobalCost(int lineNumber, Variable variable) {
      direct.addGlobalCost(1);
    }

    @Override
    protected void addLoDCost(int lineNumber, MethodInfo method, int distance) {
      direct.addLodDistance(distance);
    }

    protected void addMethodInvocationCost(int lineNumber, MethodInfo to,
        Cost methodInvocationCost) {
      indirect.add(methodInvocationCost);
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
          addGlobalCost(lineNumber, field);
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

    void assignVariable(Variable destination, int lineNumber,
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
    }

    int getLoDCount(Variable variable) {
      return variableState.getLoDCount(variable);
    }

    ParentFrame getParentFrame() {
      return parentFrame;
    }

    private Cost getTotalCost() {
      Cost totalCost = new Cost();
      totalCost.add(direct);
      totalCost.add(indirect);
      return totalCost;
    }

    public VariableState getVariableState() {
      return variableState;
    }

    protected void applyMethodOperations(int lineNumber, MethodInfo toMethod,
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
      incrementLoD(lineNumber, toMethod, methodThis, returnVariable, parentFrame);
    }

    private void recordMethodCall(int lineNumber, MethodInfo toMethod,
        Variable methodThis, List<? extends Variable> parameters,
        Variable returnVariable) {
      for (Integer lineNumberWithComplexity : toMethod.getLinesOfComplexity()) {
        addCyclomaticCost(lineNumberWithComplexity);
      }
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
        if (alreadyVisited.contains(toMethod)) {
          // Method already counted, skip (to prevent recursion)
          incrementLoD(lineNumber, toMethod, methodThis, returnVariable, parentFrame);
        } else if (toMethod.canOverride()
            && variableState.isInjectable(methodThis)) {
          // Method can be overridden / injectable
          recordOverridableMethodCall(lineNumber, toMethod, methodThis,
              returnVariable);
        } else {
          // Method can not be intercepted we have to add the cost
          // recursively
          recordNonOveridableMethodCall(lineNumber, toMethod, methodThis,
              parameters, returnVariable);
        }
      } catch (ClassNotFoundException e) {
        err.println("WARNING: class not found: " + clazzName);
      } catch (MethodNotFoundException e) {
        err.println("WARNING: method not found: " + e.getMethodName() + " in "
            + e.getClassInfo().getName());
      }
    }

    protected void recordNonOveridableMethodCall(int lineNumber,
        MethodInfo toMethod, Variable methodThis,
        List<? extends Variable> parameters, Variable returnVariable) {
      Frame childFrame = createChildFrame(toMethod);
      childFrame.recordMethodCall(lineNumber, toMethod, methodThis, parameters,
          returnVariable);
      addMethodInvocationCost(lineNumber, toMethod, childFrame.getTotalCost()
          .copyNoLOD());
    }

    protected Frame createChildFrame(MethodInfo toMethod) {
      return new Frame(err, classRepository, this, whitelist,
          getGlobalVariables(), alreadyVisited, toMethod);
    }

    private void recordOverridableMethodCall(int lineNumber,
        MethodInfo toMethod, Variable methodThis, Variable returnVariable) {
      if (returnVariable != null) {
        variableState.setInjectable(returnVariable);
        setReturnValue(returnVariable);
      }
      incrementLoD(lineNumber, toMethod, methodThis, returnVariable, this);
    }

    protected void incrementLoD(int lineNumber, MethodInfo toMethod,
        Variable destination, Variable source, ParentFrame destinationFrame) {
    }

    protected void setInjectable(List<? extends Variable> parameters) {
      for (Variable variable : parameters) {
        variableState.setInjectable(variable);
      }
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
      return "MethodCost: " + method + "\n" + super.toString();
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

  public CostRecordingFrame createFrame(MethodInfo method, int recordingDepth) {
    return new CostRecordingFrame(err, classRepository, whitelist,
        globalVariables, method, recordingDepth);
  }

  @Override
  public String toString() {
    StringBuilder buf = new StringBuilder();
    buf.append("MethodCost:");
    buf.append("\n==============\nROOT FRAME:\n" + globalVariables);
    return buf.toString();
  }

}
