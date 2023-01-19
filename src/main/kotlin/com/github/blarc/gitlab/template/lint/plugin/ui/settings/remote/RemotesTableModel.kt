package com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote

import javax.swing.table.AbstractTableModel

class RemotesTableModel : AbstractTableModel() {
    var remotesList: MutableList<Remote> = mutableListOf()

    private val columnNames = arrayOf(
        "Remote URL",
        "Gitlab URL",
        "Remote ID"
    )

    override fun getRowCount() = remotesList.size

    override fun getColumnCount() = columnNames.size

    override fun getValueAt(row: Int, column: Int): Any? {
        return when(column) {
            0 -> {
                remotesList[row].remoteUrl
            }
            1 -> {
                remotesList[row].gitlabUrl
            }
            2 -> {
                remotesList[row].remoteId
            }
            else -> throw IllegalArgumentException("Unknown column name: $column")
        }
    }
    override fun getColumnName(column: Int): String {
        if (column < columnNames.size) {
            return columnNames[column]
        }
        return "Unknown column"
    }
}
