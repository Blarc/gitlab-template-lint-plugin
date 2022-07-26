package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.openapi.components.Service

@Service
class ResolveRemoteId : Middleware {
    override val priority = 10

    override fun invoke(pass: Pass, next: () -> GitlabLintResponse?): GitlabLintResponse? {
        val remoteUrl = pass.remoteOrThrow().httpUrl ?: return null
        val gitlab = pass.gitlabOrThrow()

        val remotesMap = AppSettingsState.instance?.remotesMap
        val remoteUrlString = remoteUrl.toString()
        if (remotesMap != null && remotesMap.containsKey(remoteUrlString)) {
            pass.remoteId = remotesMap[remoteUrlString]
        }
        else {
            gitlab.searchProjectId(remoteUrlString).get().let {
                // Cache project's ID
                if (remotesMap != null && !remotesMap.containsKey(remoteUrlString)) {
                    remotesMap[remoteUrlString] = it
                }
                pass.remoteId = it
            }
        }

        return next()
    }


}
