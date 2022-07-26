package com.github.blarc.gitlab.template.lint.plugin.inspections

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintResponse
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.Companion.matchesGitlabLintRegex
import com.github.blarc.gitlab.template.lint.plugin.runLinting
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.Service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.ui.EditorNotifications

@Service
class GitlabLintInspector : LocalInspectionTool() {

    var gitlabLintResponse: GitlabLintResponse? = null

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                runBackgroundableTask("Gitlab Lint", file.project) { indicator ->
                    indicator.isIndeterminate = true
                    val project = file.project
                    if (matchesGitlabLintRegex(file.name)) {
                        val gitlabToken = AppSettingsState.instance?.gitlabToken
                        if (!gitlabToken.isNullOrEmpty()) {
                            runLinting(project, file) {
                                gitlabLintResponse = it
                            }
                        }
                    }
                    EditorNotifications.getInstance(project).updateAllNotifications()
                }
            }
        }
    }
}
