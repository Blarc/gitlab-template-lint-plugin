package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabDetector
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class ApplicationStartupListener: StartupActivity.DumbAware {
    override fun runActivity(project: Project) {
        showVersionNotification(project)
    }

    private fun showVersionNotification(project: Project) {
        val settings = service<AppSettings>()
        val version = GitlabLintBundle.plugin()?.version

        if (version == settings.lastVersion) {
            return
        }

        settings.lastVersion = version
        sendNotification(Notification.welcome(version ?: "Unknown"), project)
    }
}
