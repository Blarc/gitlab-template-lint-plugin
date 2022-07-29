package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.git.httpUrl
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.components.Service

@Service
class ResolveRemoteId : Middleware {
    override val priority = 10

    override fun invoke(pass: Pass, next: () -> GitlabLintResponse?): GitlabLintResponse? {
        val remoteUrl = pass.remoteOrThrow().httpUrl ?: return null
        val gitlab = pass.gitlabOrThrow()

        val remotesMap = AppSettings.instance?.remotesMap
        val remoteUrlString = remoteUrl.toString()
        if (remotesMap != null && remotesMap.containsKey(remoteUrlString)) {
            pass.remoteId = remotesMap[remoteUrlString]
        }
        else {
            gitlab.searchProjectId(remoteUrlString).get().let { remoteId ->

                remoteId ?: sendNotification(Notification.remoteIdNotFound(), pass.project)

                // Cache project's ID
                if (remotesMap != null && !remotesMap.containsKey(remoteUrlString)) {
                    remotesMap[remoteUrlString] = remoteId
                }
                pass.remoteId = remoteId
            }
        }

        return next()
    }


}
