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

import static com.google.classpath.ClasspathRootFactory.makeClasspathRootGroup;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;

import com.google.test.metric.report.DrillDownReport;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

public class MetricComputerTest extends AutoFieldClearTestCase {

  private MetricComputerJavaDecorator decoratedComputer;
  private ClassRepository repo = new JavaClassRepository();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
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
    MethodCost cost = decoratedComputer.compute(Medium.class, "statiCost1()I");
    assertEquals(1l, cost.getTotalComplexityCost());
  }

  /**
   * Since cost2 is called twice, once by our test and once by constructor we
   * don't want to add it twice. The constructor adds 1. The direct method call
   * adds 2. So the total cost is 3.
   */
  public void testMediumCost2() throws Exception {
    MethodInfo method = repo.getClass(Medium.class.getName()).getMethod("cost2()I");
    assertTrue(method.canOverride());
    MethodCost cost = decoratedComputer.compute(Medium.class, "cost2()I");
    assertEquals(3l, cost.getTotalComplexityCost());
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
    MethodCost cost = decoratedComputer.compute(Medium.class, "<init>()V");
    assertEquals(1l, cost.getTotalComplexityCost());
  }

  /**
   * Method4 is cost of 4 by itself, but one has to add the cost of constructor
   * since it is an instance method. The constructor is 0 but calls two methods:
   * cost1 method is static and can not be intercepted hence it has to be added.
   * cost2 method is instance and can be overridden hence we don't add that
   * cost.
   */
  public void testMediumMethod4() throws Exception {
    MethodCost cost = decoratedComputer.compute(Medium.class,
        "testMethod4()Ljava/lang/Object;");
    assertEquals(5l, cost.getTotalComplexityCost());
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
    MethodCost cost = decoratedComputer.compute(TreeInjection.class, "<init>()V");
    assertEquals(0l, cost.getTotalComplexityCost());
    assertEquals(0l, cost.getTotalGlobalCost());
    assertEquals(0l, cost.getOverallCost());
  }

  public void testTreeTitleTcc0CostIsZeroBecauseInjectable() throws Exception {
    MethodCost cost = decoratedComputer.compute(TreeInjection.class,
        "titleTcc0()Ljava/lang/String;");
    assertEquals(0l, cost.getTotalComplexityCost());
  }

  public void testTreeSubTitleTcc1CostIsOneBecauseNonInjectable() throws Exception {
    MethodCost cost = decoratedComputer.compute(TreeInjection.class,
        "subTitleTcc1()Ljava/lang/String;");
    assertEquals(1l, cost.getTotalComplexityCost());
  }

  public void testTreeFootnoteTcc0CostIsZeroBecauseInjectable() throws Exception {
    MethodCost cost = decoratedComputer.compute(TreeInjection.class,
        "footnoteTcc0()Ljava/lang/String;");
    assertEquals(0l, cost.getTotalComplexityCost());
  }

  public void testTreeVeryExpensive() throws Exception {
    MethodCost cost = decoratedComputer.compute(TreeInjection.class,
        "veryExpensive()Ljava/lang/String;");
    assertTrue("100<"+cost.getTotalComplexityCost(), 100l < cost.getTotalComplexityCost());
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
    MetricComputer computer = decoratedComputer.getDecoratedComputer();
    MethodInfo constructor = computer.getConstructorWithMostNonPrimitiveParameters(classInfo);
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

  public void testIgnoreConstructorsIfAllConstructorsArePrivate()
      throws Exception {
    assertEquals(2L, decoratedComputer.compute(Singleton.class, "doWork()V")
        .getTotalComplexityCost());
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
    assertEquals(3L, decoratedComputer.compute(StaticInit.class, "doWork()V")
        .getTotalComplexityCost());
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
    assertEquals(0L, decoratedComputer.compute(Setters.class, "doWork()V")
        .getTotalComplexityCost());
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
    ClassCost cost = decoratedComputer.compute(WholeClassCost.class);
    assertEquals(1L, cost.getMethodCost("void methodA()").getTotalComplexityCost());
    assertEquals(1L, cost.getMethodCost("void methodB()").getTotalComplexityCost());
  }

  static class Array {
    String[] strings;

    public void method() {
      strings.clone();
    }
  }

  public void testArray() throws Exception {
    repo.getClass(String[].class.getName());
    decoratedComputer.compute(repo.getClass(Array.class.getName()).getMethod("method()V"));
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
    ClassCost cost = decoratedComputer.compute(InjectableClass.class);
    MethodCost callCost0 = cost.getMethodCost("void callCost0("
          + "com.google.test.metric.MetricComputerTest$InjectableClass)");
    MethodCost callCost4 = cost.getMethodCost("void callCost4()");
    assertEquals(0L, callCost0.getTotalComplexityCost());
    assertEquals(4L, callCost4.getTotalComplexityCost());
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
    ClassCost cost = decoratedComputer.compute(GlobalState.class);
    MethodCost method = cost.getMethodCost("java.lang.String toString()");
    assertEquals(0L, method.getTotalGlobalCost());
  }

  public void testGlobalLoadMethodDispatchNoStateAccessShouldBeZero() {
    ClassCost cost = decoratedComputer.compute(GlobalStateUser.class);
    assertEquals(0L, cost.getMethodCost("void noLoad()").getTotalGlobalCost());
    assertEquals(0L, cost.getMethodCost("void accessFinalState()")
        .getTotalGlobalCost());
    assertEquals(0L, cost.getMethodCost("void accessFinalState2()")
        .getTotalGlobalCost());
  }

  public void testGlobalLoadAccessStateShouldBeOne() {
    MethodCost cost = decoratedComputer.compute(GlobalStateUser.class, "accessCount()V");
    assertEquals(1L, cost.getTotalGlobalCost());
  }

  public void testGlobalLoadAccessStateThroughFinalShouldBeOne() {
    MethodCost cost =
        decoratedComputer.compute(GlobalStateUser.class, "accessMutableState()V");
    new DrillDownReport(new PrintStream(new ByteArrayOutputStream()),
        null, Integer.MAX_VALUE, 0).print("", cost, 10);
    assertEquals("Expecting one for read and one for write", 2L,
        cost.getTotalGlobalCost());
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
    MethodCost cost = decoratedComputer.compute(CostPerLine.class, "main()V");
    assertEquals(3, cost.getTotalComplexityCost());
    List<LineNumberCost> lineNumberCosts = cost.getOperationCosts();
    assertEquals(3, lineNumberCosts.size());

    LineNumberCost line0 = lineNumberCosts.get(0);
    LineNumberCost line1 = lineNumberCosts.get(1);
    LineNumberCost line2 = lineNumberCosts.get(2);

    int methodStartingLine = cost.getMethodLineNumber();

    assertEquals(0, line0.getMethodCost().getTotalComplexityCost());
    assertEquals(methodStartingLine + 0, line0.getLineNumber());

    assertEquals(1, line1.getMethodCost().getTotalComplexityCost());
    assertEquals(methodStartingLine + 1, line1.getLineNumber());

    assertEquals(2, line2.getMethodCost().getTotalComplexityCost());
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
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);

    MethodCost cost = decoratedComputer.compute(WhiteListTest.class, "testMethod()V");
    assertEquals(0L, cost.getTotalGlobalCost());
  }

  public void testThatOnWindowsWeCanParseTheFonts() throws Exception {
    repo = new JavaClassRepository(makeClasspathRootGroup("classes-for-test/jre1.6_TrueTypeBug"));
    WhiteList whitelist = new RegExpWhiteList();
    MetricComputer computer = new MetricComputer(repo, null, whitelist, new CostModel());
    ClassInfo clazz = repo.getClass("sun.font.TrueTypeFont");
    MethodInfo method = clazz.getMethod("getTableBuffer(I)Ljava/nio/ByteBuffer;");
    try {
      computer.compute(method);
    } catch (Throwable e) {
      fail(e.getMessage());
    }
  }

  static class DoubleCountClassConst {
    static int x;
    static {
      x = 1;
    }
  }
  public void testDoubleCountClassConst() throws Exception {
    ClassCost cost = decoratedComputer.compute(DoubleCountClassConst.class);
    assertEquals(1, cost.getMethodCost(cost.getClassName() + "()").getTotalGlobalCost());
  }

  static enum TestEnum1{ ONE }
  public void testEnumerationIsZero() throws Exception {
    RegExpWhiteList customWhitelist = new RegExpWhiteList("java.");
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo)
        .withWhitelist(customWhitelist).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
    ClassCost cost = decoratedComputer.compute(TestEnum1.class);
    assertEquals(0, cost.getMethodCost(cost.getClassName() + "()").getTotalGlobalCost());
  }

  private final String testValue = null;
  class InnerClassTest {
    public void test() {
      testValue.indexOf(0);
    }
  }
  public void XtestInnerClassInjectability() throws Exception {
    MethodCost cost = decoratedComputer.compute(InnerClassTest.class, "test()V");
    assertEquals(0, cost.getTotalComplexityCost());
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
    MethodCost cost = decoratedComputer.compute(ScoreTooHigh.class,
        "doMockery(Lcom/google/test/metric/MetricComputerTest$ScoreTooHigh$Builder;)V");
    assertEquals(0, cost.getTotalComplexityCost());
  }

}