package com.github.blarc.gitlab.template.lint.plugin

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class GitlabLintUtils {
    companion object{
        fun createNotification(project: Project, message: String, type: NotificationType = NotificationType.ERROR) {
            NotificationGroupManager.getInstance()
                .getNotificationGroup("GitlabLint")
                .createNotification("Gitlab Lint", message, type)
                .notify(project)
        }
    }
}
