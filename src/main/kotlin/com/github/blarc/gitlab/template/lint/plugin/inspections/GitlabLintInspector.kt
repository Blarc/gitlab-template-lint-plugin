package com.github.blarc.gitlab.template.lint.plugin.inspections

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintRunner
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.Companion.matchesGitlabLintRegex
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotifications

class GitlabLintInspector : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                runBackgroundableTask("Gitlab Lint", file.project) {
                    it.isIndeterminate = true
                    val project = file.project
                    if (matchesGitlabLintRegex(file.name)) {
                        val gitlabToken = AppSettingsState.instance?.gitlabToken
                        if (gitlabToken != null && gitlabToken.isNotEmpty()) {
                            val linter = project.service<GitlabLintRunner>()
                            linter.run(file.text, file.virtualFile.path)
                        }
                    }
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }
            }
        }
    }
}
