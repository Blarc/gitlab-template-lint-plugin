package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.extensions.createColumn
import com.github.blarc.gitlab.template.lint.plugin.extensions.replaceAt
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.builder.text
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import javax.swing.ListSelectionModel.SINGLE_SELECTION

class GlobsConfigurable : BoundConfigurable(message("settings.globs.group.title")) {
    private var globs = AppSettings.instance.gitlabLintGlobStrings

    private val tableModel = createTableModel()

    private val table = TableView(tableModel).apply {
        setShowColumns(false)
        setSelectionMode(SINGLE_SELECTION)
    }

    override fun createPanel() = panel {
        row {
            cell(
                ToolbarDecorator.createDecorator(table)
                    .setAddAction{ addGlob() }
                    .setEditAction{ editGlob() }
                    .setRemoveAction{ removeGlob() }
                    .disableUpAction()
                    .disableDownAction()
                    .createPanel()
            ).align(Align.FILL)
        }.resizableRow()
    }


    private fun createTableModel(): ListTableModel<String> = ListTableModel(
        arrayOf(
            createColumn<String>("Glob") { glob -> glob }
        ),
        globs
    )

    private fun addGlob() {
        val dialog = GlobDialog()

        if (dialog.showAndGet()) {
            globs = globs.plus(dialog.glob)
            refreshTableModel()
        }
    }

    private fun removeGlob() {
        val row = table.selectedObject ?: return

        globs = globs.minus(row)
        refreshTableModel()
    }

    private fun editGlob() {
        val row = table.selectedObject ?: return

        val dialog = GlobDialog(row)
        if (dialog.showAndGet()) {
            globs = if (dialog.glob.isEmpty()) {
                globs.minus(row)
            } else {
                globs.replaceAt(table.selectedRow, dialog.glob)
            }
            refreshTableModel()
        }
    }

    private fun refreshTableModel() {
        tableModel.items = globs
    }

    override fun reset() {
        super.reset()

        globs = AppSettings.instance.gitlabLintGlobStrings
        refreshTableModel()
    }

    override fun isModified(): Boolean {
        return super.isModified() || globs != AppSettings.instance.gitlabLintGlobStrings
    }

    override fun apply() {
        super.apply()
        AppSettings.instance.gitlabLintGlobStrings = globs
    }
}

private class GlobDialog(var glob: String = "") : DialogWrapper(false) {
    init {
        title = message("settings.glob.dialog.title")

        if (glob.isEmpty()) {
            setOKButtonText(message("actions.add"))
        } else {
            setOKButtonText(message("actions.update"))
        }
        init()
    }

    override fun createCenterPanel() = panel {
        row {
            label(message("settings.glob.dialog.label"))
            textField()
                .text(glob)
                .bindText({glob}, {glob = it})
                .focused()
        }
    }

}