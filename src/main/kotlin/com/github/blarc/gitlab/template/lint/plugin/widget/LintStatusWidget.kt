package com.github.blarc.gitlab.template.lint.plugin.widget

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.Disposer
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.WindowManager

class LintStatusWidget(val project: Project) : StatusBarWidget {

    private var statusBar = WindowManager.getInstance().getStatusBar(project)

    companion object {
        const val ID = "LintStatus"
    }

    override fun dispose() {
        Disposer.dispose(this)
    }

    override fun ID(): String = ID

    override fun install(statusBar: StatusBar) {
        statusBar.updateWidget(ID)
    }

    override fun getPresentation(): StatusBarWidget.WidgetPresentation {
        return LintStatusPresentation(statusBar)
    }
}
