// This is a generated file. Not intended for manual editing.
package com.github.blarc.gitlab.template.lint.plugin.language.psi.impl;

import java.util.List;
import org.jetbrains.annotations.*;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;
import static com.github.blarc.gitlab.template.lint.plugin.language.psi.GitlabYamlTokenTypes.*;
import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.github.blarc.gitlab.template.lint.plugin.language.psi.*;

public class GitlabYamlPropertyImpl extends ASTWrapperPsiElement implements GitlabYamlProperty {

  public GitlabYamlPropertyImpl(@NotNull ASTNode node) {
    super(node);
  }

  public void accept(@NotNull GitlabYamlVisitor visitor) {
    visitor.visitProperty(this);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof GitlabYamlVisitor) accept((GitlabYamlVisitor)visitor);
    else super.accept(visitor);
  }

  @Override
  @Nullable
  public String getKey() {
    return GitlabYamlPsiImplUtil.getKey(this);
  }

  @Override
  @Nullable
  public String getValue() {
    return GitlabYamlPsiImplUtil.getValue(this);
  }

}
