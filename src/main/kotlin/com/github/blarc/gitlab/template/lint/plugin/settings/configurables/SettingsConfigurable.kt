package com.github.blarc.gitlab.template.lint.plugin.settings.configurables

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.extensions.emptyText
import com.github.blarc.gitlab.template.lint.plugin.extensions.notBlank
import com.github.blarc.gitlab.template.lint.plugin.extensions.reportBugLink
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ListCellRenderer
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.gitlabUrlToken.GitlabUrlTokenTable
import com.github.blarc.gitlab.template.lint.plugin.settings.lintFrequency.LintFrequencyEnum
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.ToolbarDecorator
import com.intellij.ui.dsl.builder.*

class SettingsConfigurable(val project: Project) :
    BoundConfigurable(GitlabLintBundle.message("settings.general.group.title")) {

    private val gitlabUrlTokenTable = GitlabUrlTokenTable(project)
    override fun createPanel() = panel {
        row {
            comboBox(LintFrequencyEnum.entries, ListCellRenderer())
                .label(GitlabLintBundle.message("settings.frequency.label"))
                .bindItem(AppSettings.instance::lintFrequency.toNullableProperty())

            contextHelp(GitlabLintBundle.message("settings.frequency.description"))
        }
        row {
            checkBox(GitlabLintBundle.message("settings.allow-self-signed-certificate"))
                .bindSelected(AppSettings.instance::allowSelfSignedCertificate)
        }
        row {
            checkBox(GitlabLintBundle.message("settings.force-https"))
                .bindSelected(project.service<ProjectSettings>()::forceHttps)
        }
        row {
            checkBox(GitlabLintBundle.message("settings.show-merged-preview"))
                .bindSelected(AppSettings.instance::showMergedPreview)
                .comment(GitlabLintBundle.message("settings.show-merged-preview.comment"))
        }
        row {
            checkBox(GitlabLintBundle.message("settings.run-lint-on-file-change"))
                .bindSelected(AppSettings.instance::runLintOnFileChange)
                .comment(GitlabLintBundle.message("settings.run-lint-on-file-change.comment"))
        }
        row {
            label(GitlabLintBundle.message("settings.remote"))
                .widthGroup("label")
            textField()
                .bindText(project.service<ProjectSettings>()::remote)
                .validationOnApply { notBlank(it.text) }
                .align(Align.FILL)
                .resizableColumn()
                .emptyText(GitlabLintBundle.message("settings.remote.empty-text"))

            contextHelp(GitlabLintBundle.message("settings.remote.comment"))
        }
        row {
            label(GitlabLintBundle.message("settings.fallback-branch"))
                .widthGroup("label")
            textField()
                .bindText(project.service<ProjectSettings>()::fallbackBranch)
                .align(Align.FILL)
                .resizableColumn()
                .emptyText(GitlabLintBundle.message("settings.fallback-branch.empty-text"))

            contextHelp(GitlabLintBundle.message("settings.fallback-branch.comment"))
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
