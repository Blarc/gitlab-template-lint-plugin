package com.github.blarc.gitlab.template.lint.plugin.settings

import com.github.blarc.gitlab.template.lint.plugin.gui.AppSettingsForm
import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nullable
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {
    private var appSettingsForm: AppSettingsForm? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Gitlab Template Lint"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return appSettingsForm!!.preferredFocusedComponent!!
    }

    @Nullable
    override fun createComponent(): JComponent {
        appSettingsForm = AppSettingsForm()
        return appSettingsForm!!.basePanel!!
    }

    override fun isModified(): Boolean {
        val settings = AppSettingsState.instance
        if (settings != null) {
            return !appSettingsForm!!.gitlabToken.contentEquals(settings.gitlabToken?.toCharArray()) || appSettingsForm!!.gitlabRemotesTable.isModified(settings)
        }
        return false
    }

    override fun apply() {
        val settings = AppSettingsState.instance
        if (settings != null) {
            settings.gitlabToken = String(appSettingsForm!!.gitlabToken!!)
            appSettingsForm!!.gitlabRemotesTable.commit(settings)
        }
    }

    override fun reset() {
        val settings = AppSettingsState.instance
        if (settings != null) {
            appSettingsForm!!.gitlabToken = settings.gitlabToken?.toCharArray()
            appSettingsForm!!.gitlabRemotesTable.tableModel.remotesList = settings.remotesMap.toList().toMutableList()
        }
    }

    override fun disposeUIResources() {
        appSettingsForm = null
    }
}
