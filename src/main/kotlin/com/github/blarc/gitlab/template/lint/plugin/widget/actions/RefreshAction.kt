package com.github.blarc.gitlab.template.lint.plugin.widget.actions

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.ui.EditorNotifications

class RefreshAction() : DumbAwareAction(message("actions.refresh")) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            runBackgroundableTask(message("inspection.title"), project) { indicator ->
                indicator.isIndeterminate = true
                project.service<Pipeline>().rerun()
                EditorNotifications.getInstance(project).updateAllNotifications()
            }
        }
    }

}
