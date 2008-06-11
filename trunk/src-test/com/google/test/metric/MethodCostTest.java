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

import junit.framework.TestCase;

public class MethodCostTest extends TestCase {

  public void testGetPrintableMethodName() {
    MethodCost methodCost = new MethodCost(
        "com.foo.bar.method(Ljava/lang/String;)V", -1, -1);
    assertEquals(" void method( String )", methodCost.getPrintableMethodName());

    methodCost = new MethodCost(
        "f.a.b.mymethod(IDLjava/lang/Thread;)Ljava/lang/Object;", -1, -1);
    assertEquals(" Object mymethod( int , double , Thread )", methodCost
        .getPrintableMethodName());

    methodCost = new MethodCost("c.b.ui.UI$ViewHandler.<clinit>()V", -1, -1);
    assertEquals(" UI$ViewHandler()", methodCost.getPrintableMethodName());

    methodCost = new MethodCost("c.b.ui.UI$ViewHandler.<init>(I)V", -1, -1);
    assertEquals(" UI$ViewHandler( int )", methodCost.getPrintableMethodName());
  }

  public void testDeconstructParameters() {
    MethodCost methodCost = new MethodCost("", -1, -1);

    assertEquals(" int ", methodCost.deconstructParameters("I"));
    assertEquals(" double [][][]", methodCost.deconstructParameters("[[[D"));
    assertEquals(" Object ", methodCost
        .deconstructParameters("Ljava/lang/Object;"));

  }
}
