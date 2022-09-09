package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class LintContext : Middleware {
    override val priority = 50
    var gitlabLintResponse: GitlabLintResponse? = null
    private var showGitlabTokenNotification = true

    override fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?): Pair<GitlabLintResponse?, LintStatusEnum>? {
        val remoteId = pass.remoteIdOrThrow()
        val branch = pass.repositoryOrThrow().currentBranchName ?: return null

        val gitlabUrl = pass.gitlabUrlOrThrow()
        val gitlabToken = pass.gitlabTokenOrThrow()

        val gitlab = pass.project.service<Gitlab>()


        gitlabLintResponse = gitlab.lintContent(
            gitlabUrl,
            gitlabToken,
            pass.file.text,
            remoteId,
            branch,
            showGitlabTokenNotification
        ).get()


        val lintStatus = if (gitlabLintResponse?.valid == true) {
            showGitlabTokenNotification = true
            LintStatusEnum.VALID
        } else {
            showGitlabTokenNotification = false
            LintStatusEnum.INVALID
        }
        return Pair(gitlabLintResponse, lintStatus)
    }
}
