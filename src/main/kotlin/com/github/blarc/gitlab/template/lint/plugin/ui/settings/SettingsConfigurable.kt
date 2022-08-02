package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.ui.SettingsForm
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

    override fun getPreferredFocusedComponent(): JComponent {
        return settingsForm!!.preferredFocusedComponent!!
    }

    @Nullable
    override fun createComponent(): JComponent {
        settingsForm = SettingsForm(project)
        return settingsForm!!.basePanel!!
    }


    override fun isModified(): Boolean {
        val settings = AppSettings.instance
        val gitlabUrl = settingsForm?.gitlabUrlTF
        if (settings != null && gitlabUrl != null) {
            return !settingsForm!!.gitlabTokenTF.contentEquals(settings.getGitlabToken(gitlabUrl)) || settingsForm!!.gitlabRemotesTable.isModified(settings)
        }
        return false
    }

    override fun apply() {
        val settings = AppSettings.instance
        val gitlabUrl = settingsForm?.gitlabUrlTF
        if (settings != null && gitlabUrl != null) {
            settings.saveGitlabToken(settingsForm!!.gitlabTokenTF!!, gitlabUrl)
            settingsForm!!.gitlabRemotesTable.commit(settings)
        }
    }

    override fun reset() {
        val settings = AppSettings.instance
        val gitlabUrl = settingsForm?.gitlabUrlTF
        if (settings != null && gitlabUrl != null) {
            settingsForm!!.gitlabTokenTF = settings.getGitlabToken(gitlabUrl)
            settingsForm!!.gitlabRemotesTable.tableModel.remotesList = settings.remotesMap.toList().toMutableList()
        }
    }

    override fun disposeUIResources() {
        settingsForm = null
    }
}
