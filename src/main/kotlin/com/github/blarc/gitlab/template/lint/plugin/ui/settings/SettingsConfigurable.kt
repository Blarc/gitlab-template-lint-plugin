package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.extensions.notBlank
import com.github.blarc.gitlab.template.lint.plugin.extensions.reportBugLink
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken.GitlabUrlTokenTable
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.*

class SettingsConfigurable(val project: Project) : BoundConfigurable(message("settings.general.group.title")) {

    private val gitlabUrlTokenTable = GitlabUrlTokenTable(project)
    override fun createPanel() = panel {
        row {
            comboBox(LintFrequencyEnum.values().toList(), LintFrequencyRenderer())
                .label(message("settings.frequency.label"))
                .bindItem(AppSettings.instance::lintFrequency.toNullableProperty())
        }
        row {
            comment(message("settings.frequency.description"))
        }
        row {
            textField()
                .label(message("settings.remote"))
                .bindText(project.service<ProjectSettings>()::remote)
                .validationOnApply { notBlank(it.text) }
                .align(Align.FILL)
        }
        row {
            checkBox(message("settings.force-https"))
                .bindSelected(project.service<ProjectSettings>()::forceHttps)
        }
        row {
            checkBox(message("settings.show-merged-preview"))
                .bindSelected(AppSettings.instance::showMergedPreview)
                .comment(message("settings.show-merged-preview.comment"))
        }
        row {
            checkBox(message("settings.run-lint-on-file-change"))
                .bindSelected(AppSettings.instance::runLintOnFileChange)
                .comment(message("settings.run-lint-on-file-change.comment"))
        }
        row {
            cell(
                ToolbarDecorator.createDecorator(gitlabUrlTokenTable.table)
                    .setAddAction { gitlabUrlTokenTable.addGitlabUrlToken() }
                    .setEditAction { gitlabUrlTokenTable.editGitlabUrlToken() }
                    .setRemoveAction { gitlabUrlTokenTable.removeGitlabUrlToken() }
                    .disableUpAction()
                    .disableDownAction()
                    .createPanel()
            ).align(Align.FILL)
        }.resizableRow()
        row {
            reportBugLink()
        }
    }

    override fun isModified(): Boolean {
        return super.isModified() || gitlabUrlTokenTable.isModified()
    }

    override fun apply() {
        gitlabUrlTokenTable.apply()
        super.apply()
    }

    override fun reset() {
        gitlabUrlTokenTable.reset()
        super.reset()
    }
}