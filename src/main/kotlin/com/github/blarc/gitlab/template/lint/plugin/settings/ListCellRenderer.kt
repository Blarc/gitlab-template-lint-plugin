package com.github.blarc.gitlab.template.lint.plugin.settings

import com.github.blarc.gitlab.template.lint.plugin.settings.lintFrequency.LintFrequencyEnum
import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList

class ListCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(list: JList<*>?, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component {
        val component = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus)
        if (value is LintFrequencyEnum) {
            text = value.message
        }
        return component
    }
}