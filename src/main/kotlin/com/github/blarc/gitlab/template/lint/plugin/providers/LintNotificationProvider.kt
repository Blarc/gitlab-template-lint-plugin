package com.github.blarc.gitlab.template.lint.plugin.providers

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
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
        project: Project,
        file: VirtualFile
    ) = Function { _: FileEditor ->
            val pipeline = project.service<Pipeline>()

            if (pipeline.gitlabLintResponse?.valid == false && GitlabLintUtils.isGitlabYaml(file)) {
                val panel = EditorNotificationPanel(HintUtil.ERROR_COLOR_KEY)

                if (pipeline.gitlabLintResponse?.errors?.get(0).equals("Reference not found", true)) {
                    panel.text = GitlabLintBundle.message("lint.error.reference-not-found")
                }
                else {
                    panel.text = pipeline.gitlabLintResponse?.errors.toString()
                }
                return@Function panel
            }

            return@Function null
        }

}
