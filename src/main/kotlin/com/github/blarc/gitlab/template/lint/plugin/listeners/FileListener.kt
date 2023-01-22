package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.github.blarc.gitlab.template.lint.plugin.runLinting
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusWidgetFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetSettings
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import com.intellij.psi.PsiManager
import com.intellij.ui.EditorNotifications

@Service
class FileListener : FileEditorManagerListener {

    private var firstTime = true

    private val lintStatusWidgetFactory = ApplicationManager.getApplication().getService(LintStatusWidgetFactory::class.java)
    private val statusBarWidgetSettings = ApplicationManager.getApplication().getService(StatusBarWidgetSettings::class.java)
    override fun selectionChanged(event: FileEditorManagerEvent) {

        val project = event.manager.project

        // Check if file matches regex
        val matches = if (event.newFile != null) {
            GitlabLintUtils.matchesGitlabLintGlob(event.newFile.path)
        } else {
            false
        }

        // Hide/show error notification
        EditorNotifications.getInstance(project).updateAllNotifications()

        // Hide/show widget
        val statusBarWidgetsManager = project.getService(StatusBarWidgetsManager::class.java)
        if (lintStatusWidgetFactory != null) {

            statusBarWidgetSettings?.setEnabled(lintStatusWidgetFactory, matches)
            statusBarWidgetsManager?.updateWidget(LintStatusWidgetFactory::class.java)

            if (!firstTime && AppSettings.instance.runLintOnFileChange && matches) {
                // If we can get the psi file, run linting
                PsiManager.getInstance(project).findFile(event.newFile)?.let {
                    runLinting(it)
                }
            }
        }

        firstTime = false
    }
}
