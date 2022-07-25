package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

class GitlabLintUtils {
    companion object{
        fun createNotification(project: Project, message: String, type: NotificationType = NotificationType.ERROR) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("GitlabLint")
                .createNotification("Gitlab Lint", message, type)
                .notify(project)
        }

        fun getGitlabLintRegex(): Regex {
            val appSettingsState = AppSettingsState.instance
            if (appSettingsState?.gitlabLintRegexString != null) {
                return Regex(appSettingsState.gitlabLintRegexString!!)
            }
            return Regex(".*gitlab-ci\\.(yaml|yml)$")
        }

        fun matchesGitlabLintRegex(text: String): Boolean {
            return getGitlabLintRegex().matches(text)
        }
    }
}
