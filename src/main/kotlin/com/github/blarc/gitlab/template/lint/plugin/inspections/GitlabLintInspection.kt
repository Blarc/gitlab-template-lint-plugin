package com.github.blarc.gitlab.template.lint.plugin.inspections

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.lintGitlabYaml
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.lintFrequency.LintFrequencyEnum
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile

class GitlabLintInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return object : PsiElementVisitor() {
            override fun visitFile(file: PsiFile) {
                if (AppSettings.instance.lintFrequency == LintFrequencyEnum.ON_CHANGE) {
                    lintGitlabYaml(file)
                }
                else {
                    GitlabLintUtils.updateStatusWidget(file.project, LintStatusEnum.WAITING)
                }
            }
        }
    }
}
