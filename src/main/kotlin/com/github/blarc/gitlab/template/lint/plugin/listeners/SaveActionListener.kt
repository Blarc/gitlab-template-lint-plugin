package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.lintGitlabYaml
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.lintFrequency.LintFrequencyEnum
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.DumbService
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager

class SaveActionListener(val project: Project) : FileDocumentManagerListener {

    override fun beforeDocumentSaving(document: Document) {
        DumbService.getInstance(project).runWhenSmart {
            val file = PsiDocumentManager.getInstance(project).getPsiFile(document)
            if (file != null && AppSettings.instance.lintFrequency == LintFrequencyEnum.ON_SAVE) {
                lintGitlabYaml(file)
            }
            super.beforeDocumentSaving(document)
        }
    }
}
