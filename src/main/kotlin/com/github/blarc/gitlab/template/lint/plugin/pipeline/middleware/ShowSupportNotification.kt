package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabObject
import com.github.blarc.gitlab.template.lint.plugin.notifications.Notification
import com.github.blarc.gitlab.template.lint.plugin.notifications.sendNotification
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.pipeline.PipelineStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class ShowSupportNotification : Middleware {
    override val priority = 10

    override fun invoke(pass: Pass, next: () -> Pair<GitlabObject?, PipelineStatusEnum>?): Pair<GitlabObject?, PipelineStatusEnum>? {
        val gitlabLintResponse = next()

        val settings = service<AppSettings>()

        if (settings.requestSupport && (settings.hits == 50 || settings.hits % 100 == 0)) {
            sendNotification(Notification.star())
        }

        return gitlabLintResponse;
    }
}
