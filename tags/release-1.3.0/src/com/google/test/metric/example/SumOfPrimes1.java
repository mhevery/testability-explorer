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
package com.google.test.metric.example;

public class SumOfPrimes1 {

  private final Primeness primeness = new Primeness();
  
  public int sum(int max) {
    int sum = 0;
    for (int i = 0; i < max; i++) {
      if (primeness.isPrime(i)) {
        sum += i;
      }
    }
    return sum;
  }
  
}
