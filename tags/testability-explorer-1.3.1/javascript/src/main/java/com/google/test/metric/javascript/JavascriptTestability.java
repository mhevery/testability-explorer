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
package com.google.test.metric.javascript;

import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.Token;

import java.io.IOException;

/**
 * Main entry point for assessing cost to test Javascript
 *
 * @author alexeagle@google.com (Alex Eagle)
 */
public class JavascriptTestability {
  private final FileRepository repository;

  public JavascriptTestability(FileRepository repository) {
    this.repository = repository;
  }

  public int calculateCost() throws IOException {
    FunctionCountNodeVisitor v = new FunctionCountNodeVisitor();
    for (AstRoot astRoot : repository.getAsts()) {
      astRoot.visit(v);
    }
    return v.getCount();
  }

  private class FunctionCountNodeVisitor implements NodeVisitor {
    int count = 0;
    public boolean visit(AstNode node) {
      if (node.getType() == Token.FUNCTION) {
        if (node.getEnclosingFunction() != null) {
          count++;
        }
      }
      return true;
    }

    public int getCount() {
      return count;
    }
  }
}
