package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.openapi.components.service
import com.intellij.platform.ide.progress.withBackgroundProgress
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


fun lintGitlabYaml(file: PsiFile) {
    if (GitlabLintUtils.isGitlabYaml(file.virtualFile)) {
        lint(file)
    }
}

fun lint(file: PsiFile) {
    val project = file.project
    val pipeline = project.service<Pipeline>()

    CoroutineScope(Dispatchers.Default).launch {
        withBackgroundProgress(file.project, GitlabLintBundle.message("inspection.title")) {
            pipeline.accept(file)
            EditorNotifications.getInstance(project).updateAllNotifications()
        }
    }
}
