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
// Copyright 2008 Google Inc. All Rights Reserved.

package com.google.test.metric.ast;

import com.google.test.metric.Type;
import com.google.test.metric.asm.Visibility;
import com.google.test.metric.ast.AbstractSyntaxTree.Clazz;

public class Field extends VariableImpl
    implements FieldInfo, FieldHandle {

  private final Clazz owner;
  private final Visibility access;

  private final static String FIELD_FORMAT = "%s.%s{%s}";

  public Field(Clazz newOwner, String newName, Type newType,
      Visibility newAccess, boolean newIsConstant) {
    super(newName, newType, newIsConstant, false);
    owner = newOwner;
    access = newAccess;

    if(owner != null) {
      owner.registerField(this);
    } else {
      System.out.println("TODO: metric.ast.Field: "+newName);
    }
  }

  @Override
  public String toString() {
    return String.format(FIELD_FORMAT, owner.getName(), getName(),
        type.toString());
  }

  public boolean isPrivate() {
    throw new UnsupportedOperationException();
  }
}