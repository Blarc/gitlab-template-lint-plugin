package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabFactory
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service

@Service
class ResolveGitlabToken : Middleware {
    override val priority = 5
    private var showGitlabTokenNotification = true


    override fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?): Pair<GitlabLintResponse?, LintStatusEnum>? {
        val remote = pass.remoteOrThrow()
        val url = remote.httpUrl ?: return null

        val gitlabToken = getGitlabToken(pass) ?: return null
        pass.gitlab = GitlabFactory.getInstance(pass.project)!!.getGitLab(url, gitlabToken)

        return next()
    }

    private fun getGitlabToken(pass: Pass) : String? {
        val gitlabToken = AppSettings.instance?.gitlabToken

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
