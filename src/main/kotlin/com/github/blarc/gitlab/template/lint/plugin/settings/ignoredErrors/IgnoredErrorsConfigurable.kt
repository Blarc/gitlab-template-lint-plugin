package com.github.blarc.gitlab.template.lint.plugin.settings.ignoredErrors

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.extensions.createColumn
import com.github.blarc.gitlab.template.lint.plugin.extensions.reportBugLink
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.Align
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.table.TableView
import com.intellij.util.ui.ListTableModel
import javax.swing.ListSelectionModel

class IgnoredErrorsConfigurable(val project: Project) : BoundConfigurable(message("settings.ignored-errors.group.title")) {
    private var ignoredErrors = projectsIgnoredErrors()

    private val tableModel = createTableModel()

    private val table = TableView(tableModel).apply {
        setShowColumns(true)
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
    }
    override fun createPanel() = panel {
        row {
            cell(
                ToolbarDecorator.createDecorator(table)
                    .setRemoveAction { removeIgnoredError() }
                    .disableAddAction()
                    .disableUpAction()
                    .disableDownAction()
                    .createPanel()
            ).align(Align.FILL)
        }.resizableRow()
        row {
            reportBugLink()
        }
    }

    private fun createTableModel(): ListTableModel<IgnoredError> = ListTableModel(
        arrayOf(
            createColumn<IgnoredError>(message("settings.ignored-error.file")) { ignoredError -> ignoredError.filePath.removePrefix("${project.basePath ?: ""}/") },
            createColumn(message("settings.ignored-error.message")) { ignoredError -> ignoredError.errorMessage }
        ),
        ignoredErrors
    )

    private fun removeIgnoredError() {
        val selectedRow = table.selectedObject ?: return

        ignoredErrors = ignoredErrors.minus(selectedRow)
        refreshTableModel()
    }

    private fun refreshTableModel() {
        tableModel.items = ignoredErrors
    }

    override fun reset() {
        super.reset()
        ignoredErrors = projectsIgnoredErrors()
        refreshTableModel()
    }

    override fun isModified(): Boolean {
        return ignoredErrors != projectsIgnoredErrors()
    }

    override fun apply() {
        super.apply()
        project.service<ProjectSettings>().ignoredErrors = ignoredErrors.groupBy {
            it.filePath
        }.mapValues {
            it.value.map { ignoredError -> ignoredError.errorMessage }.toMutableSet()
        }.toMutableMap()
    }

    private fun projectsIgnoredErrors() = project.service<ProjectSettings>().ignoredErrors.map { mapEntry ->
        mapEntry.value.map {
            IgnoredError(mapEntry.key, it)
        }
    }.flatten()
}