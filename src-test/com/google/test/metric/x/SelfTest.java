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

import com.google.test.metric.AutoFieldClearTestCase;
import com.google.test.metric.ClassCost;
import com.google.test.metric.ClassRepository;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MethodCost;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.example.Primeness;
import com.google.test.metric.example.SumOfPrimes1;
import com.google.test.metric.example.SumOfPrimes2;
import com.google.test.metric.testing.MetricComputerBuilder;
import com.google.test.metric.testing.MetricComputerJavaDecorator;

public class SelfTest extends AutoFieldClearTestCase {

  private final ClassRepository repo = new JavaClassRepository();
  private MetricComputerJavaDecorator decoratedComputer;

  @Override
  protected void setUp() throws Exception {
    MetricComputer toDecorate = new MetricComputerBuilder().withClassRepository(repo).build();
    decoratedComputer = new MetricComputerJavaDecorator(toDecorate, repo);
  }

  public void testMethodCost() throws Exception {
    decoratedComputer.compute(MethodCost.class.getName());
  }

  public void testClassCost() throws Exception {
    decoratedComputer.compute(ClassCost.class.getName());
  }

  public void testClassRepository() throws Exception {
    decoratedComputer.compute(JavaClassRepository.class.getName());
  }

  public void testClassReader() throws Exception {
    decoratedComputer.compute(ClassReader.class.getName());
  }

  public void testPrimeness() throws Exception {
    decoratedComputer.compute(Primeness.class.getName());
  }

  public void testSumOfPrimes() throws Exception {
    decoratedComputer.compute(SumOfPrimes1.class.getName());
  }

  public void testSumOfPrimes2() throws Exception {
    decoratedComputer.compute(SumOfPrimes2.class.getName());
  }


}
