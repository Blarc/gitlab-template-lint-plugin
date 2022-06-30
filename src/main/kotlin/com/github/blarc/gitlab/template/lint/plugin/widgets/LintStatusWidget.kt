package com.github.blarc.gitlab.template.lint.plugin.widgets

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.WindowManager

class LintStatusWidget(project: Project) : StatusBarWidget {

    private var statusBar = WindowManager.getInstance().getStatusBar(project)

    companion object {
        const val ID = "LintStatus"
    }

    override fun ID(): String = ID

    override fun install(statusBar: StatusBar) {
        statusBar.updateWidget(ID)
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return LintStatusPresentation(statusBar)
    }

    override fun dispose() {
        println("Hello")
    }

}
