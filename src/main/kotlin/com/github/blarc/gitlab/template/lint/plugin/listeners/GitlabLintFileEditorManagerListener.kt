package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils.Companion.matchesGitlabLintRegex
import com.github.blarc.gitlab.template.lint.plugin.widgets.LintStatusWidgetFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetSettings
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import com.intellij.ui.EditorNotifications
import org.jetbrains.kotlin.idea.util.application.getService

@Service
class GitlabLintFileEditorManagerListener : FileEditorManagerListener {

    private val lintStatusWidgetFactory = ApplicationManager.getApplication().getService<LintStatusWidgetFactory>()
    private val statusBarWidgetSettings = ApplicationManager.getApplication().getService<StatusBarWidgetSettings>()

    var matches: Boolean = false
    override fun selectionChanged(event: FileEditorManagerEvent) {

        val project = event.manager.project

        // Check if file matches regex
        if (event.newFile != null) {
            matches = matchesGitlabLintRegex(event.newFile.name)
        }

        // Hide/show error notification
        EditorNotifications.getInstance(project).updateAllNotifications()

        // Hide/show widget
        val statusBarWidgetsManager = project.getService<StatusBarWidgetsManager>()

        if (lintStatusWidgetFactory != null) {
            statusBarWidgetSettings?.setEnabled(lintStatusWidgetFactory, matches)
            statusBarWidgetsManager?.updateWidget(LintStatusWidgetFactory::class.java)
        }
    }
}
