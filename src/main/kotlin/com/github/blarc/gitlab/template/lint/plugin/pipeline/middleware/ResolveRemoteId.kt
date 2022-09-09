package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import java.net.URI

@Service
class ResolveRemoteId : Middleware {
    override val priority = 30

    override fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?): Pair<GitlabLintResponse?, LintStatusEnum>? {
        val remoteUrl = pass.remoteOrThrow().httpUrl ?: return null
        val gitlabUrl = pass.gitlabUrlOrThrow()
        val gitlabToken = pass.gitlabTokenOrThrow()

        pass.remoteId = resolveRemoteId(gitlabUrl, gitlabToken, remoteUrl, pass.project) ?: return Pair(null, LintStatusEnum.INVALID_ID)

        return next()
    }

    private fun resolveRemoteId(gitlabUrl: String, gitlabToken: String, remoteUrl: URI, project: Project): Long? {

        val remotesMap = AppSettings.instance?.remotesMap
        val remoteUrlString = remoteUrl.toString()

        return if (remotesMap != null && remotesMap.containsKey(remoteUrlString)) {
            remotesMap[remoteUrlString]
        }
        else {
            val gitlab = project.service<Gitlab>()
            gitlab.searchProjectId(gitlabUrl, gitlabToken, remoteUrlString).get().let {

                // Cache project's ID
                if (remotesMap != null && !remotesMap.containsKey(remoteUrlString)) {
                    remotesMap[remoteUrlString] = it
                }
                it
            }
        }
    }
}
