package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity

class GitlabPostStartupActivity: StartupActivity.Background {
    override fun runActivity(project: Project) {
        val gitlabToken = AppSettingsState.instance?.gitlabToken
        if (gitlabToken == null || gitlabToken.isEmpty()) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("GitlabLint")
                .createNotification("Gitlab Lint","Please set your private gitlab token in settings!", NotificationType.ERROR)
                .notify(project)

            return
        }
    }
}
