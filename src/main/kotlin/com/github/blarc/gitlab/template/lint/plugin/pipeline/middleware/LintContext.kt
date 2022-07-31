package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.components.Service

@Service
class LintContext : Middleware {
    override val priority = 50
    var gitlabLintResponse: GitlabLintResponse? = null
    private var showGitlabTokenNotification = true

    override fun invoke(pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?): Pair<GitlabLintResponse?, LintStatusEnum>? {
        val remoteId = pass.remoteIdOrThrow()
        val branchName = pass.repositoryOrThrow().currentBranchName ?: return null

        val gitlab = Gitlab(pass.project)

        gitlabLintResponse = gitlab.lintContent(pass.file.text, remoteId, branchName, pass.project, showGitlabTokenNotification).get()
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
