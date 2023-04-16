package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabObject
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.providers.EditorWithMergedPreview
import com.github.blarc.gitlab.template.lint.plugin.pipeline.PipelineStatusEnum
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager

@Service
class LintTemplate : Middleware {
    override val priority = 50
    private var showGitlabTokenNotification = true

    override fun invoke(pass: Pass, next: () -> Pair<GitlabObject?, PipelineStatusEnum>?): Pair<GitlabObject?, PipelineStatusEnum>? {
        val remoteId = pass.remoteIdOrThrow()
        val branch = pass.repositoryOrThrow().currentBranchName ?: return null

        val gitlabUrl = pass.gitlabUrlOrThrow()
        val gitlabToken = pass.gitlabTokenOrThrow()

        val gitlab = pass.project.service<Gitlab>()

        val lintTemplate = gitlab.lintTemplate(
            gitlabUrl,
            gitlabToken,
            pass.file.text,
            remoteId,
            branch,
            showGitlabTokenNotification
        ).get()


        val lintStatus = if (lintTemplate?.valid == true) {
            showGitlabTokenNotification = true

            WriteCommandAction.runWriteCommandAction(pass.project) {
                val selectedEditor = FileEditorManager.getInstance(pass.project).getSelectedEditor(pass.file.virtualFile)
                if (selectedEditor is EditorWithMergedPreview) {
                    lintTemplate.mergedYaml?.let { selectedEditor.setPreviewText(it) }
                }
            }

            PipelineStatusEnum.VALID
        } else {
            showGitlabTokenNotification = false
            PipelineStatusEnum.INVALID
        }
        return Pair(lintTemplate, lintStatus)
    }
}
