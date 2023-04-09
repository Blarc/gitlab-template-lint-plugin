package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabObject
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.pipeline.PassResolveInclude
import com.github.blarc.gitlab.template.lint.plugin.widget.PipelineStatusEnum
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class ResolveInclude : Middleware {
    override val priority = 50

    private var showGitlabTokenNotification = true

    override fun invoke(
        pass: Pass,
        next: () -> Pair<GitlabObject?, PipelineStatusEnum>?
    ): Pair<GitlabObject?, PipelineStatusEnum>? {
        pass as PassResolveInclude
        val remoteId = pass.remoteIdOrThrow()
        val branch = pass.repositoryOrThrow().currentBranchName ?: return null

        val gitlabUrl = pass.gitlabUrlOrThrow()
        val gitlabToken = pass.gitlabTokenOrThrow()

        val gitlab = pass.project.service<Gitlab>()

        val includePsiElement = pass.includePsiElement
        // TODO @Blarc Compute the path to the file

        val resolveTemplate = gitlab.getFile(
            gitlabUrl,
            gitlabToken,
            "filePath",
            remoteId,
            branch,
            showGitlabTokenNotification
        ).get()

        // If resolve template is null, there was an error and notification was shown,
        // so we don't want to show it again
        showGitlabTokenNotification = resolveTemplate != null

        return Pair(resolveTemplate, PipelineStatusEnum.VALID)
    }
}