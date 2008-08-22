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

import com.google.test.metric.LocalVariableInfo;

import java.util.List;

/**
 * Handle to a method node in the AST. To be used on the parsing side only.
 */
public interface MethodHandle {

  String getNameDesc();

  List<ParameterHandle> getParameters();

  List<LocalVariableInfo> getLocalVariables();

}
