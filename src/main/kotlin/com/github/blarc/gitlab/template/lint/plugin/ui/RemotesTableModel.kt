package com.github.blarc.gitlab.template.lint.plugin.ui

import javax.swing.table.AbstractTableModel

class RemotesTableModel(var remotesList: MutableList<Pair<String, Long?>>): AbstractTableModel() {

    private val columnNames = arrayOf(
        "remoteUrl",
        "gitlabProjectId"
    )

    private val columnClasses = arrayOf(
        String::class.java,
        Long::class.java
    )

    override fun getRowCount() = remotesList.size

    override fun getColumnCount() = columnNames.size

    override fun getValueAt(row: Int, column: Int): Any? {
        return when(column) {
            0 -> {
                remotesList[row].first
            }
            1 -> {
                remotesList[row].second
            }
            else -> throw IllegalArgumentException("Unknown column name: $column")
        }
    }
    override fun getColumnName(columnIndex: Int): String {
        when (columnIndex) {
            0 -> return "Remote URL"
            1 -> return "Remote ID"
        }
        return "Unknown column"
    }
}
