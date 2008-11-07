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

  public class Frame {

    private final Frame parentFrame;
    private final MethodCost methodCost;
    private final Map<Variable, Integer> lodCount = new HashMap<Variable, Integer>();
    private final Set<Variable> injectables = new HashSet<Variable>();
    private final Set<Variable> globals = new HashSet<Variable>();
    private Variable returnValue;

    public Frame(Frame parentFrame, MethodCost methodCost) {
      this.parentFrame = parentFrame;
      this.methodCost = methodCost;
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
      int thisCount = getLoDCount(methodThis);
      parentFrame.recordLoDDispatch(lineNumber, toMethod, returnVariable,
          thisCount + 1);
    }

    /**
     * If and only if the array is a static, then add it as a Global State Cost
     * for the {@code inMethod}.
     */
    public void assignArray(Variable array, Variable index, Variable value,
        int lineNumber) {
      if (globals.contains(array)) {
        methodCost.addGlobalCost(lineNumber, array);
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
      if (fieldInstance == null || globals.contains(fieldInstance)) {
        if (!field.isFinal()) {
          inMethod.addGlobalCost(lineNumber, fieldInstance);
        }
        globals.add(field);
      }
    }

    public void assignLocal(int lineNumber, Variable destination,
        Variable source) {
      assignVariable(methodCost, lineNumber, destination, this, source);
    }

    public void assignParameter(MethodInfo inMethod, int lineNumber,
        Variable destination, Frame sourceFrame, Variable source) {
      MethodCost inMethodCost = TestabilityVisitor.this.getMethodCost(inMethod);
      assignVariable(inMethodCost, lineNumber, destination, sourceFrame, source);
    }

    public void assignReturnValue(MethodInfo inMethod, int lineNumber,
        Variable destination) {
      assignVariable(TestabilityVisitor.this.getMethodCost(inMethod),
          lineNumber, destination, this, returnValue);
    }

    private void assignVariable(MethodCost inMethod, int lineNumber,
        Variable destination, Frame sourceFrame, Variable source) {
      if (sourceFrame.isInjectable(source)) {
        setInjectable(destination);
      }
      if (destination.isGlobal() || sourceFrame.isGlobal(source)) {
        setGlobal(destination);
        if (source instanceof LocalField && !source.isFinal()) {
          inMethod.addGlobalCost(lineNumber, source);
        }
      }
      setLoDCount(destination, sourceFrame.getLoDCount(source));
    }

    public int getLoDCount(Variable variable) {
      Integer count = lodCount.get(variable);
      if (count == null) {
        if (variable instanceof LocalField) {
          LocalField localField = (LocalField) variable;
          return getLoDCount(localField.getField());
        } else {
          return 0;
        }
      } else {
        return count.intValue();
      }
    }

    public MethodInfo getMethod(String clazzName, String methodName) {
      return classRepository.getClass(clazzName).getMethod(methodName);
    }

    public Frame getParentFrame() {
      return parentFrame;
    }

    public boolean isClassWhiteListed(String clazzName) {
      return whitelist.isClassWhiteListed(clazzName);
    }

    public boolean isGlobal(Variable var) {
      if (var == null) {
        return false;
      }
      if (var.isGlobal()) {
        return true;
      }
      if (globals.contains(var)) {
        return true;
      }
      if (var instanceof LocalField) {
        LocalField field = (LocalField) var;
        return isGlobal(field.getInstance()) || isGlobal(field.getField());
      }
      if (parentFrame == null) {
        return false;
      }
      return rootFrame.isGlobal(var);
    }

    public boolean isInjectable(Variable var) {
      if (injectables.contains(var)) {
        return true;
      } else {
        if (var instanceof LocalField) {
          return isInjectable(((LocalField) var).getField());
        } else {
          return injectables.contains(var) ? true : parentFrame != null
              && rootFrame.isInjectable(var);
        }
      }
    }

    public void recordLoDDispatch(int lineNumber, MethodInfo method,
        Variable variable, int distance) {
      setLoDCount(variable, distance);
      if (distance > 1) {
        methodCost.addCostSource(new LoDViolation(lineNumber, method
            .getFullName(), distance));
      }
    }

    public void recordNonOverridableMethodCall(int lineNumber,
        MethodInfo toMethod, Variable methodThis,
        List<? extends Variable> parameters, Variable returnVariable) {
      MethodCost to = TestabilityVisitor.this.getMethodCost(toMethod);
      if (methodCost == to) {
        // Prevent recursion.
        return;
      }
      methodCost.addMethodCost(lineNumber, to,
          Reason.NON_OVERRIDABLE_METHOD_CALL);
      Frame currentFrame = new Frame(this, to);
      if (toMethod.isInstance()) {
        currentFrame.assignParameter(toMethod, lineNumber, toMethod
            .getMethodThis(), currentFrame.parentFrame, methodThis);
      }
      currentFrame.applyMethodOperations(lineNumber, toMethod, methodThis,
          parameters, returnVariable);
      currentFrame.assignReturnValue(toMethod, lineNumber, returnVariable);
    }

    public void recordOverridableMethodCall(int lineNumber,
        MethodInfo toMethod, Variable methodThis, Variable returnVariable) {
      if (returnVariable != null) {
        setInjectable(returnVariable);
        setReturnValue(returnVariable);
      }
      if (returnVariable != null) {
        int thisCount = getLoDCount(methodThis);
        recordLoDDispatch(lineNumber, toMethod, returnVariable, thisCount + 1);
      }
    }

    public void reportError(String errorMessage) {
      err.println(errorMessage);
    }

    void setGlobal(Variable var) {
      globals.add(var);
    }

    public void setInjectable(List<? extends Variable> parameters) {
      for (Variable variable : parameters) {
        setInjectable(variable);
      }
    }

    public void setInjectable(MethodInfo method) {
      if (!method.isStatic()) {
        setInjectable(method.getMethodThis());
      }
      setInjectable(method.getParameters());
    }

    void setInjectable(Variable var) {
      if (parentFrame != null
          && (var instanceof LocalField || var instanceof FieldInfo)) {
        rootFrame.setInjectable(var);
      } else {
        injectables.add(var);
      }
    }

    void setLoDCount(Variable value, int newCount) {
      Integer count = lodCount.get(value);
      int intCount = count == null ? 0 : count;
      if (intCount < newCount) {
        lodCount.put(value, newCount);
      }
    }

    public void setReturnValue(Variable value) {
      boolean isWorse = isGlobal(value) && !isGlobal(returnValue);
      if (isWorse) {
        returnValue = value;
      }
    }

    @Override
    public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append("MethodCost: " + methodCost);
      buf.append("\nInjectables:");
      for (Variable var : injectables) {
        buf.append("\n   ");
        buf.append(var);
      }
      buf.append("\nGlobals:");
      for (Variable var : globals) {
        buf.append("\n   ");
        buf.append(var);
      }
      buf.append("\nLod:");
      for (Variable var : lodCount.keySet()) {
        buf.append("\n   ");
        buf.append(var);
        buf.append(": ");
        buf.append(lodCount.get(var));
      }
      return buf.toString();
    }

    public boolean wasMethodAlreadyVisited(MethodInfo toMethod) {
      return methodCosts.containsKey(toMethod);
    }

  }

  // TODO: refactor me. The root frame needs to be of different class so that
  // we can remove all of the ifs in Frame
  private final Frame rootFrame = new Frame(null, null);
  private final ClassRepository classRepository;
  private final Map<MethodInfo, MethodCost> methodCosts = new HashMap<MethodInfo, MethodCost>();
  private final PrintStream err;
  private final WhiteList whitelist;
  private final CostModel costModel;

  public TestabilityVisitor(ClassRepository classRepository, PrintStream err,
      WhiteList whitelist, CostModel costModel) {
    this.classRepository = classRepository;
    this.err = err;
    this.costModel = costModel;
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
    Frame currentFrame = new Frame(rootFrame, getMethodCost(method));

    if (method.getMethodThis() != null) {
      currentFrame.setInjectable(method.getMethodThis());
    }
    currentFrame.setInjectable(method.getParameters());
    currentFrame.applyMethodOperations(-1, method, method.getMethodThis(),
        method.getParameters(), new Constant("rootReturn", Type.OBJECT));

    return currentFrame;
  }

  /**
   * Looks up the MethodCost and returns the cached one, or a new one is created
   * for this method. Then link() is called. Note: this returns the linked
   * method cost only because some tests require linking (and don't go through
   * the usual route of ClassCost#link().
   */
  public MethodCost getLinkedMethodCost(MethodInfo method) {
    MethodCost cost = getMethodCost(method);
    cost.link(costModel);
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

  public Frame getRootFrame() {
    return rootFrame;
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
    buf.append("\n==============\nROOT FRAME:\n" + rootFrame);
    return buf.toString();
  }

}
