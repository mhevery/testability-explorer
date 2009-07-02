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

import static com.google.test.metric.Reason.IMPLICIT_CONSTRUCTOR;
import static com.google.test.metric.Reason.IMPLICIT_SETTER;
import static com.google.test.metric.Reason.IMPLICIT_STATIC_INIT;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import com.google.test.metric.TestabilityVisitor.CostRecordingFrame;
import com.google.test.metric.TestabilityVisitor.Frame;

public class MetricComputer {

  private final ClassRepository classRepository;
  private final PrintStream err;
  private final WhiteList whitelist;
  private final int recordingDepth;

  public MetricComputer(ClassRepository classRepository, PrintStream err,
      WhiteList whitelist, int recordingDepth) {
    this.classRepository = classRepository;
    this.err = err;
    this.whitelist = whitelist;
    this.recordingDepth = recordingDepth;
  }

  public ClassCost compute(String name) {
    return compute(classRepository.getClass(name));
  }

  /**
   * Computing the ClassCost for a ClassInfo involves tallying up all the MethodCosts contained
   * in the class. Then an overall cost is calculated, based on the {@code CostModel} the metric
   * computer is using.
   *
   * @param clazz to compute the metric for.
   * @return classCost
   */
  public ClassCost compute(ClassInfo clazz) {
    List<MethodCost> methods = new ArrayList<MethodCost>();
    for (MethodInfo method : clazz.getMethods()) {
      methods.add(compute(method));
    }
    return new ClassCost(clazz.getName(), methods);
  }

  /**
   * Computing the MethodCost for a MethodInfo involves tallying up:
   * <ul><li>The cost in any static initialization blocks of the class which holds the method.</li>
   * <li>The cost of constructing the object.</li>
   * <li>Recognizing injectability through setter methods. This in most cases improves the score
   * unless you have lots of code in your setter.</li>
   * <li>The field costs</li>
   * <li>Lastly the costs are added up for all of the lines in this method. This includes the
   * transitive non-mockable/interceptable closure of all the costs of the methods that are called.</li>
   * <li></li></ul>
   *
   * @param method to compute the cost for.
   * @return MethodCost for this method, including the accumulated costs the methods it calls. This
   * MethodCost is guaranteed to have already been linked (sealed for adding additional costs).
   */
  public MethodCost compute(MethodInfo method) {
    TestabilityVisitor visitor = new TestabilityVisitor(classRepository, new VariableState(), err, whitelist);
    TestabilityVisitor.CostRecordingFrame frame = visitor.createFrame(method, recordingDepth);
    addStaticInitializationCost(method, frame);
    if (!method.isStatic() && !method.isConstructor()) {
      addConstructorCost(method, frame);
      addSetterInjection(method, frame);
    }
    addFieldCost(method, frame);
    return frame.applyMethodOperations();
  }



  /** Goes through all methods and adds an implicit cost for those beginning with "set" (assuming
   * to test the {@code baseMethod}'s class, you need to be able to call the setters for initialization.  */
  private void addSetterInjection(MethodInfo baseMethod, CostRecordingFrame frame) {
    for (MethodInfo setter : baseMethod.getSiblingSetters()) {
      frame.applyImplicitCost(setter, IMPLICIT_SETTER);
    }
  }

  /** Adds an implicit cost to all non-static methods for calling the constructor. (Because to test
   * any instance method, you must be able to instantiate the class.) Also marks parameters
   * injectable for the constructor with the most non-primitive parameters. */
  private void addConstructorCost(MethodInfo method, CostRecordingFrame frame) {
    MethodInfo constructor = method.getClassInfo().getConstructorWithMostNonPrimitiveParameters();
    if (constructor != null) {
      frame.applyImplicitCost(constructor, IMPLICIT_CONSTRUCTOR);
    }
  }

  /** Doesn't really add the field costs (there are none), but marks non-private fields as injectable. */
  private void addFieldCost(MethodInfo method,
      Frame frame) {
    for (FieldInfo field : method.getClassInfo().getFields()) {
      if (!field.isPrivate()) {
        frame.getGlobalVariables().setInjectable(field);
      }
    }
  }

   /** Includes the cost of all static initialization blocks, as well as static field assignments. */
  private void addStaticInitializationCost(MethodInfo baseMethod, CostRecordingFrame frame) {
    if (baseMethod.isStaticConstructor()) {
      return;
    }
    for (MethodInfo method : baseMethod.getClassInfo().getMethods()) {
      if (method.isStaticConstructor()) {
        frame.applyImplicitCost(method, IMPLICIT_STATIC_INIT);
      }
    }
  }

}
