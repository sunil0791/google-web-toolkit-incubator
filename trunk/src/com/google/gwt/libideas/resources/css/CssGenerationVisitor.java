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
package com.google.gwt.libideas.resources.css;

import com.google.gwt.dev.util.TextOutput;
import com.google.gwt.libideas.resources.css.ast.Context;
import com.google.gwt.libideas.resources.css.ast.CssDef;
import com.google.gwt.libideas.resources.css.ast.CssEval;
import com.google.gwt.libideas.resources.css.ast.CssIf;
import com.google.gwt.libideas.resources.css.ast.CssMediaRule;
import com.google.gwt.libideas.resources.css.ast.CssNode;
import com.google.gwt.libideas.resources.css.ast.CssPageRule;
import com.google.gwt.libideas.resources.css.ast.CssProperty;
import com.google.gwt.libideas.resources.css.ast.CssRule;
import com.google.gwt.libideas.resources.css.ast.CssSelector;
import com.google.gwt.libideas.resources.css.ast.CssSprite;
import com.google.gwt.libideas.resources.css.ast.CssUrl;
import com.google.gwt.libideas.resources.css.ast.CssVisitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 */
public class CssGenerationVisitor extends CssVisitor {

  private final TextOutput out;
  private boolean needsOpenBrace;
  private boolean needsComma;
  private final SortedMap<Integer, List<CssNode>> substitutionPositions = new TreeMap<Integer, List<CssNode>>();

  public CssGenerationVisitor(TextOutput out) {
    this.out = out;
  }

  @Override
  public void endVisit(CssIf x, Context ctx) {
    // Match up an explanatory comment
    out.printOpt("/* } */");
    out.newlineOpt();
  }

  @Override
  public void endVisit(CssMediaRule x, Context ctx) {
    out.indentOut();
    out.print("}");
    out.newline();
  }

  @Override
  public void endVisit(CssPageRule x, Context ctx) {
    out.indentOut();
    out.print("}");
    out.newline();
  }

  @Override
  public void endVisit(CssRule x, Context ctx) {
    if (!x.getProperties().isEmpty()) {
      // Don't print empty rule blocks
      closeBrace();
    }
  }

  public SortedMap<Integer, List<CssNode>> getSubstitutionPositions() {
    return substitutionPositions;
  }

  @Override
  public boolean visit(CssDef x, Context ctx) {
    // These are not valid CSS
    out.printOpt("/* CssDef */");
    out.newlineOpt();
    return false;
  }

  @Override
  public boolean visit(CssEval x, Context ctx) {
    // These are not valid CSS
    out.printOpt("/* CssEval */");
    out.newlineOpt();
    return false;
  }

  @Override
  public boolean visit(CssIf x, Context ctx) {
    // Record where the contents of the if block should be inserted
    out.printOpt("/* @if " + x.getExpression() + " { */");
    out.newlineOpt();
    out.indentIn();
    addSubstitition(x);
    return false;
  }

  @Override
  public boolean visit(CssMediaRule x, Context ctx) {
    out.print("@MEDIA");
    for (String m : x.getMedias()) {
      out.print(" " + m);
    }
    spaceOpt();
    out.print("{");
    out.newlineOpt();
    out.indentIn();
    return true;
  }

  @Override
  public boolean visit(CssPageRule x, Context ctx) {
    out.print("@page");
    if (x.getPseudoPage() != null) {
      out.print(" :");
      out.print(x.getPseudoPage());
    }
    spaceOpt();
    out.print("{");
    out.newlineOpt();
    out.indentIn();
    return true;
  }

  @Override
  public boolean visit(CssProperty x, Context ctx) {
    if (needsOpenBrace) {
      openBrace();
      needsOpenBrace = false;
    }

    out.print(x.getName());
    colon();
    if (x.getExpression() != null) {
      addSubstitition(x);
    } else {
      Iterator<String> i = x.getValues().listIterator();
      while (i.hasNext()) {
        out.print(i.next());
        if (i.hasNext()) {
          out.print(' ');
        }
      }
    }

    if (x.isImportant()) {
      important();
    }

    semi();

    return true;
  }

  @Override
  public boolean visit(CssRule x, Context ctx) {
    if (x.getProperties().isEmpty()) {
      // Don't print empty rule blocks
      return false;
    }

    needsOpenBrace = true;
    needsComma = false;
    return true;
  }

  @Override
  public boolean visit(CssSelector x, Context ctx) {
    if (needsComma) {
      comma();
    }
    needsComma = true;
    out.print(x.getSelector());
    return true;
  }

  @Override
  public boolean visit(CssSprite x, Context ctx) {
    // These are not valid CSS
    out.printOpt("/* CssSprite */");
    out.newlineOpt();
    addSubstitition(x);
    return false;
  }

  @Override
  public boolean visit(CssUrl x, Context ctx) {
    // These are not valid CSS
    out.printOpt("/* CssUrl */");
    out.newlineOpt();
    return false;
  }

  private void addSubstitition(CssNode node) {
    int position = out.toString().length();
    if (substitutionPositions.containsKey(position)) {
      substitutionPositions.get(position).add(node);
    } else {
      List<CssNode> nodes = new ArrayList<CssNode>();
      nodes.add(node);
      substitutionPositions.put(position, nodes);
    }
  }

  private void closeBrace() {
    out.indentOut();
    out.print('}');
    out.newline();
  }

  private void colon() {
    spaceOpt();
    out.print(':');
    spaceOpt();
  }

  private void comma() {
    out.print(',');
    spaceOpt();
  }

  private void important() {
    out.print(" !important");
  }

  private void openBrace() {
    spaceOpt();
    out.print('{');
    out.newlineOpt();
    out.indentIn();
  }

  private void semi() {
    out.print(';');
    out.newlineOpt();
  }

  private void spaceOpt() {
    out.printOpt(' ');
  }
}
