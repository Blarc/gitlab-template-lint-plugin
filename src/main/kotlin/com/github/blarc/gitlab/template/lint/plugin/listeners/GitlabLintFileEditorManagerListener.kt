package com.github.blarc.gitlab.template.lint.plugin.listeners

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState
import com.github.blarc.gitlab.template.lint.plugin.widgets.LintStatusWidgetFactory
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.fileEditor.FileEditorManagerEvent
import com.intellij.openapi.fileEditor.FileEditorManagerListener
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetSettings
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import org.jetbrains.kotlin.idea.util.application.getService

@Service
class GitlabLintFileEditorManagerListener : FileEditorManagerListener {

    override fun selectionChanged(event: FileEditorManagerEvent) {

        val project = event.manager.project

        var regex = ".*gitlab-ci\\.(yaml|yml)$"
        val appSettingsState = AppSettingsState.instance
        if (appSettingsState?.gitlabLintRegexString != null) {
            regex = appSettingsState.gitlabLintRegexString!!
        }

        var matches = false
        if (event.newFile != null) {
            matches = Regex(regex).matches(event.newFile.name)
        }

        val lintStatusWidgetFactory = ApplicationManager.getApplication().getService<LintStatusWidgetFactory>()
        val statusBarWidgetSettings = ApplicationManager.getApplication().getService<StatusBarWidgetSettings>()
        val statusBarWidgetsManager = project.getService<StatusBarWidgetsManager>()

        if (lintStatusWidgetFactory != null) {
            statusBarWidgetSettings?.setEnabled(lintStatusWidgetFactory, matches)
            statusBarWidgetsManager?.updateWidget(LintStatusWidgetFactory::class.java)
        }
    }
}
