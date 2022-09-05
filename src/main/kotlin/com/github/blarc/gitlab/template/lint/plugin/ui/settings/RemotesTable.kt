package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.ui.table.JBTable
import javax.swing.ListSelectionModel

class RemotesTable(val tableModel: RemotesTableModel) : JBTable() {

    init {
        model = tableModel
        columnSelectionAllowed = false
        rowHeight = 22
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }

    private fun isValidRow(selectedRow: Int) = selectedRow >= 0 && selectedRow < tableModel.remotesList.size

    fun removeSelectedRemotes() {
        if (selectedRows.isEmpty()) {
            return
        }

        selectedRows.forEach {
            if (isValidRow(it)) {
                tableModel.remotesList.removeAt(it)
            }
        }

        tableModel.fireTableDataChanged()
    }

    fun commit(settings: AppSettings) {
        settings.remotesMap = tableModel.remotesList.toMap().toMutableMap()
    }
    fun addRemote() {
        val remoteEditor = RemoteEditor("Add remote", null, null)
        if (remoteEditor.showAndGet()) {
            tableModel.remotesList.add(Pair(remoteEditor.getRemoteUrl()!!, remoteEditor.getRemoteId()!!))
            tableModel.fireTableDataChanged()
        }
    }

    fun editRemote() : Boolean {
        if (selectedRowCount != 1) {
            return false
        }

        val selectedRemote = tableModel.remotesList[selectedRow]
        val remoteEditor = RemoteEditor("Edit remote", selectedRemote.first, selectedRemote.second)
        if (remoteEditor.showAndGet()) {
            tableModel.remotesList[selectedRow] = Pair(remoteEditor.getRemoteUrl()!!, remoteEditor.getRemoteId()!!)
            tableModel.fireTableDataChanged()
        }

        return true
    }

    fun isModified(settings: AppSettings): Boolean {
        return tableModel.remotesList != settings.remotesMap.toList()
    }
}
