package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken.GitlabUrlToken
import com.intellij.openapi.components.service
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nullable
import javax.swing.JComponent

class SettingsConfigurable(val project: Project) : Configurable {
    private var settingsForm: SettingsForm? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return message("settings.general.group.title")
    }

    @Nullable
    override fun createComponent(): JComponent {
        settingsForm = SettingsForm(project)
        return settingsForm!!.basePanel!!
    }


    override fun isModified(): Boolean {
        val appSettings = AppSettings.instance
        val projectSettings = project.service<ProjectSettings>()

        if (appSettings != null) {
            return settingsForm!!.gitlabRemotesTable.isModified(appSettings) ||
                    settingsForm!!.gitlabUrlTokenTable.isModified(projectSettings) ||
                    !settingsForm!!.gitlabRemoteTF.contentEquals(projectSettings.remote) ||
                    settingsForm!!.forceHttpsCB != projectSettings.forceHttps ||
                    settingsForm!!.lintFrequency != appSettings.lintFrequency ||
                    settingsForm!!.showMergedPreviewCB != appSettings.showMergedPreview ||
                    settingsForm!!.runLintOnFileChangeCB != appSettings.runLintOnFileChange
        }
        return false
    }

    override fun apply() {
        val appSettings = AppSettings.instance
        val projectSettings = project.service<ProjectSettings>()

        if (appSettings != null) {
            settingsForm!!.gitlabRemotesTable.commit(appSettings)
            settingsForm!!.gitlabUrlTokenTable.commit(appSettings)
            projectSettings.remote = settingsForm!!.gitlabRemoteTF!!
            projectSettings.forceHttps = settingsForm!!.forceHttpsCB
            appSettings.lintFrequency = settingsForm!!.lintFrequency
            appSettings.showMergedPreview = settingsForm!!.showMergedPreviewCB
            appSettings.runLintOnFileChange = settingsForm!!.runLintOnFileChangeCB
        }
    }

    override fun reset() {
        val appSettings = AppSettings.instance
        val projectSettings = project.service<ProjectSettings>()
        val gitlabUrl = projectSettings.gitlabUrl

        if (appSettings != null && gitlabUrl != null) {
            settingsForm!!.gitlabRemotesTable.tableModel.remotesList = appSettings.remotes.values.toMutableList()
            settingsForm!!.gitlabUrlTokenTable.tableModel.gitlabUrlTokenList = projectSettings.gitlabUrls
                .map { GitlabUrlToken(it, appSettings.getGitlabToken(it)) }.toMutableList()
            settingsForm!!.gitlabRemoteTF = projectSettings.remote
            settingsForm!!.forceHttpsCB = projectSettings.forceHttps
            settingsForm!!.lintFrequency = appSettings.lintFrequency
            settingsForm!!.showMergedPreviewCB = appSettings.showMergedPreview
            settingsForm!!.runLintOnFileChangeCB = appSettings.runLintOnFileChange
        }
    }

    override fun disposeUIResources() {
        settingsForm = null
    }
}
