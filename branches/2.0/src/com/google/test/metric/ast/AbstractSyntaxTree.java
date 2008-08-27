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

import com.google.test.metric.Type;
import com.google.test.metric.asm.Visibility;
import com.google.test.metric.method.op.turing.Operation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simplified Abstract Syntax Tree interface, for adding the syntax nodes, and
 * iterating the structure.
 */
public final class AbstractSyntaxTree {

  private static final Collection<ClassHandle> EMPTY_CLAZZ_LIST = Collections
      .emptySet();

  private final static Clazz PRIMITIVE_CLAZZ = new Clazz(null, "",
      EMPTY_CLAZZ_LIST);

  public final static ClassHandle PRIMITIVE = PRIMITIVE_CLAZZ;

  static class Node {
  }

  /**
   * Internal representation of a Java-Package.
   */
  static final class JavaModule extends Module implements
      JavaModuleHandle {

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
  static final class CppModule extends Module implements CppModuleHandle,
    CppModuleInfo {

    final CppModule parent;
    final List<CppModule> children = new ArrayList<CppModule>();

    private CppModule(String newName, CppModule parent) {
      super(newName);
      this.parent = parent;
      if (parent != null) {
        parent.addModule(this);
      }
    }

    @Override
    public CppModuleHandle getHandle() {
      return this;
    }

    public void addModule(CppModule child) {
      children.add(child);
    }

    public List<CppModuleInfo> getChildren() {
      List<CppModuleInfo> result = new ArrayList<CppModuleInfo>();
      for (CppModule m : children) {
        result.add(m);
      }
      return result;
    }
  }

  /**
   * Internal representation of a Cpp-Class
   */
  static final class CppClazz extends Clazz implements CppClassHandle,
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

  static final class CppMethod extends Method implements
      CppMethodHandle, CppMethodInfo {

    CppMethod(Node owner, String name, Type returnType, Visibility access) {
      super(owner, name, returnType, access);
    }
  }

  private final Map<NodeHandle, Node> nodes = new HashMap<NodeHandle, Node>();

  // All the defined modules.
  private final Map<ModuleHandle, Module> modules = new HashMap<ModuleHandle, Module>();

  // Mapping of name : handle for all defined classes.
  private final Map<String, ClassHandle> classDirectory = new HashMap<String, ClassHandle>();

  // Mapping of JavaClassHandle : JavaClazz
  private final Map<JavaClassHandle, JavaClazz> javaClasses = new HashMap<JavaClassHandle, JavaClazz>();

  // Mapping of CppClassHandle : CppClazz
  private final Map<CppClassHandle, CppClazz> cppClasses = new HashMap<CppClassHandle, CppClazz>();

  // Mapping ClassHandle : Clazz. Contains entries for ALL classes-
  // javaClasses + cppClass is a subset of this map.
  private final Map<ClassHandle, Clazz> classes = new HashMap<ClassHandle, Clazz>();

  private final Map<JavaMethodHandle, JavaMethod> javaMethods = new HashMap<JavaMethodHandle, JavaMethod>();
  private final Map<CppMethodHandle, CppMethod> cppMethods = new HashMap<CppMethodHandle, CppMethod>();
  private final Map<MethodHandle, Method> methods = new HashMap<MethodHandle, Method>();

  private final Map<FieldHandle, Field> fields = new HashMap<FieldHandle, Field>();

  /**
   * Add a new top-level module to this AST.
   *
   * @param lang
   *          The language of this module. If we don't care about language
   *          specific features, {@code Language.INDEPENDENT} should be passen.
   * @param name
   *          The name of the module
   * @return A handle to the newly created module.
   * @throws IllegalArgumentException
   *           if the language is not supported.
   */
  public ModuleHandle createModule(Language lang, ModuleHandle parent, String name) {
    Module module;

    switch (lang) {
    case JAVA:
      if (parent != null) {
        throw new IllegalArgumentException();
      }
      module = new JavaModule(name);
      break;
    case CPP:
      CppModule cppParent = null;
      if (parent != null) {
        if (parent instanceof CppModule) {
          cppParent = (CppModule) parent;
        } else {
          throw new IllegalArgumentException();
        }
      }
      module = new CppModule(name, cppParent);
      break;
    case INDEPENDENT:
      module = new Module(name);
      break;
    default:
      throw new IllegalArgumentException("Undefined Language!");
    }

    nodes.put(module.getHandle(), module);
    if (parent == null) {
      modules.put(module.getHandle(), module);
    }
    return module.getHandle();

  }

  /**
   * Adds a new class to the repository, not yet associated with a module.
   *
   * @param lang
   *          The language of this class. See {} for a discussion.
   * @param name
   *          The name of the class.
   * @param superClassList
   *          All interfaces and superclasses that this class is implementing /
   *          extending.
   * @return A handle to the newly created class.
   * @throws IllegalArgumentException
   *           if the language is not supported.
   */
  public ClassHandle createClass(Language lang, String name,
      ClassHandle... superClassList) {
    Clazz clazz;
    Collection<ClassHandle> superClasses = Arrays.asList(superClassList);

    switch (lang) {
    case JAVA: {
      JavaClazz newClazz = new JavaClazz(null, name, superClasses);
      javaClasses.put(newClazz.getHandle(), newClazz);
      clazz = newClazz;
    }
      break;
    case CPP: {
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
    nodes.put(clazz.getHandle(), clazz);
    return clazz.getHandle();
  }

  /**
   * Checks whether the given class is defined. Language independent.
   *
   * @param clazzName
   *          The name of the class.
   * @return {@code true} if this class is defined in any language,
   *         {@code false} else.
   */
  public boolean containsClass(String clazzName) {
    return classDirectory.containsKey(clazzName);
  }

  /**
   * Retrieves a language-independent handle to the class.
   *
   * @param clazzName
   *          The name of the class.
   * @return A handle to the class.
   */
  public ClassHandle getClass(String clazzName) {
    return classDirectory.get(clazzName);
  }

  /**
   * Retrieves a java-specific handle to a class.
   *
   * @param classHandle
   *          The language-independent class handle
   * @return A handle to the class if it exists, {@code null} else.
   */
  public JavaClassHandle getJavaClassHandle(ClassHandle classHandle) {
    return javaClasses.get(classHandle);
  }

  public MethodHandle createMethod(Language lang, NodeHandle owner,
      String name, Visibility access, Type returnType) {

    Node ownerNode = nodes.get(owner);
    Method method;

    switch (lang) {
    case JAVA: {
      JavaClazz ownerClazz = javaClasses.get(owner);
      JavaMethod jMethod = new JavaMethod(ownerClazz, name, returnType, access);
      javaMethods.put(jMethod, jMethod);
      method = jMethod;
      break;
    }
    case CPP: {
      CppMethod jMethod = new CppMethod(ownerNode, name, returnType, access);
      cppMethods.put(jMethod, jMethod);
      method = jMethod;
      break;
    }
    case INDEPENDENT:
      method = new Method(ownerNode, name, returnType, access);
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
    for(Field f : fields.values()) {
      //TODO: Field should implement equals and hashCode
      if(f.getName().equals(field.getName()) &&
         f.type.equals(field.type) &&
         //f.isFinal() == field.isFinal() &&
         f.isGlobal() == field.isGlobal() &&
         f.getClass().equals(field.getClass() )) {
        System.out.println("Returned old: "+f);
        return f;
      }
    }
    fields.put(field, field);
    System.out.println("Returned new: "+field);
    return field;
  }

  /**
   * Accepts a new visitor for traversing the tree.
   *
   * @param v
   */
  public void accept(Visitor v) {
    for (Module m : modules.values()) {
      v.visitModule(m);
      m.accept(v);
    }
    for (ClassInfo c : classes.values()) {
      v.visitClass(c);
    }
  }

  public ParameterHandle createMethodParameter(MethodHandle methodHandle,
      String name, Type type) {
    Method method = methods.get(methodHandle);
    Parameter p = new Parameter(method, name, type);
    return p;
  }

  public LocalVariableHandle createLocalVariable(MethodHandle methodHandle,
      String name, Type type) {
    Method method = methods.get(methodHandle);
    LocalVariable v = new LocalVariable(method, name, type);
    return v;
  }

  public void createOperations(MethodHandle methodHandle,
      List<Operation> operations) {
    Method method = methods.get(methodHandle);
    method.addOperations(operations);
  }
}
