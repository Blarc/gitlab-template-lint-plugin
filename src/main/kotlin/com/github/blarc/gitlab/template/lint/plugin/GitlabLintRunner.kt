package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotifications


fun runLinting(file: PsiFile) {
    runBackgroundableTask(GitlabLintBundle.message("inspection.title"), file.project) { indicator ->
        indicator.isIndeterminate = true
        val project = file.project
        if (GitlabLintUtils.matchesGitlabLintGlob(file.virtualFile.path)) {
            val pipeline = project.service<Pipeline>()
            pipeline.accept(file)
        }
        EditorNotifications.getInstance(project).updateAllNotifications()
    }
}
