package com.github.blarc.gitlab.template.lint.plugin.inspections

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintRunner
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.Companion.matchesGitlabLintRegex
import com.github.blarc.gitlab.template.lint.plugin.widgets.LintStatusEnum
import com.intellij.codeInsight.hint.HintUtil
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Key
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.ui.EditorNotificationPanel
import com.intellij.ui.EditorNotifications

class GitlabLintNotificationProvider : EditorNotifications.Provider<EditorNotificationPanel>() {

    companion object {
        private val KEY = Key.create<EditorNotificationPanel>("GitlabLintNotificationProvider")
    }

    override fun getKey(): Key<EditorNotificationPanel> = KEY

    override fun createNotificationPanel(
        file: VirtualFile,
        fileEditor: FileEditor,
        project: Project
    ): EditorNotificationPanel? {

        val gitlabRunner = project.service<GitlabLintRunner>()

        if (gitlabRunner.status == LintStatusEnum.INVALID && matchesGitlabLintRegex(file.name)) {
            val panel = EditorNotificationPanel(HintUtil.ERROR_COLOR_KEY)
            panel.text = gitlabRunner.gitlabLintResponse?.errors.toString()
//            TODO @Blarc: Implement error ignoring.
//            panel.createActionLabel("Ignore this error.") {
//                EditorNotifications.getInstance(project).updateAllNotifications()
//            }

            return panel
        }
        return null
    }
}
