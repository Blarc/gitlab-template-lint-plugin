package com.github.blarc.gitlab.template.lint.plugin.widget

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.intellij.openapi.components.Service
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory

@Service
class LintStatusWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): String = LintStatusWidget.ID

    override fun getDisplayName(): String = message("widgets.lint.status.display.name")

    override fun isAvailable(project: Project): Boolean = true

    override fun createWidget(project: Project): StatusBarWidget = LintStatusWidget(project)

    override fun disposeWidget(widget: StatusBarWidget) {
        Disposer.dispose(widget)
    }

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}
