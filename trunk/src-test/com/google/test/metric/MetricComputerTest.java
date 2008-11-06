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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import com.google.test.metric.report.DrillDownReport;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

public class MetricComputerTest extends AutoFieldClearTestCase {

  private MetricComputerJavaDecorator computer;
  private final ClassRepository repo = new JavaClassRepository();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    computer = new MetricComputerJavaDecorator(toDecorate, repo);
  }

  public static class Medium {
    public Medium() {
      statiCost1();
      cost2();
    }

    /**
     * I cost 1
     */
    public static int statiCost1() {
      int i = 0;
      return i > 0 ? 1 : 2;
    }

    /**
     * I cost 2, but I am instance method so I can be overridden. so my cost may
     * be avoided in most cases.
     */
    public int cost2() {
      int i = 0;
      return i > 0 ? i > 1 ? 1 : 2 : 2;
    }

    /**
     * I cost 2, but I am a <em>final</em> instance method that can not be overridden.
     * My cost is unavoidable.
     */
    public final int finalCost2() {
      int i = 0;
      return i > 0 ? i > 1 ? 1 : 2 : 2;
    }

    /**
     * I am instance method hence you will have to add the cost of constructor
     * to me. (by myself I cost 4)
     */
    public Object testMethod4() {
      int i = 0;
      i = i > 0 ? 1 : 2;
      i = i > 0 ? 1 : 2;
      i = i > 0 ? 1 : 2;
      i = i > 0 ? 1 : 2;
      return new Object();
    }
  }

  public void testMediumCost1() throws Exception {
    MethodInfo method = repo.getClass(Medium.class.getName()).getMethod("statiCost1()I");
    assertFalse(method.canOverride());
    MethodCost cost = computer.compute(Medium.class, "statiCost1()I");
    assertEquals(1l, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  /**
   * Since cost2 is called twice, once by our test and once by constructor we
   * don't want to add it twice. The constructor adds 1. The direct method call
   * adds 2. So the total cost is 3.
   */
  public void testMediumCost2() throws Exception {
    MethodInfo method = repo.getClass(Medium.class.getName()).getMethod("cost2()I");
    assertTrue(method.canOverride());
    MethodCost cost = computer.compute(Medium.class, "cost2()I");
    assertEquals(3l, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testMediumFinalCost2() throws Exception {
    MethodInfo method = repo.getClass(Medium.class.getName()).getMethod("finalCost2()I");
    assertFalse(method.canOverride());
  }

  /**
   * Cost of the constructor needs to add only the cost of the static method it calls.
   * (The static method can not be overridden). The cost of the instance method can be
   * overridden in a subclass.
   */
  public void testMediumInit() throws Exception {
    MethodInfo method = repo.getClass(Medium.class.getName()).getMethod("<init>()V");
    assertFalse(method.canOverride());
    MethodCost cost = computer.compute(Medium.class, "<init>()V");
    assertEquals(1l, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  /**
   * Method4 is cost of 4 by itself, but one has to add the cost of constructor
   * since it is an instance method. The constructor is 0 but calls two methods:
   * cost1 method is static and can not be intercepted hence it has to be added.
   * cost2 method is instance and can be overridden hence we don't add that
   * cost.
   */
  public void testMediumMethod4() throws Exception {
    MethodCost cost = computer.compute(Medium.class,
        "testMethod4()Ljava/lang/Object;");
    assertEquals(5l, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  public static class Node {
    public String cost1() {
      int a = 0;
      return a == 2 ? "" : null;
    }
  }

  public static class TreeInjection {
    private Node subTitle; // non-injectable
    public Node title = new Node(); // injectable b/c public field (only after constructor)
    private final Node footnote; // injectable via constructor

    public TreeInjection(Node footnote) {
      this.footnote = footnote;
    }

    public String titleTcc0() {
      return title.cost1();
    }

    public String subTitleTcc1() {
      return subTitle.cost1();
    }

    public String footnoteTcc0() {
      return footnote.cost1();
    }

    public String veryExpensive() {
      return "".toLowerCase();
    }
  }

  public void testTreeConstructorHasZeroCost() throws Exception {
    MethodCost cost = computer.compute(TreeInjection.class, "<init>()V");
    assertEquals(0l, cost.getTotalCost().getCyclomaticComplexityCost());
    assertEquals(0l, cost.getTotalCost().getGlobalCost());
    assertEquals(0l, cost.getOverallCost());
  }

  public void testTreeTitleTcc0CostIsZeroBecauseInjectable() throws Exception {
    MethodCost cost = computer.compute(TreeInjection.class,
        "titleTcc0()Ljava/lang/String;");
    assertEquals(0l, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testTreeSubTitleTcc1CostIsOneBecauseNonInjectable() throws Exception {
    MethodCost cost = computer.compute(TreeInjection.class,
        "subTitleTcc1()Ljava/lang/String;");
    assertEquals(1l, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  public void testTreeFootnoteTcc0CostIsZeroBecauseInjectable() throws Exception {
    MethodCost cost = computer.compute(TreeInjection.class,
        "footnoteTcc0()Ljava/lang/String;");
    assertEquals(0l, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  static class ChoseConstructor {
    ChoseConstructor() {
    }

    ChoseConstructor(Object a) {
    }

    ChoseConstructor(Object a, int c, int d) {
    }

    ChoseConstructor(Object a, Object b) {
    }
  }

  public void testChooseConstructorWithMostNonPrimitiveParameters() throws Exception {
    ClassInfo classInfo = repo.getClass(ChoseConstructor.class.getName());
    MethodInfo constructor = classInfo.getConstructorWithMostNonPrimitiveParameters();
    assertEquals("<init>(Ljava/lang/Object;Ljava/lang/Object;)V", constructor
        .getNameDesc());
  }

  static class Singleton {
    private Singleton() {
      CostUtil.staticCost1();
    }

    public void doWork() {
      CostUtil.staticCost2();
    }
  }

  public void testIgnoreConstructorsIfAllConstructorsArePrivate() throws Exception {
    assertEquals(2L, computer.compute(Singleton.class, "doWork()V").getTotalCost().getCyclomaticComplexityCost());
    ClassInfo classInfo = repo.getClass(Singleton.class.getName());
    MethodInfo constructor = classInfo
        .getConstructorWithMostNonPrimitiveParameters();
    assertNull("Constructor should not be found when private",  constructor);
  }

  static class StaticInit {
    static {
      CostUtil.staticCost1();
    }

    public void doWork() {
      CostUtil.staticCost2();
    }
  }

  public void testAddStaticInitializationCost() throws Exception {
    assertEquals(3L, computer.compute(StaticInit.class, "doWork()V").getTotalCost().getCyclomaticComplexityCost());
  }

  static class Setters {
    private Object o;

    public void setO(Object o) {
      this.o = o;
    }

    public void doWork() {
      o.toString();
    }
  }

  public void testSetterInjection() throws Exception {
    MethodCost cost = computer.compute(Setters.class, "doWork()V");
    assertEquals(0L, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  static class WholeClassCost {
    void methodA() {
      CostUtil.staticCost1();
    }

    void methodB() {
      CostUtil.staticCost1();
    }
  }

  public void testComputeClassCost() throws Exception {
    ClassCost cost = computer.compute(WholeClassCost.class);
    assertEquals(1L, cost.getMethodCost("void methodA()").getTotalCost().getCyclomaticComplexityCost());
    assertEquals(1L, cost.getMethodCost("void methodB()").getTotalCost().getCyclomaticComplexityCost());
  }

  static class Array {
    String[] strings;

    public void method() {
      strings.clone();
    }
  }

  public void testArray() throws Exception {
    repo.getClass(String[].class.getName());
    computer.compute(repo.getClass(Array.class.getName()).getMethod("method()V"));
  }

  static class InjectableClass {
    public void cost4() {
      CostUtil.staticCost4();
    }

    public static void callCost0(InjectableClass ref) {
      indirection(ref);
    }

    private static void indirection(InjectableClass ref) {
      ref.cost4();
    }
    public static void callCost4() {
      InjectableClass x = new InjectableClass();
      indirection(x);
    }
  }

  public void testInjectabilityIsTransitive() throws Exception {
    MethodCost callCost0 = computer.compute(InjectableClass.class, "callCost0("
        + "Lcom/google/test/metric/MetricComputerTest$InjectableClass;)V");
    assertEquals(0L, callCost0.getTotalCost().getCyclomaticComplexityCost());

    MethodCost callCost4 = computer.compute(InjectableClass.class, "callCost4()V");
    assertEquals(4L, callCost4.getTotalCost().getCyclomaticComplexityCost());
  }

  static class GlobalState {
    int i;
    static final String X = "X";

    public int inc() {
      return i++;
    }

    public void noop() {
    }

    @Override
    public String toString() {
      return X;
    }
  }

  static final GlobalState ref = new GlobalState();
  static int count;
  static class GlobalStateUser {

    // StateLoad: 0
    public void noLoad() {
    }

    // StateLoad: 1
    public void accessCount() {
      count++;
    }

    // StateLoad: 0
    public void accessFinalState() {
      ref.noop();
    }

    // StateLoad: 0
    public void accessFinalState2() {
      ref.toString();
    }

    // StateLoad: 1
    public void accessMutableState() {
      ref.inc();
    }
  }

  public void testGlobalLoadWhichAccessesFinalShouldBeZero() {
    ClassCost cost = computer.compute(GlobalState.class);
    MethodCost method = cost.getMethodCost("java.lang.String toString()");
    assertEquals(0L, method.getTotalCost().getGlobalCost());
  }

  public void testGlobalLoadMethodDispatchNoStateAccessShouldBeZero() {
    ClassCost cost = computer.compute(GlobalStateUser.class);
    assertEquals(0L, cost.getMethodCost("void noLoad()").getTotalCost().getGlobalCost());
    assertEquals(0L, cost.getMethodCost("void accessFinalState()").getTotalCost().getGlobalCost());
    assertEquals(0L, cost.getMethodCost("void accessFinalState2()").getTotalCost().getGlobalCost());
  }

  public void testGlobalLoadAccessStateShouldBeOne() {
    MethodCost cost = computer.compute(GlobalStateUser.class, "accessCount()V");
    assertEquals(1L, cost.getTotalCost().getGlobalCost());
  }

  public void testGlobalLoadAccessStateThroughFinalShouldBeOne() {
    MethodCost cost =
        computer.compute(GlobalStateUser.class, "accessMutableState()V");
    new DrillDownReport(new PrintStream(new ByteArrayOutputStream()),
        null, Integer.MAX_VALUE, 0).print("", cost, 10);
    assertEquals("Expecting one for read and one for write", 2L,
        cost.getTotalCost().getGlobalCost());
  }

  public void testJavaLangObjectParsesCorrectly() throws Exception {
    repo.getClass(Object.class.getName());
  }

  public static class CostPerLine {
    static void main(){
      CostUtil.staticCost0(); // line0
      CostUtil.staticCost1(); // line1
      CostUtil.staticCost2(); // line2
    }
  }

  public void testCostPerLine() throws Exception {
    MethodCost cost = computer.compute(CostPerLine.class, "main()V");
    assertEquals(3, cost.getTotalCost().getCyclomaticComplexityCost());
    List<ViolationCost> lineNumberCosts = cost.getViolationCosts();
    assertEquals(3, lineNumberCosts.size());

    MethodInvokationCost line0 = (MethodInvokationCost) lineNumberCosts.get(0);
    MethodInvokationCost line1 = (MethodInvokationCost) lineNumberCosts.get(1);
    MethodInvokationCost line2 = (MethodInvokationCost) lineNumberCosts.get(2);

    int methodStartingLine = cost.getMethodLineNumber();

    assertEquals(0, line0.getMethodCost().getTotalCost().getCyclomaticComplexityCost());
    assertEquals(methodStartingLine + 0, line0.getLineNumber());

    assertEquals(1, line1.getMethodCost().getTotalCost().getCyclomaticComplexityCost());
    assertEquals(methodStartingLine + 1, line1.getLineNumber());

    assertEquals(2, line2.getMethodCost().getTotalCost().getCyclomaticComplexityCost());
    assertEquals(methodStartingLine + 2, line2.getLineNumber());
  }

  public static class WhiteListTest {
    public void testMethod() {
      new String(new byte[0]);
    }
  }

  public void testWhiteList() throws Exception {
    RegExpWhiteList customWhitelist = new RegExpWhiteList("java.lang");
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo)
        .withWhitelist(customWhitelist).build();
    computer = new MetricComputerJavaDecorator(toDecorate, repo);

    MethodCost cost = computer.compute(WhiteListTest.class, "testMethod()V");
    assertEquals(0L, cost.getTotalCost().getGlobalCost());
  }

  static class DoubleCountClassConst {
    static int x;
    static {
      x = 1;
    }
  }
  public void testDoubleCountClassConst() throws Exception {
    ClassCost cost = computer.compute(DoubleCountClassConst.class);
    assertEquals(1, cost.getMethodCost(cost.getClassName() + "()").getTotalCost().getGlobalCost());
  }

  static enum TestEnum1{ ONE }
  public void testEnumerationIsZero() throws Exception {
    RegExpWhiteList customWhitelist = new RegExpWhiteList("java.");
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo)
        .withWhitelist(customWhitelist).build();
    computer = new MetricComputerJavaDecorator(toDecorate, repo);
    ClassCost cost = computer.compute(TestEnum1.class);
    assertEquals(0, cost.getMethodCost(cost.getClassName() + "()").getTotalCost().getGlobalCost());
  }

  private final String testValue = null;
  class InnerClassTest {
    public void test() {
      testValue.indexOf(0);
    }
  }
  public void XtestInnerClassInjectability() throws Exception {
    MethodCost cost = computer.compute(InnerClassTest.class, "test()V");
    assertEquals(0, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  private static class ScoreTooHigh {
    public void doMockery(Builder builder) {
      Product product = builder.build();
      product.execute();
    }
    public static class Product {
      public void execute() {
        CostUtil.staticCost2();
      }
    }
    public static class Builder {
      public Product build() {
        CostUtil.staticCost4();
        return null;
      }
    }
  }

  public void testReturnValueFromInjectableIsInjectable() throws Exception {
    MethodCost cost = computer.compute(ScoreTooHigh.class,
        "doMockery(Lcom/google/test/metric/MetricComputerTest$ScoreTooHigh$Builder;)V");
    assertEquals(0, cost.getTotalCost().getCyclomaticComplexityCost());
  }

  public static class NonDeterministicCostUtil {

    public static int global;
    public int notGlobal;
    public int value;

    private int trinary(boolean bool) {
        try {
          return !bool ? notGlobal : global;
        } catch (Exception e) {
          return 1;
        }
    }

    public void test() {
      value = trinary(false);
    }


   }

  public void testWhenLeftAndRightSideOfTrinaryReturnDifferentCostUseHigherOne()
      throws Exception {
    MethodCost cost = computer.compute(NonDeterministicCostUtil.class,
        "test()V");
    assertEquals(1, cost.getTotalCost().getGlobalCost());
  }

}