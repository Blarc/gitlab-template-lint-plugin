package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class ApplicationStartupListener: StartupActivity.DumbAware {

    private var firstTime = true
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

        if (firstTime) {
            sendNotification(Notification.welcome(version ?: "Unknown"), project)
        }
        firstTime = false
    }
}
