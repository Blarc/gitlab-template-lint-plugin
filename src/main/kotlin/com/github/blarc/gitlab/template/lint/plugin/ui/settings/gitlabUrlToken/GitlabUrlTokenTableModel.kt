package com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken;

import javax.swing.table.AbstractTableModel

class GitlabUrlTokenTableModel(): AbstractTableModel()
{
    var gitlabUrlTokenList: MutableList<GitlabUrlToken> = mutableListOf()

    private val columnNames = arrayOf(
        "Gitlab URL",
        "Gitlab Token"
    )

    private val columnClasses = arrayOf(
        String::class.java,
        String::class.java
    )

    override fun getRowCount() = gitlabUrlTokenList.size

    override fun getColumnCount() = columnNames.size

    override fun getValueAt(row: Int, column: Int): Any? {
        return when(column) {
            0 -> {
                gitlabUrlTokenList[row].gitlabUrl
            }
            1 -> {
                gitlabUrlTokenList[row].gitlabToken
            }
            else -> throw IllegalArgumentException("Unknown column name: $column")
        }
    }

    override fun getColumnName(column: Int): String {
        if (column < columnNames.size) {
            return columnNames[column]
        }
        throw RuntimeException("Unknown column")
    }

    override fun getColumnClass(columnIndex: Int): Class<*> {
        if (columnIndex < columnClasses.size) {
            return columnClasses[columnIndex]
        }
        throw RuntimeException("Unknown column")
    }
}
