package com.github.blarc.gitlab.template.lint.plugin.providers

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.Companion.matchesGitlabLintRegex
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications

class LintNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>() {

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("LintNotificationProvider")
    }

    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {

        val pipeline = project.service<Pipeline>()

        if (pipeline.gitlabLintResponse?.valid == false && matchesGitlabLintRegex(file.name)) {
            val panel = EditorNotificationPanel(HintUtil.ERROR_COLOR_KEY)
            panel.text = pipeline.gitlabLintResponse?.errors.toString()
//            TODO @Blarc: Implement error ignoring.
//            panel.createActionLabel("Ignore this error.") {
//                EditorNotifications.getInstance(project).updateAllNotifications()
//            }

            return panel
        }

        return null
    }
}
