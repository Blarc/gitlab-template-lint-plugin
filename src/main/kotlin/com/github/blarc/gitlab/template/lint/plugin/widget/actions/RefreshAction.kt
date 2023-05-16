package com.github.blarc.gitlab.template.lint.plugin.widget.actions

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.lintGitlabYaml
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.psi.PsiManager

class RefreshAction : DumbAwareAction(message("actions.refresh")) {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            FileEditorManager.getInstance(project).selectedEditor?.file?.let {
                PsiManager.getInstance(project).findFile(it)?.let { psiFile ->
                    lintGitlabYaml(psiFile)
                }
            }
        }
    }

}
