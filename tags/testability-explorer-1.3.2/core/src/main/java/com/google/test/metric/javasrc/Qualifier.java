/*
 * Copyright 2009 Google Inc.
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
 package com.google.test.metric.javasrc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import antlr.collections.AST;

public class Qualifier {

  private String pkg = "";
  private List<String> imports = new ArrayList<String>();
  private Map<String, String> aliases = new HashMap<String, String>();

  public void compilationUnit(AST ast) {
    searchAST(ast, "");
  }

  private void searchAST(AST ast, String prefix) {
    while(ast != null) {
      if (ast.getType() == JavaTokenTypes.PACKAGE_DEF) {
        setPackage(ast.getFirstChild());
      } else if (ast.getType() == JavaTokenTypes.IMPORT) {
        addImport(stringify(ast.getFirstChild()));
      } else if (ast.getType() == JavaTokenTypes.STATIC_IMPORT) {
        //System.out.println(ast.toStringList());
      } else if (ast.getType() == JavaTokenTypes.CLASS_DEF) {
        aliasType(ast.getFirstChild(), prefix);
      } else {
        searchAST(ast.getFirstChild(), prefix);
      }
      ast = ast.getNextSibling();
    }
  }

  private void aliasType(AST ast, String prefix) {
    String fullName = prefix + ast.getNextSibling().getText();
    String qualifiedName = pkg + fullName;
    addAlias(fullName, qualifiedName);
    searchAST(ast, fullName + "$");
  }

  private void setPackage(AST ast) {
    ast = ast.getNextSibling(); // ANNOTATIONS
    setPackage(stringify(ast));
  }

  public String stringify(AST ast) {
    if (ast == null)
      return "";
    String text = "";
    AST child = ast.getFirstChild();
    if (child != null)
      text += stringify(child);
    text += ast.getText();
    if (child != null)
      text += stringify(child.getNextSibling());
    return text;
  }

  public void setPackage(String pkg) {
    this.pkg = pkg +  ".";
  }

  public String qualify(String context, String ident) {
    // Look for inner class first
    String[] innerClasses = context.split("\\$");
    for (int i = innerClasses.length; i >= 0; --i) {
      String innerClass = join(innerClasses, i) + ident.replace('.', '$');
      if (aliases.containsKey(innerClass)) {
        return aliases.get(innerClass);
      }
    }

    // Check the aliases
    if (aliases.containsKey(ident)) {
      return aliases.get(ident);
    }

    // If we already seem to be qualified as we have a dot
    if (ident.contains(".")) {
      return ident;
    }

    // Look through the import statements
    for (String imprt : imports) {
      if (imprt.endsWith("." + ident)) {
        return imprt;
      }
    }
    // Java lang is the default import.
    try {
      String javaDefault = "java.lang." + ident;
      Class.forName(javaDefault);
      return javaDefault;
    } catch (ClassNotFoundException e) {
    }

    // Defaoult to current package.
    return pkg + ident;
  }

  private String join(String[] strings, int firstN) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < firstN; i++) {
      builder.append(strings[i]);
      builder.append("$");
    }
    return builder.toString();
  }

  public void addImport(String imprt) {
    imports.add(imprt);
  }

  public void addAlias(String ident, String qualified) {
    aliases.put(ident, qualified);
    aliases.put(qualified, qualified);
    aliases.put(qualified.replace('$', '.'), qualified);
  }

  public String qualify(String context, AST ast) {
    return qualify(context, stringify(ast));
  }

  public String getPackage() {
    return pkg;
  }

}
