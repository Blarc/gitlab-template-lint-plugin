package com.github.blarc.gitlab.template.lint.plugin.widget

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.github.blarc.gitlab.template.lint.plugin.widget.actions.OpenSettingsAction
import com.github.blarc.gitlab.template.lint.plugin.widget.actions.RefreshAction
import com.intellij.dvcs.ui.LightActionGroup
import com.intellij.icons.AllIcons
import com.intellij.ide.DataManager
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.util.Conditions
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.ui.popup.PopupFactoryImpl.ActionGroupPopup
import javax.swing.Icon

class LintStatusPresentation(private val statusBar: StatusBar) : StatusBarWidget.MultipleTextValuesPresentation {
    override fun getTooltipText(): String {
        val status = statusBar.project?.service<Pipeline>()?.lintStatus ?: return LintStatusEnum.WAITING.tooltip
        return status.tooltip
    }

    override fun getPopup(): ListPopup {
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

    override fun getSelectedValue(): String {
        val status = statusBar.project?.service<Pipeline>()?.lintStatus ?: return LintStatusEnum.WAITING.text
        return status.text
    }


    override fun getIcon(): Icon {
        return when (statusBar.project?.service<Pipeline>()?.lintStatus) {
            LintStatusEnum.INVALID -> AllIcons.General.ExclMark
            LintStatusEnum.INVALID_ID -> AllIcons.General.ExclMark
            LintStatusEnum.VALID -> AllIcons.General.InspectionsOK
            LintStatusEnum.RUNNING -> AllIcons.General.InlineRefreshHover
            else -> {
                AllIcons.General.InspectionsPause
            }
        }
    }

    private fun createActions(): ActionGroup {
        val actionGroup = LightActionGroup()
        actionGroup.add(OpenSettingsAction())
        actionGroup.add(RefreshAction())
        return actionGroup
    }

}

