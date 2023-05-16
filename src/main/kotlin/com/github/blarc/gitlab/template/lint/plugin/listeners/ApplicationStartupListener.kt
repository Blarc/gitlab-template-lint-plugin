package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.lintGitlabYaml
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class ApplicationStartupListener : ProjectActivity {

    private var firstTime = true
    override suspend fun execute(project: Project) {
        showVersionNotification(project)
        DataManager.getInstance().dataContextFromFocusAsync.onSuccess {
            runReadAction {
                it.getData(PlatformDataKeys.PSI_FILE)?.let { file ->
                    lintGitlabYaml(file)
                }
            }
        }
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
