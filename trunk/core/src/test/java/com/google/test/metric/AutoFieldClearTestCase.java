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

import java.lang.reflect.Field;

import junit.framework.TestCase;

public abstract class AutoFieldClearTestCase extends TestCase {

  @Override
  protected void tearDown() throws Exception {
    setAllFieldsNull(this); // To prevent out of memory
    super.tearDown();
  }

  private static void setAllFieldsNull(Object object) {
    Class<?> clazz = object.getClass();
    while (clazz != null && clazz != TestCase.class) {
      for (Field field : clazz.getDeclaredFields()) {
        field.setAccessible(true);
        try {
          field.set(object, null);
        } catch (Exception e) {
        }
      }
      clazz = clazz.getSuperclass();
    }
  }

}
