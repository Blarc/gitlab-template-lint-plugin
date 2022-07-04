package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.service
import com.intellij.openapi.progress.runBackgroundableTask
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

class GitlabLintInspector : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (Regex(".*gitlab-ci\\.(yaml|yml)$").matches(file.name)) {
                    val gitlabToken = AppSettingsState.instance.gitlabToken
                    if (gitlabToken != null && gitlabToken.isNotEmpty()) {
                        val linter = file.project.service<GitlabLintRunner>()
                        runBackgroundableTask("Linting Gitlab CI", file.project) {
                            it.isIndeterminate = true
                            val gitlabLintResponse = linter.run(file.text, file.virtualFile.path)

                            if (gitlabLintResponse != null && !gitlabLintResponse.valid) {
                                holder.registerProblem(
                                    file, gitlabLintResponse.errors.toString(), ProblemHighlightType.ERROR
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
