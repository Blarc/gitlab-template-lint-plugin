package com.github.blarc.gitlab.template.lint.plugin.widgets

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

class LintStatusWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = LintStatusWidget.ID

    override fun getDisplayName(): String = "Lint Status"

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget = LintStatusWidget(project)

    override fun disposeWidget(widget: StatusBarWidget) {}

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}
