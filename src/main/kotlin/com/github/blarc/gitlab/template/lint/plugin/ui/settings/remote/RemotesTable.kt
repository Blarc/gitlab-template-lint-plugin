package com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.project.Project
import com.intellij.ui.table.JBTable
import javax.swing.ListSelectionModel

class RemotesTable(val project: Project, val tableModel: RemotesTableModel) : JBTable()
{
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
        settings.remotes = tableModel.remotesList.associateBy { it.remoteUrl }.toMutableMap()
    }
    fun addRemote() {
        val remoteEditor = RemoteEditor(project, "Add remote", null)
        if (remoteEditor.showAndGet()) {
            // TODO @Blarc: Input validation.
            tableModel.remotesList.add(Remote(remoteEditor.getRemoteUrl()!!, remoteEditor.getGitlabUrl(), remoteEditor.getRemoteId()))
            tableModel.fireTableDataChanged()
        }
    }

    fun editRemote() : Boolean {
        if (selectedRowCount != 1) {
            return false
        }

        val selectedRemote = tableModel.remotesList[selectedRow]
        val remoteEditor = RemoteEditor(project, "Edit remote", selectedRemote)
        if (remoteEditor.showAndGet()) {
            // TODO @Blarc: Input validation.
            tableModel.remotesList[selectedRow] = Remote(remoteEditor.getRemoteUrl()!!, remoteEditor.getGitlabUrl(), remoteEditor.getRemoteId())
            tableModel.fireTableDataChanged()
        }

        return true
    }

    fun isModified(appSettings: AppSettings): Boolean {
        return tableModel.remotesList != appSettings.remotes.toList()
    }
}
