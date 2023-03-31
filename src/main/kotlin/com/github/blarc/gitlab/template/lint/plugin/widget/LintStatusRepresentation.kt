package com.github.blarc.gitlab.template.lint.plugin.widget

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.github.blarc.gitlab.template.lint.plugin.widget.actions.OpenSettingsAction
import com.github.blarc.gitlab.template.lint.plugin.widget.actions.RefreshAction
import com.intellij.dvcs.ui.LightActionGroup
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.util.Conditions
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.WindowManager
import com.intellij.ui.popup.PopupFactoryImpl.ActionGroupPopup
import javax.swing.Icon

class LintStatusPresentation(val project: Project) : StatusBarWidget.MultipleTextValuesPresentation {

    override fun getPopup(): ListPopup {
        val statusBar = WindowManager.getInstance().getStatusBar(project)
        return ActionGroupPopup(
            GitlabLintBundle.message("lint.status.popup.title"),
            createActions(),
            DataManager.getInstance().getDataContext(statusBar.component),
            false,
            false,
            true,
            false,
            null,
            -1,
            Conditions.alwaysTrue(),
            null
        )
    }

    private fun createActions(): ActionGroup {
        val actionGroup = LightActionGroup()
        actionGroup.add(OpenSettingsAction())
        actionGroup.add(RefreshAction())
        return actionGroup
    }

    override fun getTooltipText(): String? {
        val status = project.service<Pipeline>().lintStatus
        return status.tooltip
    }


    override fun getSelectedValue(): String? {
        val status = project.service<Pipeline>().lintStatus
        return status.text
    }

    override fun getIcon(): Icon? {
        val status = project.service<Pipeline>().lintStatus
        return status.icon
    }

}

