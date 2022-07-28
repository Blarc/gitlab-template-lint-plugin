package com.github.blarc.gitlab.template.lint.plugin.widgets

import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.util.Consumer
import org.jetbrains.annotations.Nullable
import java.awt.event.MouseEvent
import javax.swing.Icon

class LintStatusPresentation(private val statusBar: StatusBar) : StatusBarWidget.MultipleTextValuesPresentation {
    override fun getTooltipText(): String {
        val status = statusBar.project?.service<Pipeline>()?.lintStatus ?: return LintStatusEnum.WAITING.tooltip
        return status.tooltip
    }

    override fun getClickConsumer(): Consumer<MouseEvent>? {
        return null
    }

    @Nullable("Null means the widget is unable to show the popup.")
    override fun getPopupStep(): ListPopup? {
        return null
    }

    override fun getSelectedValue(): String {
        val status = statusBar.project?.service<Pipeline>()?.lintStatus ?: return LintStatusEnum.WAITING.text
        return status.text
    }


    override fun getIcon(): Icon {
        return when (statusBar.project?.service<Pipeline>()?.lintStatus) {
            LintStatusEnum.INVALID -> AllIcons.General.ExclMark
            LintStatusEnum.VALID -> AllIcons.General.InspectionsOK
            LintStatusEnum.RUNNING -> AllIcons.General.InlineRefreshHover
            else -> {
                AllIcons.General.InspectionsPause
            }
        }
    }

}

