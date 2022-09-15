package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import javax.swing.table.AbstractTableModel

class RemotesTableModel(): AbstractTableModel() {
    var remotesList: MutableList<Remote> = mutableListOf()

    private val columnNames = arrayOf(
        "remoteUrl",
        "gitlabUrl",
        "gitlabProjectId"
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
    override fun getColumnName(columnIndex: Int): String {
        when (columnIndex) {
            0 -> return "Remote URL"
            1 -> return "Gitlab URL"
            2 -> return "Remote ID"
        }
        return "Unknown column"
    }
}
