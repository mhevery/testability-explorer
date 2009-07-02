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

import java.io.File;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.classpath.DirectoryClassPath;
import com.google.test.metric.method.op.turing.Operation;

public class ClassInfoTest extends AutoFieldClearTestCase {

  private final ClassRepository repo = new JavaClassRepository();

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
    ClassInfo clazz = repo.getClass(EmptyClass.class.getCanonicalName());
    assertEquals(EmptyClass.class.getCanonicalName(), clazz.getName());
    assertEquals(EmptyClass.class.getCanonicalName(), clazz.toString());
    assertSame(clazz, repo.getClass(EmptyClass.class.getCanonicalName()));
  }

  public void testMethodNotFoundException() throws Exception {
    ClassInfo clazz = repo.getClass(EmptyClass.class.getCanonicalName());
    try {
      clazz.getMethod("IDontExistMethod()V");
      fail();
    } catch (MethodNotFoundException e) {
      assertTrue(e.getMessage().contains("IDontExistMethod()V"));
      assertTrue(e.getMessage().contains(EmptyClass.class.getCanonicalName()));
      assertEquals("IDontExistMethod()V", e.getMethodName());
      assertEquals(clazz, e.getClassInfo());
    }
  }

  public static class SingleMethodClass {
    public void methodA() {
    }
  }

  public void testParseSingleMethodClass() throws Exception {
    ClassInfo clazz = repo.getClass(SingleMethodClass.class.getCanonicalName());
    MethodInfo method = clazz.getMethod("void methodA()");
    assertEquals("void methodA()", method.toString());
    assertSame(method, clazz.getMethod("void methodA()"));
  }

  public void testFiledNotFound() throws Exception {
    ClassInfo clazz = repo.getClass(EmptyClass.class.getCanonicalName());
    try {
      clazz.getField("IDontExistField");
      fail();
    } catch (FieldNotFoundException e) {
      assertTrue(e.getMessage().contains("IDontExistField"));
      assertTrue(e.getMessage().contains(EmptyClass.class.getCanonicalName()));
      assertEquals("IDontExistField", e.getFieldName());
      assertEquals(clazz, e.getClassInfo());
    }
  }

  public static class SingleFieldClass {
    Object fieldA;
  }

  public void testParseFields() throws Exception {
    ClassInfo clazz = repo.getClass(SingleFieldClass.class.getCanonicalName());
    FieldInfo field = clazz.getField("fieldA");
    assertEquals("fieldA", field.getName());
    assertEquals(SingleFieldClass.class.getCanonicalName()
        + ".fieldA{java.lang.Object}", field.toString());
    assertSame(field, clazz.getField("fieldA"));
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
    assertLocalVars("void method()", params(), locals("this"));
  }

  public void testLocalVarsStaticMethod() throws Exception {
    assertLocalVars("void staticMethod()", params(), locals());
  }

  public void testLocalVarsMethod3() throws Exception {
    assertLocalVars("void method3(java.lang.Object, int, int[])",
        params("a", "b", "c"), locals("this", "d"));
  }

  public void testLocalVarsStaticMethod3() throws Exception {
    assertLocalVars("void staticMethod3(java.lang.Object, int, int[])", params("a",
        "b", "c"), locals("d"));
  }

  private void assertLocalVars(String method, String[] params, String[] locals) {
    ClassInfo classInfo = repo.getClass(LocalVarsClass.class.getCanonicalName());
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
    repo.getClass(Object.class.getCanonicalName());
  }

  public void testJavaLangString() throws Exception {
    repo.getClass(String.class.getCanonicalName());
  }

  public void testJavaUtilBitSet() throws Exception {
    repo.getClass(BitSet.class.getCanonicalName());
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
    repo.getClass(BitSetGetMethod.class.getCanonicalName());
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
    repo.getClass(Monitor.class.getCanonicalName());
  }

  public void testJSRinstructionInTryCatchFinally() throws Exception {
    repo.getClass(InetAddress.class.getCanonicalName());
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

  public void testMethodInSuperInterface() throws Exception {
    ClassInfo interfaceClassInfo = repo.getClass(SubTestInterface.class.getCanonicalName());
    assertEquals(repo.getClass(Object.class.getCanonicalName()), interfaceClassInfo.getSuperClass());
    List<ClassInfo> superInterfaces = interfaceClassInfo.getInterfaces();
    assertEquals(1, superInterfaces.size());
    assertEquals(repo.getClass(TestInterface.class.getCanonicalName()), superInterfaces.get(0));
    assertNotNull(interfaceClassInfo.getMethod("java.lang.Object get(java.lang.Object)"));
  }

  public void testPickConcreteMethodOverInterfaceMethod() throws Exception {
    ClassInfo classInfo = repo.getClass(ImplementsSubTestInterface.class.getCanonicalName());
    ClassInfo interfaceClassInfo = repo.getClass(SubTestInterface.class.getCanonicalName());
    MethodInfo method = classInfo.getMethod("java.lang.Object get(java.lang.Object)");
    assertSame(classInfo, method.getClassInfo());
    assertNotSame(interfaceClassInfo, method.getClassInfo());
  }

  public void testReadInvalidByteCodeClassFile() throws Exception {
    ClassRepository repo = new JavaClassRepository(new DirectoryClassPath(new File("classes-for-test")));
    try {
      repo.getClass("invalidByteCode");
      fail();
    } catch (ClassNotFoundException e) {
    }
  }

  public void testGetSettersShouldReturnItemsInAlphabeticalOrderAndIncludeSuperClasses() throws Exception {
    List<ClassInfo> emptyInterfaces = Collections.emptyList();
    ClassInfo superClass = new ClassInfo("super", false, null, emptyInterfaces, null);
    List<ParameterInfo> params = Collections.emptyList();
    List<LocalVariableInfo> locals = Collections.emptyList();
    List<Operation> operations = Collections.emptyList();
    superClass.addMethod(new MethodInfo(superClass, "void setB()", -1, null, params, locals,Visibility.PUBLIC, operations, false, false, Collections.<Integer>emptyList()));
    superClass.addMethod(new MethodInfo(superClass, "void setA()", -1, null, params, locals,Visibility.PRIVATE, operations, false, false, Collections.<Integer>emptyList()));
    superClass.addMethod(new MethodInfo(superClass, "voidX()", -1, null, params, locals,Visibility.PUBLIC, operations, false, false, Collections.<Integer>emptyList()));
    ClassInfo clazz = new ClassInfo("super", false, superClass, emptyInterfaces, null);
    clazz.addMethod(new MethodInfo(clazz, "void setD()", -1, null, params, locals,Visibility.PUBLIC, operations, false, false, Collections.<Integer>emptyList()));
    clazz.addMethod(new MethodInfo(clazz, "void setC()", -1, null, params, locals,Visibility.PUBLIC, operations, false, false, Collections.<Integer>emptyList()));

    Collection<MethodInfo> setters = clazz.getSetters();
    assertEquals(3, setters.size());
    Iterator<MethodInfo> iterator = setters.iterator();
    assertEquals("void setB()", iterator.next().getName());
    assertEquals("void setC()", iterator.next().getName());
    assertEquals("void setD()", iterator.next().getName());
  }

}
