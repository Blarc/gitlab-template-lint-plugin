package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class RecordHit : Middleware {
    override val priority = 20

    override fun invoke(pass: Pass, next: () -> GitlabLintResponse?) : GitlabLintResponse? {
        val gitlabLintResponse = next() ?: return null

        service<AppSettings>().recordHit();

        return gitlabLintResponse;
    }
}

