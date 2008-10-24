/*
 * Copyright 2008 Google Inc.
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
 
package com.google.test.metric.testing;

import com.google.test.metric.ClassRepository;
import com.google.test.metric.CostModel;
import com.google.test.metric.JavaClassRepository;
import com.google.test.metric.MetricComputer;
import com.google.test.metric.RegExpWhiteList;

import java.io.PrintStream;

/**
 * For use in tests, this is a preferable way to construct a {@code MetricComputer},
 * as opposed to inheriting from a TestCase that does it for you. Prefer composition
 * over inheritance.
 *
 * @author Jonathan Andrew Wolter
 */
public class MetricComputerBuilder {

  private ClassRepository repo = new JavaClassRepository();
  private PrintStream printStream = null;
  private RegExpWhiteList whitelist = new RegExpWhiteList();
  private CostModel costModel = new CostModel();

  public MetricComputerBuilder withClassRepository(ClassRepository repository) {
    this.repo = repository;
    return this;
  }

  public MetricComputerBuilder withPrintStream(PrintStream stream) {
    this.printStream = stream;
    return this;
  }

  public MetricComputerBuilder withWhitelist(RegExpWhiteList regExpWhitelist) {
    this.whitelist = regExpWhitelist;
    return this;
  }

  public MetricComputerBuilder withCostModel(CostModel model) {
    this.costModel = model;
    return this;
  }

  public MetricComputer build() {
    return new MetricComputer(repo, printStream, whitelist, costModel);
  }

}
