package com.github.blarc.gitlab.template.lint.plugin.inspections

import com.github.blarc.gitlab.template.lint.plugin.runLinting
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.LintFrequencyEnum
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.components.Service
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

@Service
class GitlabLintInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (AppSettings.instance.lintFrequency == LintFrequencyEnum.ON_CHANGE) {
                    runLinting(file)
                }
            }
        }
    }
}
