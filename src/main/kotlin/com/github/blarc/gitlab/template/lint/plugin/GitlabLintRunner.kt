package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiFile


fun runLinting(project: Project, file: PsiFile) {
    val pipeline = project.service<Pipeline>()
    pipeline.accept(file)
}
