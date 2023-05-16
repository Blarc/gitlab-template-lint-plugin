package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotifications


fun lintGitlabYaml(file: PsiFile) {
    if (GitlabLintUtils.isGitlabYaml(file.virtualFile)) {
        lint(file)
    }
}

fun lint(file: PsiFile) {
    runBackgroundableTask(GitlabLintBundle.message("inspection.title"), file.project) { indicator ->
        indicator.isIndeterminate = true
        val project = file.project
        val pipeline = project.service<Pipeline>()
        pipeline.accept(file)
        EditorNotifications.getInstance(project).updateAllNotifications()
    }
}
