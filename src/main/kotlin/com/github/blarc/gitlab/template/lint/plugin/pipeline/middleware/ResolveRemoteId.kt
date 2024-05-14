package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class ResolveRemoteId : Middleware {
    override val priority = 30
    private var showRemoteIdNotification = true

    override fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?): Pair<GitlabLintResponse?, LintStatusEnum>? {
        val remoteUrl = pass.remoteOrThrow()
        val gitlabUrl = pass.gitlabUrlOrThrow()
        val gitlabToken = pass.gitlabTokenOrThrow()

        pass.remoteId = resolveRemoteId(gitlabUrl, gitlabToken, remoteUrl, pass.project) ?: return Pair(null, LintStatusEnum.INVALID_ID)

        return next()
    }

    private fun resolveRemoteId(gitlabUrl: String, gitlabToken: String, remoteUrl: String, project: Project): Long? {

        val remotesMap = AppSettings.instance.remotes

        return if (remotesMap.containsKey(remoteUrl) && remotesMap[remoteUrl]?.remoteId != null) {
            showRemoteIdNotification = true
            remotesMap[remoteUrl]!!.remoteId
        }
        else {
            val gitlab = project.service<Gitlab>()

            // This prevents multiple notifications from showing up
            val showRemoteIdNotificationTemp = showRemoteIdNotification
            showRemoteIdNotification = false
            gitlab.searchProjectId(gitlabUrl, gitlabToken, remoteUrl, showRemoteIdNotificationTemp).get().let {
                showRemoteIdNotification = it != null

                // Cache project's ID
                if (remotesMap.containsKey(remoteUrl)) {
                    remotesMap[remoteUrl]!!.remoteId = it
                }
                it
            }
        }
    }
}
