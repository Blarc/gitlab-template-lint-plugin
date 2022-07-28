package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.intellij.openapi.components.Service

@Service
class LintContext : Middleware {
    override val priority = 50
    var gitlabLintResponse: GitlabLintResponse? = null

    override fun invoke(pass: Pass, next: () -> GitlabLintResponse?): GitlabLintResponse? {
        val remoteId = pass.remoteIdOrThrow()
        val branchName = pass.repositoryOrThrow().currentBranchName ?: return null
        val gitlab = pass.gitlabOrThrow()

        gitlabLintResponse = gitlab.lintContent(pass.file.text, remoteId, branchName).get()
        return gitlabLintResponse
    }
}
