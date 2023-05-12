package com.github.blarc.gitlab.template.lint.plugin.providers

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.gitlab.GitlabLintErrorsEnum
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotificationProvider
import java.util.function.Function

class LintNotificationProvider : EditorNotificationProvider {

    override fun collectNotificationData(
        project: Project, file: VirtualFile
    ) = Function { _: FileEditor ->
        val pipeline = project.service<Pipeline>()

        if (pipeline.gitlabLintResponse?.valid == false && GitlabLintUtils.isGitlabYaml(file)) {
            val panel = EditorNotificationPanel(HintUtil.ERROR_COLOR_KEY)

            val errors = pipeline.gitlabLintResponse?.errors
            if (errors?.firstOrNull().equals("Reference not found", true)) {
                panel.text = message("lint.error.reference-not-found")
            } else {
                panel.text =
                    errors?.joinToString(separator = " ") ?: "Lint response was invalid, but no errors were returned."
            }

            panel.text = when (errors?.firstOrNull()) {
                GitlabLintErrorsEnum.REFERENCE_NOT_FOUND.message -> message("lint.error.reference-not-found")
                GitlabLintErrorsEnum.PIPELINE_FILTERED_OUT.message -> {
                    panel.createActionLabel(message("lint.error.explanation")) {
                        GitlabLintBundle.openPipelineFilteredIssue()
                    }
                    message("lint.error.pipeline-filtered")
                }
                else -> errors?.joinToString(separator = " ")
                    ?: "Lint response was invalid, but no errors were returned."
            }

            panel.createActionLabel(message("lint.error.ignore")) {
                project.service<ProjectSettings>().ignoredErrors.getOrPut(file.path) { mutableSetOf() }
                    .add(errors?.get(0) ?: "")
                panel.isVisible = false
            }

            return@Function panel
        }
        return@Function null
    }
}
