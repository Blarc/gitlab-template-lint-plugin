package com.github.blarc.gitlab.template.lint.plugin.providers

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.pipeline.LintPipeline
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
            val lintPipeline = project.service<LintPipeline>()

            if (lintPipeline.gitlabLint?.valid == false && GitlabLintUtils.isGitlabYaml(file)) {
                val panel = EditorNotificationPanel(HintUtil.ERROR_COLOR_KEY)
                panel.text = lintPipeline.gitlabLint?.errors.toString()
                return@Function panel
            }

            return@Function null
        }

}
