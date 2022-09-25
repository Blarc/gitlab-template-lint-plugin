package com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken;

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.project.Project
import com.intellij.ui.table.JBTable
import javax.swing.ListSelectionModel

class GitlabUrlTokenTable(val project: Project, val tableModel: GitlabUrlTokenTableModel) : JBTable()
{
    init {
        model = tableModel
        columnSelectionAllowed = false
        rowHeight = 22
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
        setDefaultRenderer(String::class.java, GitlabTokenRenderer())
    }

    private fun isValidRow(selectedRow: Int) = selectedRow >= 0 && selectedRow < tableModel.gitlabUrlTokenList.size

    fun removeSelectedGitlabUrlTokens() {
        if (selectedRows.isEmpty()) {
            return
        }

        selectedRows.forEach {
            if (isValidRow(it)) {
                tableModel.gitlabUrlTokenList.removeAt(it)
            }
        }

        tableModel.fireTableDataChanged()
    }

    fun commit (settings: AppSettings) {
        tableModel.gitlabUrlTokenList.forEach {
            it.gitlabToken?.let { token -> settings.saveGitlabToken(token, it.gitlabUrl) }
        }
    }

    fun addGitlabUrlToken() {
        val gitlabUrlTokenEditor = GitlabUrlTokenEditor(project,"Add Gitlab URL and Token", null)
        if (gitlabUrlTokenEditor.showAndGet()) {
            tableModel.gitlabUrlTokenList.add(GitlabUrlToken(gitlabUrlTokenEditor.getGitlabUrl()!!, gitlabUrlTokenEditor.getGitlabToken()!!))
            tableModel.fireTableDataChanged()
        }
    }

    fun editGitlabUrlToken() : Boolean {
        if (selectedRowCount != 1) {
            return false
        }

        val selectedGitlabUrlToken = tableModel.gitlabUrlTokenList[selectedRow]
        val gitlabUrlTokenEditor = GitlabUrlTokenEditor(project,"Edit Gitlab URL and Token", selectedGitlabUrlToken)
        if (gitlabUrlTokenEditor.showAndGet()) {
            tableModel.gitlabUrlTokenList[selectedRow] = GitlabUrlToken(gitlabUrlTokenEditor.getGitlabUrl()!!, gitlabUrlTokenEditor.getGitlabToken()!!)
            tableModel.fireTableDataChanged()
        }

        return true
    }

    fun isModified(projectSettings: ProjectSettings): Boolean {
        return tableModel.gitlabUrlTokenList.map { it.gitlabUrl } != projectSettings.gitlabUrls
    }


}
