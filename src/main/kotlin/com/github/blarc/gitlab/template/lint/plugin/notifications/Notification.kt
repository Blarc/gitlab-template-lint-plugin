package com.github.blarc.gitlab.template.lint.plugin.notifications

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.openPluginSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.ide.browsers.BrowserLauncher
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.net.URI

data class Notification(
    val title: String? = null,
    val message: String,
    val actions: Set<NotificationAction> = setOf(),
    val type: Type = Type.PERSISTENT
) {
    enum class Type {
        PERSISTENT,
        TRANSIENT
    }

    companion object {
        private val DEFAULT_TITLE = message("notifications.title")


        fun welcome(version: String) = Notification(message = message("notifications.welcome", version))

        fun repositoryNotFound() = Notification(DEFAULT_TITLE, message("notifications.repository-not-found"))

        fun remoteNotFound() = Notification(DEFAULT_TITLE, message("notifications.remote-not-found"))

        fun remoteIdNotFound() = Notification(DEFAULT_TITLE, message("notifications.remote-id-not-found"))

        fun star() = Notification(
            message = """
                Finding Gitlab Lint useful? Show your support 💖 and ⭐ the repository 🙏.
            """.trimIndent(),
            actions = setOf(
                NotificationAction.openRepository() {
                    service<AppSettings>().requestSupport = false;
                },
                NotificationAction.doNotAskAgain() {
                    service<AppSettings>().requestSupport = false;
                }
            )
        )

        fun gitlabTokenNotSet(project: Project) = Notification(
            message = message("notifications.gitlab-token-not-set"),
            actions = setOf(NotificationAction.settings(project, message("actions.set-gitlab-token")))
        )

    }

    fun isTransient() = type == Type.TRANSIENT
    fun isPersistent() = !isTransient();
}

data class NotificationAction(val title: String, val run: (dismiss: () -> Unit) -> Unit) {
    companion object {
        fun settings(project: Project, title: String = message("settings.title")) = NotificationAction(title) { dismiss ->
            dismiss()
            openPluginSettings(project)
        }

        fun openRepository(onComplete: () -> Unit) = NotificationAction(message("actions.sure-take-me-there")) { dismiss ->
            GitlabLintBundle.openRepository()
            dismiss()
            onComplete()
        }

        fun doNotAskAgain(onComplete: () -> Unit) = NotificationAction(message("actions.do-not-ask-again")) { dismiss ->
            dismiss()
            onComplete()
        }

        fun openUrl(url: URI, title: String = message("actions.take-me-there")) = NotificationAction(title) { dismiss ->
            dismiss()
            BrowserLauncher.instance.open(url.toString());
        }
    }
}