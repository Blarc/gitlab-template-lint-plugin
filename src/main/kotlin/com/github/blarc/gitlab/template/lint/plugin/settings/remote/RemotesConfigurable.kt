package com.github.blarc.gitlab.template.lint.plugin.settings.remote

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.extensions.*
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import javax.swing.ListSelectionModel.SINGLE_SELECTION

class RemotesConfigurable(project: Project) : BoundConfigurable(message("settings.remotes.group.title")) {
    private var remotes = AppSettings.instance.remotes.values.toList()
    private val gitlabUrls = project.service<ProjectSettings>().gitlabUrls


    private val tableModel = createTableModel()

    private val table = TableView(tableModel).apply {
        setShowColumns(true)
        setSelectionMode(SINGLE_SELECTION)
    }

    override fun createPanel() = panel {
        row {
            cell(
                ToolbarDecorator.createDecorator(table)
                    .setAddAction { addRemote() }
                    .setEditAction { editRemote() }
                    .setRemoveAction { removeRemote() }
                    .disableUpAction()
                    .disableDownAction()
                    .createPanel()
            ).align(Align.FILL)
        }.resizableRow()
        row {
            reportBugLink()
        }
    }

    private fun createTableModel(): ListTableModel<Remote> = ListTableModel(
        arrayOf(
            createColumn<Remote>(message("settings.remote.remote-url")) { remote -> remote.remoteUrl },
            createColumn(message("settings.remote.gitlab-api-url")) { remote -> remote.gitlabUrl.orEmpty() },
            createColumn(message("settings.remote.project-id")) { remote -> remote.remoteId?.toString().orEmpty() }
        ),
        remotes
    )

    private fun addRemote() {
        val dialog = RemoteDialog(gitlabUrls)

        if (dialog.showAndGet()) {
            remotes = remotes.plus(dialog.remote)
            refreshTableModel()
        }
    }

    private fun removeRemote() {
        val row = table.selectedObject ?: return

        remotes = remotes.minus(row)
        refreshTableModel()
    }

    private fun editRemote() {
        val row = table.selectedObject ?: return

        val dialog = RemoteDialog(gitlabUrls, row.copy())

        if (dialog.showAndGet()) {
            remotes = remotes.replaceAt(table.selectedRow, dialog.remote)
            refreshTableModel()
        }
    }

    private fun refreshTableModel() {
        tableModel.items = remotes
    }


    override fun reset() {
        super.reset()
        remotes = AppSettings.instance.remotes.values.toList()
        refreshTableModel()
    }

    override fun isModified(): Boolean {
        return remotes != AppSettings.instance.remotes.values.toList()
    }

    override fun apply() {
        super.apply()
        AppSettings.instance.remotes = remotes.associateBy { it.remoteUrl }.toMutableMap()
    }

    private class RemoteDialog(val gitlabUrls: Set<String>, newRemote: Remote? = null) : DialogWrapper(false) {
        val remote = newRemote ?: Remote()

        init {
            title = message("settings.remotes.dialog.title")
            setOKButtonText(newRemote?.let { message("actions.update") } ?: message("actions.add"))
            setSize(700, 200)
            init()
        }

        override fun createCenterPanel() = panel {
            row(message("settings.remote.remote-url")) {
                textField()
                    .align(Align.FILL)
                    .bindText(remote::remoteUrl)
                    .focused()
                    .validationOnApply { notBlank(it.text) }
                    .resizableColumn()
                    .emptyText(message("settings.remote.remote-url.empty-text"))

                contextHelp(message("settings.remote.remote-url.context-help"))
                    .align(AlignX.RIGHT)
            }
            row(message("settings.remote.gitlab-api-url")) {
                comboBox(gitlabUrls)
                    .align(Align.FILL)
                    .bindItem(remote::gitlabUrl.toNullableProperty())
                    .resizableColumn()

                contextHelp(message("settings.remote.gitlab-api-url.context-help"))
                    .align(AlignX.RIGHT)
            }
            row(message("settings.remote.project-id")) {
                textField()
                    .align(Align.FILL)
                    .bindText({ remote.remoteId?.toString().orEmpty() }, { remote.remoteId = it.toLong() })
                    .validationOnApply { isLong(it.text) }
                    .resizableColumn()
                    .emptyText(message("settings.remote.project-id.empty-text"))

                contextHelp(message("settings.remote.project-id.context-help"))
                    .align(AlignX.RIGHT)
            }
        }
    }
}