package com.github.blarc.gitlab.template.lint.plugin.pipeline.middleware

import com.github.blarc.gitlab.template.lint.plugin.gitlab.Gitlab
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintErrorsEnum
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pass
import com.github.blarc.gitlab.template.lint.plugin.providers.EditorWithMergedPreview
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditorManager

@Service
class LintContext : Middleware {
    override val priority = 50
    private var showGitlabTokenNotification = true

    override fun invoke(
        pass: Pass, next: () -> Pair<GitlabLintResponse?, LintStatusEnum>?
    ): Pair<GitlabLintResponse?, LintStatusEnum>? {
        val remoteId = pass.remoteIdOrThrow()
        val branch = pass.repositoryOrThrow().currentBranchName ?: return null

        val gitlabUrl = pass.gitlabUrlOrThrow()
        val gitlabToken = pass.gitlabTokenOrThrow()

        val gitlab = pass.project.service<Gitlab>()

        // Try to lint with current branch
        var gitlabLintResponse = lintContent(gitlab, gitlabUrl, gitlabToken, pass, remoteId, branch)
        if (gitlabLintResponse?.valid == true) {
            showGitlabTokenNotification = true
            setMergedPreview(pass, gitlabLintResponse.mergedYaml)
            return Pair(gitlabLintResponse, LintStatusEnum.VALID)
        }

        // Reduce errors by ignored errors
        val ignoredErrors = pass.project.service<ProjectSettings>().ignoredErrors
        gitlabLintResponse?.errors?.removeIf { ignoredErrors[pass.file.virtualFile.path]?.contains(it) == true }
        if (gitlabLintResponse?.errors?.isEmpty() == true) {
            gitlabLintResponse.valid = true
            showGitlabTokenNotification = true
            setMergedPreview(pass, gitlabLintResponse.mergedYaml)
            return Pair(gitlabLintResponse, LintStatusEnum.VALID)
        }

        val fallbackBranch = pass.project.service<ProjectSettings>().fallbackBranch
        if (gitlabLintResponse?.errors?.firstOrNull()
                .equals(GitlabLintErrorsEnum.REFERENCE_NOT_FOUND.message, true) && fallbackBranch.isNotBlank()
        ) {
            // Try to lint with fallback branch
            gitlabLintResponse = lintContent(gitlab, gitlabUrl, gitlabToken, pass, remoteId, fallbackBranch)
            if (gitlabLintResponse?.valid == true) {
                showGitlabTokenNotification = true
                setMergedPreview(pass, gitlabLintResponse.mergedYaml)
                return Pair(gitlabLintResponse, LintStatusEnum.VALID)
            }
        }

        // Show error
        showGitlabTokenNotification = false
        setMergedPreview(pass, gitlabLintResponse?.mergedYaml)
        return Pair(gitlabLintResponse, LintStatusEnum.INVALID)
    }

    private fun setMergedPreview(
        pass: Pass, mergedYaml: String?
    ) {
        WriteCommandAction.runWriteCommandAction(pass.project) {
            val selectedEditor = FileEditorManager.getInstance(pass.project).getSelectedEditor(pass.file.virtualFile)
            if (selectedEditor is EditorWithMergedPreview) {
                mergedYaml?.let { selectedEditor.setPreviewText(it) }
            }
        }
    }

    private fun lintContent(
        gitlab: Gitlab,
        gitlabUrl: String,
        gitlabToken: String,
        pass: Pass,
        remoteId: Long,
        branch: String
    ): GitlabLintResponse? {
        return gitlab.lintContent(
            gitlabUrl,
            gitlabToken,
            pass.file.text,
            remoteId,
            branch,
            showGitlabTokenNotification
        ).get()
    }
}
