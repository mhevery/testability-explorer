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

import com.google.classpath.ClasspathRootFactory;
import com.google.test.metric.ast.ClassHandle;
import com.google.test.metric.ast.ClassInfo;
import com.google.test.metric.ast.FieldInfo;
import com.google.test.metric.ast.LocalVariableInfo;
import com.google.test.metric.ast.MethodInfo;
import com.google.test.metric.ast.MockVisitor;

import java.net.InetAddress;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

public class ClassInfoTest extends ClassRepositoryTestCase {

  private ClassInfo parseClass(Class<?> clazz) {
    repo.getClass(EmptyClass.class);
    MockVisitor v = new MockVisitor();
    ast.accept(v);
    return v.getClassInfo(clazz.getName());
  }

  public void testNonExistingClass() throws Exception {
    try {
      repo.getClass("IDontExistClass");
      fail();
    } catch (ClassNotFoundException e) {
      assertTrue(e.getMessage().contains("IDontExistClass"));
      assertEquals("IDontExistClass", e.getClassName());
    }
  }

  public static class EmptyClass {
  }

  public void testParseEmptyClass() throws Exception {
    ClassHandle classHandle = repo.getClass(EmptyClass.class);
    ClassInfo classInfo = parseClass(EmptyClass.class);

    assertNotNull(classInfo);
    assertEquals(EmptyClass.class.getName(), classInfo.toString());
    assertSame(classHandle, repo.getClass(EmptyClass.class));
  }

  public void testMethodNotFoundException() throws Exception {
    ClassInfo classInfo = parseClass(EmptyClass.class);
    try {
      classInfo.getMethod("IDontExistMethod()V");
      fail();
    } catch (MethodNotFoundException e) {
      assertTrue(e.getMessage().contains("IDontExistMethod()V"));
      assertTrue(e.getMessage().contains(EmptyClass.class.getName()));
      assertEquals("IDontExistMethod()V", e.getMethodName());
      assertEquals(classInfo.getName(), e.getClassName());
    }
  }

  public static class SingleMethodClass {
    public void methodA() {
    }
  }

  public void testParseSingleMethodClass() throws Exception {
    ClassInfo classInfo = parseClass(SingleMethodClass.class);

    MethodInfo method = classInfo.getMethod("methodA()V");
    assertEquals("methodA()V", method.getName());
    assertEquals("void methodA()", method.toString());
    assertSame(method, classInfo.getMethod("methodA()V"));
  }

  public void testFiledNotFound() throws Exception {
    ClassInfo clazz = parseClass(EmptyClass.class);
    try {
      clazz.getField("IDontExistField");
      fail();
    } catch (FieldNotFoundException e) {
      assertTrue(e.getMessage().contains("IDontExistField"));
      assertTrue(e.getMessage().contains(EmptyClass.class.getName()));
      assertEquals("IDontExistField", e.getFieldName());
      assertEquals(clazz.getName(), e.getClassName());
    }
  }

  public static class SingleFieldClass {
    Object fieldA;
  }

  public void testParseFields() throws Exception {
    ClassInfo classInfo = parseClass(SingleFieldClass.class);
    FieldInfo field = classInfo.getField("fieldA");
    assertEquals("fieldA", field.getName());
    assertEquals(SingleFieldClass.class.getName()
        + ".fieldA{java.lang.Object}", field.toString());
    assertSame(field, classInfo.getField("fieldA"));
  }

  public static class LocalVarsClass {
    public void method() {
    }

    public static void staticMethod() {
    }

    public void method3(Object a, int b, int[] c) {
      Object d = null;
      a = d;
    }

    public static void staticMethod3(Object a, int b, int[] c) {
      Object d = null;
      a = d;
    }
  }

  public void testLocalVarsMethod() throws Exception {
    assertLocalVars("method()V", params(), locals("this"));
  }

  public void testLocalVarsStaticMethod() throws Exception {
    assertLocalVars("staticMethod()V", params(), locals());
  }

  public void testLocalVarsMethod3() throws Exception {
    assertLocalVars("method3(Ljava/lang/Object;I[I)V",
        params("a", "b", "c"), locals("this", "d"));
  }

  public void testLocalVarsStaticMethod3() throws Exception {
    assertLocalVars("staticMethod3(Ljava/lang/Object;I[I)V", params("a",
        "b", "c"), locals("d"));
  }

  private void assertLocalVars(String method, String[] params, String[] locals) {
    ClassInfo classInfo = parseClass(LocalVarsClass.class);
    MethodInfo methodInfo = classInfo.getMethod(method);
    List<ParameterInfo> paramsParse = methodInfo.getParameters();
    List<LocalVariableInfo> localsParse = methodInfo.getLocalVariables();

    assertEquals("Expecting " + Arrays.toString(params) + " found "
        + paramsParse, params.length, paramsParse.size());
    assertEquals("Expecting " + Arrays.toString(locals) + " found "
        + localsParse, locals.length, localsParse.size());
    for (int i = 0; i < params.length; i++) {
      assertEquals(params[i], paramsParse.get(i).getName());
    }
    for (int i = 0; i < locals.length; i++) {
      assertEquals(locals[i], localsParse.get(i).getName());
    }
  }

  private String[] params(String... strings) {
    return strings;
  }

  private String[] locals(String... strings) {
    return strings;
  }

  public void testJavaLangObject() throws Exception {
    repo.getClass(Object.class);
  }

  public void testJavaLangString() throws Exception {
    repo.getClass(String.class);
  }

  public void testJavaUtilBitSet() throws Exception {
    repo.getClass(BitSet.class);
  }

  static class BitSetGetMethod {
    private long[] bits;

    public BitSetGetMethod get(int fromIndex, int toIndex) {
      int startBitIndex = 0;
      while (true) {
        bits[0] = bits[1] | (startBitIndex == 2 ? 0 : 2);
      }
    }
  }

  public void testJavaUtilBitSetGetMethod() throws Exception {
    repo.getClass(BitSetGetMethod.class);
  }

  private static class Monitor {
    public void method() {
      synchronized (this) {
        hashCode();
      }
    }

    public void method2() {
      hashCode();
      synchronized (this) {
        hashCode();
      }
      hashCode();
    }
  }

  public void testMonitor() throws Exception {
    repo.getClass(Monitor.class);
  }

  public void testJSRinstructionInTryCatchFinally() throws Exception {
    repo.getClass(InetAddress.class);
  }

  interface TestInterface {
    Object get(Object o);
  }

  interface SubTestInterface extends TestInterface {
  }

  class ImplementsSubTestInterface implements SubTestInterface {
    public Object get(Object o) {
      return null;
    }
  }
/*
 * TODO: put back in once the interace/superclass issues is resolved properly..
  public void testMethodInSuperInterface() throws Exception {
    ClassHandle interfaceClassHandle = repo.getClass(SubTestInterface.class);
    assertEquals(repo.getClass(Object.class), interfaceClassHandle.getSuperClass());
    List<ClassHandle> superInterfaces = interfaceClassHandle.getInterfaces();
    assertEquals(1, superInterfaces.size());
    assertEquals(repo.getClass(TestInterface.class), superInterfaces.get(0));
    assertNotNull(interfaceClassHandle.getMethod("get(Ljava/lang/Object;)Ljava/lang/Object;"));
  }

  public void testPickConcreteMethodOverInterfaceMethod() throws Exception {
    ClassHandle ClassHandle = repo.getClass(ImplementsSubTestInterface.class);
    ClassHandle interfaceClassHandle = repo.getClass(SubTestInterface.class);
    MethodInfo method = ClassHandle.getMethod("get(Ljava/lang/Object;)Ljava/lang/Object;");
    assertSame(ClassHandle, method.getClassHandle());
    assertNotSame(interfaceClassHandle, method.getClassHandle());
  }
*/
  public void testReadInvalidByteCodeClassFile() throws Exception {
    ClassRepository repo = new ClassRepository(ClasspathRootFactory.makeClasspathRootGroup("classes-for-test"));
    try {
      repo.getClass("invalidByteCode");
      fail();
    } catch (ClassNotFoundException e) {
    }
  }

}
