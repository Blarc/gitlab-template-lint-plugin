// This is a generated file. Not intended for manual editing.
package com.github.blarc.gitlab.template.lint.plugin.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.github.blarc.gitlab.template.lint.plugin.language.psi.impl.*;

public interface GitlabYamlTokenTypes {

  IElementType PROPERTY = new GitlabYamlElementType("PROPERTY");

  IElementType COMMENT = new GitlabYamlTokenType("COMMENT");
  IElementType CRLF = new GitlabYamlTokenType("CRLF");
  IElementType KEY = new GitlabYamlTokenType("KEY");
  IElementType SEPARATOR = new GitlabYamlTokenType("SEPARATOR");
  IElementType VALUE = new GitlabYamlTokenType("VALUE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == PROPERTY) {
        return new GitlabYamlPropertyImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
