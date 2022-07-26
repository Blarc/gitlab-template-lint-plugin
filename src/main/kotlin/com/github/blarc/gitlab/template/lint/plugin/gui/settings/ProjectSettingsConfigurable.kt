package com.github.blarc.gitlab.template.lint.plugin.gui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.intellij.openapi.components.service
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.project.Project
import com.intellij.ui.layout.panel

class ProjectSettingsConfigurable(project : Project) : BoundConfigurable(message("settings.general.group.title")) {
    private val settings = project.service<ProjectSettings>()


    override fun createPanel() = panel {
        row(message("settings.general.field.fallback-branch.label")) {
            textField(settings::fallbackBranch)
                .comment(message("settings.general.field.fallback-branch.help"))
//                .withValidationOnApply { notBlank(it.text) }
        }
        row(message("settings.general.field.remote.label")) {
            textField(settings::remote)
//                .withValidationOnApply { notBlank(it.text) }
        }
        titledRow(message("settings.general.section.advanced.label")) {
            row(message("settings.general.field.force-https.label")) {
                checkBox(message("settings.general.field.force-https.label"), settings::forceHttps)
            }.largeGapAfter()

            row(message("settings.general.field.check-commit-on-remote.label")) {
                checkBox(
                    message("settings.general.field.check-commit-on-remote.label"),
                    settings::checkCommitOnRemote,
                    comment = message("settings.general.field.check-commit-on-remote.help")
                )
            }
        }
    }
}
