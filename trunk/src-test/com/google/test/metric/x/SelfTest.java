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
package com.google.test.metric.x;

import org.objectweb.asm.ClassReader;

import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.ClassRepositoryTestCase;
import com.google.test.metric.CostModel;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;
import com.google.test.metric.example.Primeness;
import com.google.test.metric.example.SumOfPrimes1;
import com.google.test.metric.example.SumOfPrimes2;

public class SelfTest extends ClassRepositoryTestCase {

  private MetricComputer computer;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    computer = new MetricComputer(repo, null, new RegExpWhiteList(), new CostModel());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    computer = null;
  }

  public void testMethodCost() throws Exception {
    System.out.println(computer.compute(MethodCost.class));
  }

  public void testClassCost() throws Exception {
    System.out.println(computer.compute(ClassCost.class));
  }

  public void testClassRepository() throws Exception {
    System.out.println(computer.compute(ClassRepository.class));
  }

  public void testClassReader() throws Exception {
    System.out.println(computer.compute(ClassReader.class));
  }

  public void testPrimeness() throws Exception {
    System.out.println(computer.compute(Primeness.class));
  }

  public void testSumOfPrimes() throws Exception {
    System.out.println(computer.compute(SumOfPrimes1.class));
  }

  public void testSumOfPrimes2() throws Exception {
    System.out.println(computer.compute(SumOfPrimes2.class));
  }


}
