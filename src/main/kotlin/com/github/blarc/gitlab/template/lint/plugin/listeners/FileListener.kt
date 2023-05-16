package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.updateStatusWidget
import com.github.blarc.gitlab.template.lint.plugin.lintGitlabYaml
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusWidget
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.startup.StartupManager
import com.intellij.openapi.wm.WindowManager
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotifications

@Service
class FileListener : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {

        val file = event.newFile ?: return
        val project = event.manager.project

        // Hide/show error notification
        EditorNotifications.getInstance(project).updateAllNotifications()

        // Hide widget if file doesn't match glob
        if (!GitlabLintUtils.isGitlabYaml(file)) {
            updateStatusWidget(project, LintStatusEnum.HIDDEN)
            return
        }

        // Run linting if startup is finished and linting is enabled
        val startupFinished = StartupManager.getInstance(project).postStartupActivityPassed()
        if (startupFinished && AppSettings.instance.runLintOnFileChange) {
            WindowManager.getInstance().getStatusBar(project).updateWidget(LintStatusWidget.ID)
            // If we can get the psi file, run linting
            PsiManager.getInstance(project).findFile(file)?.let {
                lintGitlabYaml(it)
            }
        } else {
            updateStatusWidget(project, LintStatusEnum.WAITING)
        }
    }
}
