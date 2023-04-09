package com.github.blarc.gitlab.template.lint.plugin.pipeline

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement

class PassResolveInclude(project: Project, val includePsiElement: PsiElement) : Pass(project, includePsiElement.containingFile) {
}