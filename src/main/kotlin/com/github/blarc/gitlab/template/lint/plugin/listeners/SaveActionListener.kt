package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.runLinting
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.LintFrequencyEnum
import com.intellij.openapi.editor.Document
import com.intellij.openapi.fileEditor.FileDocumentManagerListener
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager

class SaveActionListener(val project: Project) : FileDocumentManagerListener {

    override fun beforeDocumentSaving(document: Document) {
        val file = PsiDocumentManager.getInstance(project).getPsiFile(document)
        if (file != null && AppSettings.instance.lintFrequency == LintFrequencyEnum.ON_SAVE) {
            runLinting(file)
        }
        super.beforeDocumentSaving(document)
    }
}