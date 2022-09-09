package com.github.blarc.gitlab.template.lint.plugin.widget.actions

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.openPluginSettings
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.DumbAwareAction

class OpenSettingsAction : DumbAwareAction(message("actions.open.settings")) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            openPluginSettings(project)
        }
    }
}
