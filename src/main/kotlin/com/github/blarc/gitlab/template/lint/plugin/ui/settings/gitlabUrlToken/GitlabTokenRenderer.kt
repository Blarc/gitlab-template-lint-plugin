package com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken

import java.awt.Component
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer


class GitlabTokenRenderer(private val columnIndex: Int) : DefaultTableCellRenderer() {

    companion object {
        private const val MAX_LENGTH = 25
    }

    override fun getTableCellRendererComponent(
        table: JTable?,
        value: Any?,
        isSelected: Boolean,
        hasFocus: Boolean,
        row: Int,
        column: Int
    ): Component {
        if (column == columnIndex && value is String? && !value.isNullOrBlank()) {
            var maskedValue = ""
            for (index in 0 until value.toString().length + 1) {
                maskedValue += "•"
                if (index > MAX_LENGTH) {
                    break
                }
            }
            return super.getTableCellRendererComponent(table, maskedValue, isSelected, hasFocus, row, column)
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
    }
}
