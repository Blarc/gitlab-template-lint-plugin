package com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken;

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.extensions.createColumn
import com.github.blarc.gitlab.template.lint.plugin.extensions.notBlank
import com.github.blarc.gitlab.template.lint.plugin.extensions.replaceAt
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import javax.swing.ListSelectionModel.SINGLE_SELECTION

class GitlabUrlTokenTable(val project: Project) {
    private var gitlabUrls = project.service<ProjectSettings>().gitlabUrls
    private var gitlabUrlTokens = gitlabUrls
        .map { GitlabUrlToken(it, AppSettings.instance.getGitlabToken(it)) }

    private val tableModel = createTableModel()

    val table = TableView(tableModel).apply {
        setShowColumns(true)
        setSelectionMode(SINGLE_SELECTION)
        setDefaultRenderer(String::class.java, GitlabTokenRenderer(1))
    }

    private fun createTableModel(): ListTableModel<GitlabUrlToken> = ListTableModel(
        arrayOf(
            createColumn<GitlabUrlToken>(message("settings.gitlabUrlToken.gitlab-url")) { gitlabUrlToken -> gitlabUrlToken.gitlabUrl },
            createColumn(message("settings.gitlabUrlToken.gitlab-token")) { gitlabUrlToken -> gitlabUrlToken.gitlabToken.orEmpty() }
        ),
        gitlabUrlTokens
    )

    fun addGitlabUrlToken() {
        val dialog = GitlabUrlTokenDialog()

        if (dialog.showAndGet()) {
            gitlabUrlTokens = gitlabUrlTokens.plus(dialog.gitlabUrlToken)
            refreshTableModel()
        }
    }

    fun removeGitlabUrlToken() {
        val row = table.selectedObject ?: return
        gitlabUrlTokens = gitlabUrlTokens.minus(row)
        gitlabUrls = gitlabUrls.minus(row.gitlabUrl)
        refreshTableModel()
    }

    fun editGitlabUrlToken() {
        val row = table.selectedObject ?: return

        val dialog = GitlabUrlTokenDialog(row.copy())

        if (dialog.showAndGet()) {
            gitlabUrlTokens = gitlabUrlTokens.replaceAt(table.selectedRow, dialog.gitlabUrlToken)
            refreshTableModel()
        }
    }

    private fun refreshTableModel() {
        tableModel.items = gitlabUrlTokens
    }

    fun reset() {
        gitlabUrlTokens =
            gitlabUrlTokens.map { GitlabUrlToken(it.gitlabUrl, AppSettings.instance.getGitlabToken(it.gitlabUrl)) }
        refreshTableModel()
    }

    fun isModified(): Boolean {
        val orgGitlabUrls = project.service<ProjectSettings>().gitlabUrls
        return orgGitlabUrls.size != gitlabUrlTokens.size ||
                gitlabUrlTokens.any {
                    !orgGitlabUrls.contains(it.gitlabUrl) ||
                            AppSettings.instance.getGitlabToken(it.gitlabUrl) != it.gitlabToken
                }
    }

    fun apply() {
        gitlabUrlTokens.forEach { AppSettings.instance.saveGitlabToken(it.gitlabToken!!, it.gitlabUrl) }
        project.service<ProjectSettings>().gitlabUrls = gitlabUrlTokens.map { it.gitlabUrl }.toSet()
    }

    private class GitlabUrlTokenDialog(newGitlabUrlToken: GitlabUrlToken? = null) : DialogWrapper(true) {
        var gitlabUrlToken = newGitlabUrlToken ?: GitlabUrlToken()

        init {
            title = message("settings.gitlabUrlToken.dialog")
            setOKButtonText(newGitlabUrlToken?.let { message("actions.update") } ?: message("actions.add"))
            setSize(700, 200)
            init()
        }

        override fun createCenterPanel() = panel {
            row(message("settings.gitlabUrlToken.gitlab-url")) {
                textField()
                    .align(Align.FILL)
                    .bindText({ gitlabUrlToken.gitlabUrl }, { gitlabUrlToken.gitlabUrl = it })
                    .focused()
                    .validationOnApply { notBlank(it.text) }

            }
            row(message("settings.gitlabUrlToken.gitlab-token")) {
                textField()
                    .align(Align.FILL)
                    .bindText({ gitlabUrlToken.gitlabToken.orEmpty() }, { gitlabUrlToken.gitlabToken = it })
                    .validationOnApply { notBlank(it.text) }

            }
        }
    }
}
