package com.google.test.metric.ast;

import java.util.Collection;

/**
 * Internal representation of a Java Class
 */
final class JavaClazz extends Clazz implements
    JavaClassHandle, JavaClassInfo {

  boolean isInterface;

  public JavaClazz(Module newModule, String newName,
      Collection<ClassHandle> superClasses) {
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