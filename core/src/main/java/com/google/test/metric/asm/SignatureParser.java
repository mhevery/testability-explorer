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
package com.google.test.metric.asm;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import com.google.test.metric.JavaType;
import com.google.test.metric.Type;

public class SignatureParser extends NoopSignatureVisitor {

  interface Setter {
    void set(Type type);
  }

  public static class TypeVisitor extends NoopSignatureVisitor {
    private final Setter setter;

    public TypeVisitor(Setter setter) {
      this.setter = setter;
    }

    @Override
    public SignatureVisitor visitArrayType() {
      return new TypeVisitor(new Setter() {
        public void set(Type type) {
          setter.set(type.toArray());
        }
      });
    }

    @Override
    public void visitBaseType(char descriptor) {
      setter.set(JavaType.fromDesc("" + descriptor));
    }

    @Override
    public void visitClassType(String name) {
      setter.set(JavaType.fromJava(name));
    }

  }

  private final List<Type> parameters = new ArrayList<Type>();
  private Type returnType;

  @Override
  public SignatureVisitor visitArrayType() {
    return new TypeVisitor(new Setter() {
      public void set(Type type) {
        parameters.add(type.toArray());
      }
    });
  }

  @Override
  public void visitBaseType(char descriptor) {
    parameters.add(JavaType.fromDesc("" + descriptor));
  }

  @Override
  public void visitClassType(String name) {
    parameters.add(JavaType.fromJava(name));
  }

  @Override
  public SignatureVisitor visitParameterType() {
    return this;
  }

  @Override
  public SignatureVisitor visitReturnType() {
    return new TypeVisitor(new Setter() {
      public void set(Type type) {
        returnType = type;
      }
    });
  }

  public List<Type> getParameters() {
    return parameters;
  }

  public Type getReturnType() {
    return returnType;
  }

  public static SignatureParser parse(String signature) {
    SignatureParser parser = new SignatureParser();
    new SignatureReader(signature).accept(parser);
    return parser;
  }

}
