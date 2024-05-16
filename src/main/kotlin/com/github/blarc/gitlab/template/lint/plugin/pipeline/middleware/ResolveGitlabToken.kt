package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service

@Service(Service.Level.PROJECT)
class ResolveGitlabToken : Middleware {
    override val priority = 5
    private var showGitlabTokenNotification = true

    override suspend fun invoke(
        pass: Pass,
        next: suspend () -> Pair<GitlabLintResponse?, LintStatusEnum>?
    ): Pair<GitlabLintResponse?, LintStatusEnum>? {

        val gitlabToken = resolveGitlabToken(pass) ?: return null

        pass.gitlabToken = gitlabToken

        return next()
    }

    private suspend fun resolveGitlabToken(pass: Pass) : String? {
        val gitlabUrl = pass.gitlabUrlOrThrow()

        val gitlabToken = AppSettings.instance.getGitlabToken(gitlabUrl)
        if (gitlabToken.isNullOrEmpty() && showGitlabTokenNotification) {
            sendNotification(Notification.gitlabTokenNotSet(pass.project), pass.project)
            showGitlabTokenNotification = false
        }
        else if (!gitlabToken.isNullOrEmpty()) {
            showGitlabTokenNotification = true
        }

        return if (gitlabToken.isNullOrEmpty()) return null else gitlabToken
    }

}
