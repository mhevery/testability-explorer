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
package com.google.test.metric.ast;

import com.google.test.metric.FieldNotFoundException;
import com.google.test.metric.MethodNotFoundException;
import com.google.test.metric.Type;
import com.google.test.metric.asm.Visibility;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simplified Abstract Syntax Tree interface, for adding the syntax nodes,
 * and iterating the structure.
 */
public final class AbstractSyntaxTree {

  private static final Collection<ClassHandle> EMPTY_CLAZZ_LIST =
      Collections.emptySet();

  private final static Clazz PRIMITIVE_CLAZZ =
      new Clazz(null, "", EMPTY_CLAZZ_LIST);

  public final static ClassHandle PRIMITIVE = PRIMITIVE_CLAZZ;


  /**
   * Internal representation of a "Module". Will never ever be passed to
   * somebody outside this class- only either as a ModuleHandle for creating
   * children, or as a ModuleInfo for reading the necessary data.
   */
  private static class Module implements ModuleHandle, ModuleInfo {
    String name;

    private Module(String newName) {
      name = newName;
    }

    public ModuleHandle getHandle() {
      return this;
    }

    public String getName() {
      return name;
    }
  }

  private static class Clazz implements ClassHandle, ClassInfo {

    String name;
    Module module;
    Collection<ClassHandle> superClasses;
    Map<String, FieldInfo> fields = new HashMap<String, FieldInfo>();

    Set<Method> methods = new HashSet<Method>();


    private Clazz(Module newModule, String newName,
        Collection<ClassHandle> theSuperClasses) {
      module = newModule;
      name = newName;
      superClasses = new HashSet<ClassHandle>(theSuperClasses);
    }

    public String getName() {
      return name;
    }

    @Override
    public String toString() {
      return name;
    }

    /**
     * {@inheritDoc}
     */
    public ClassHandle getHandle() {
      return this;
    }

    /**
     * {@inheritDoc}
     */
    public MethodInfo getMethod(String methodName) throws
        MethodNotFoundException {
      for (MethodInfo info : methods) {
        if (info.getName().equals(methodName)) {
          return info;
        }
      }
      throw new MethodNotFoundException(name, methodName);
    }

    /**
     * {@inheritDoc}
     */
    public FieldInfo getField(String fieldName) throws FieldNotFoundException {
      if (fields.containsKey(fieldName)) {
        return fields.get(fieldName);
      }
      throw new FieldNotFoundException(name, fieldName);
    }

    public void registerMethod(Method method) {
      methods.add(method);
    }

    public void registerField(Field field) {
      fields.put(field.getName(), field);
    }
  }

  /**
   * Internal representation of a method.
   */
  private static class Method implements MethodInfo, MethodHandle {

    Clazz owner;
    String name;
    Visibility access;
    Type returnType;
    final List<Parameter> parameters = new LinkedList<Parameter>();

    private Method(Clazz newOwner, String newName, Type newReturnType,
        Visibility newAccess) {
      owner = newOwner;
      name = newName;
      returnType = newReturnType;
      access = newAccess;

      owner.registerMethod(this);
    }

    public String getName() {
      return name;
    }

    public String getNameDesc() {
      return name;
    }

    public List<LocalVariableInfo> getLocalVariables() {
      throw new UnsupportedOperationException();
    }

    public List<ParameterInfo> getParameters() {
      throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
      StringBuilder nameBuilder =
          new StringBuilder(name.substring(0, name.indexOf(")")));
      nameBuilder.insert(0, returnType.toString() + " ");
      return nameBuilder.toString();
    }
  }

  private static class Parameter implements ParameterInfo, ParameterHandle {

    public String getName() {
      throw new UnsupportedOperationException();
    }

  }

  private static class Field implements FieldInfo, FieldHandle {

    private final Clazz owner;
    private final String name;
    private final Type type;
    private final Visibility access;
    private final boolean isConstant;

    private Field(Clazz newOwner, String newName, Type newType,
        Visibility newAccess, boolean newIsConstant) {
      owner = newOwner;
      name = newName;
      type = newType;
      access = newAccess;
      isConstant = newIsConstant;

      owner.registerField(this);
    }

    public String getName() {
      return name;
    }
  }
  /**
   * Internal representation of a Java-Package.
   */
  private static final class JavaModule extends Module
      implements JavaModuleHandle {

    private JavaModule(String newName) {
      super(newName);
    }

    @Override
    public JavaModuleHandle getHandle() {
      return this;
    }
  }

  /**
   * Internal representation of a C++ Module.
   */
  static final class CppModule extends Module implements CppModuleHandle {

    private CppModule(String newName) {
      super(newName);
    }

    @Override
    public CppModuleHandle getHandle() {
      return this;
    }
  }

  /**
   * Internal representation of a Cpp-Class
   */
  private static final class CppClazz extends Clazz implements CppClassHandle,
      CppClassInfo {

    public CppClazz(Module newModule, String newName,
        Collection<ClassHandle> theSuperClasses) {
      super(newModule, newName, theSuperClasses);
    }

    @Override
    public CppClassHandle getHandle() {
      return this;
    }
  }

  /**
   * Internal representation of a Java Class
   */
  private static final class JavaClazz extends Clazz
      implements JavaClassHandle, JavaClassInfo {

    boolean isInterface;

    public JavaClazz(Module newModule, String newName,
        Collection<ClassHandle> superClasses ) {
      super(newModule, newName, superClasses);
    }

    public void setIsInterface(boolean newIsInterface) {
      isInterface = newIsInterface;
    }

    @Override
    public JavaClassHandle getHandle() {
      return this;
    }
  }

  private static final class JavaMethod extends Method
      implements JavaMethodHandle, JavaMethodInfo {

    boolean isAbstract;
    boolean isFinal;

    JavaMethod(Clazz owner, String name, Type returnType, Visibility access) {
      super(owner, name, returnType, access);
    }

    private void setIsFinal(boolean newFinal) {
      isFinal = newFinal;
    }

    public boolean getIsFinal() {
      return isFinal;
    }

    private void setIsAbstract(boolean newIsAbstract) {
      isAbstract = newIsAbstract;
    }

    public boolean getIsAbstract() {
      return isAbstract;
    }
  }

  // All the defined modules.
  private final Set<Module> modules = new HashSet<Module>();

  // Mapping of name : handle for all defined classes.
  private final Map<String, ClassHandle> classDirectory =
      new HashMap<String, ClassHandle>();

  // Mapping of JavaClassHandle : JavaClazz
  private final Map<JavaClassHandle, JavaClazz> javaClasses =
      new HashMap<JavaClassHandle, JavaClazz>();

  // Mapping of CppClassHandle : CppClazz
  private final Map<CppClassHandle, CppClazz> cppClasses =
    new HashMap<CppClassHandle, CppClazz>();

  // Mapping ClassHandle : Clazz. Contains entries for ALL classes-
  // javaClasses + cppClass is a subset of this map.
  private final Map<ClassHandle, Clazz> classes =
    new HashMap<ClassHandle, Clazz>();

  private final Map<JavaMethodHandle, JavaMethod> javaMethods =
      new HashMap<JavaMethodHandle, JavaMethod>();
  private final Map<MethodHandle, Method> methods =
      new HashMap<MethodHandle, Method>();

  private final Map<FieldHandle, Field> fields =
      new HashMap<FieldHandle, Field>();

  /**
   * Add a new top-level module to this AST.
   *
   * @param lang The language of this module. If we don't care about language
   *        specific features, {@code Language.INDEPENDENT} should be passen.
   * @param name The name of the module
   * @return A handle to the newly created module.
   * @throws IllegalArgumentException if the language is not supported.
   */
  public ModuleHandle createModule(Language lang, String name) {
    Module module;

    switch (lang) {
      case JAVA:
        module = new JavaModule(name);
        break;
      case CPP:
        module = new CppModule(name);
        break;
      case INDEPENDENT:
        module = new Module(name);
        break;
      default:
        throw new IllegalArgumentException("Undefined Language!");
    }

    modules.add(module);
    return module.getHandle();

  }

  /**
   * Adds a new class to the repository, not yet associated with a module.
   * @param lang The language of this class. See {} for a
   * discussion.
   * @param name The name of the class.
   * @param superClassList All interfaces and superclasses that this class is
   * implementing / extending.
   * @return A handle to the newly created class.
   * @throws IllegalArgumentException if the language is not supported.
   */
  public ClassHandle createClass(Language lang, String name,
      ClassHandle... superClassList) {
    Clazz clazz;
    Collection<ClassHandle> superClasses = Arrays.asList(superClassList);

    switch (lang) {
      case JAVA:
        {
          JavaClazz newClazz = new JavaClazz(null, name, superClasses);
          javaClasses.put(newClazz.getHandle(), newClazz);
          clazz = newClazz;
        }
        break;
      case CPP:
        {
          CppClazz newClazz = new CppClazz(null, name, superClasses);
          cppClasses.put(newClazz.getHandle(), newClazz);
          clazz = newClazz;
        }
        break;
      case INDEPENDENT:
        clazz = new Clazz(null, name, superClasses);
        break;
      default:
        throw new IllegalArgumentException("Undefined Language!");
    }

    classDirectory.put(name, clazz.getHandle());
    classes.put(clazz.getHandle(), clazz);
    System.out.println("Created class " + name);
    return clazz.getHandle();
  }

  /**
   * Checks whether the given class is defined. Language independent.
   * @param clazzName The name of the class.
   * @return {@code true} if this class is defined in any language,
   * {@code false} else.
   */
  public boolean containsClass(String clazzName) {
    System.out.println("Querying for class " + clazzName);
    return classDirectory.containsKey(clazzName);
  }

  /**
   * Retrieves a language-independent handle to the class.
   * @param clazzName The name of the class.
   * @return A handle to the class.
   */
  public ClassHandle getClass(String clazzName) {
    return classDirectory.get(clazzName);
  }

  /**
   * Retrieves a java-specific handle to a class.
   * @param classHandle The language-independent class handle
   * @return A handle to the class if it exists, {@code null} else.
   */
  public JavaClassHandle getJavaClassHandle(ClassHandle classHandle) {
    return javaClasses.get(classHandle);
  }

  public MethodHandle createMethod(Language lang, ClassHandle owner,
      String name, Visibility access, Type returnType) {

    Clazz ownerClazz = classes.get(owner);

    Method method;

    switch (lang) {
      case JAVA:
      {
        JavaMethod jMethod = new JavaMethod(ownerClazz, name,
            returnType, access);
        javaMethods.put(jMethod, jMethod);
        method = jMethod;
        break;
      }
      case CPP:
        // Not yet defined- we use the independent case here.
      case INDEPENDENT:
        method = new Method(ownerClazz, name, returnType, access);
        break;
      default:
        throw new IllegalArgumentException("Undefined Language!");
    }

    methods.put(method, method);
    return method;
  }

  public FieldHandle createField(Language lang, ClassHandle owner, String name,
      Visibility access, Type type, boolean isFinal) {

    Clazz ownerClazz = classes.get(owner);

    Field field = new Field(ownerClazz, name, type, access, isFinal);
    fields.put(field, field);
    return field;
  }

  /**
   * Accepts a new visitor for traversing the tree.
   * @param v
   */
  public void accept(Visitor v) {
    for (Module m : modules) {
      v.visitModule(m);
    }
    for (ClassInfo c : classes.values()) {
      v.visitClass(c);
    }
  }

  /**
   * Visible for testing only (!).
   * Retrieves the class-info for a given handle.
   */
  public ClassInfo getClassInfo(ClassHandle handle) {
    return classes.get(handle);
  }

  public ParameterHandle createMethodParameter(MethodHandle method, String name,
      Type type) {
    throw new UnsupportedOperationException();
  }
}
