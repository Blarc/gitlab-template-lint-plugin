package com.github.blarc.gitlab.template.lint.plugin.settings

import com.intellij.openapi.options.Configurable
import org.jetbrains.annotations.Nls
import org.jetbrains.annotations.Nullable
import javax.swing.JComponent

class AppSettingsConfigurable : Configurable {
    private var mySettingsComponent: AppSettingsComponent? = null

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP
    @Nls(capitalization = Nls.Capitalization.Title)
    override fun getDisplayName(): String {
        return "Gitlab Template Lint"
    }

    override fun getPreferredFocusedComponent(): JComponent {
        return mySettingsComponent!!.preferredFocusedComponent
    }

    @Nullable
    override fun createComponent(): JComponent {
        mySettingsComponent = AppSettingsComponent()
        return mySettingsComponent!!.panel
    }

    override fun isModified(): Boolean {
        val settings: AppSettingsState = AppSettingsState.instance
        return !mySettingsComponent!!.gitlabToken.contentEquals(settings.gitlabToken?.toCharArray())
    }

    override fun apply() {
        val settings: AppSettingsState = AppSettingsState.instance
        settings.gitlabToken = String(mySettingsComponent!!.gitlabToken!!)
    }

    override fun reset() {
        val settings: AppSettingsState = AppSettingsState.instance
        mySettingsComponent!!.gitlabToken = settings.gitlabToken?.toCharArray()
    }

    override fun disposeUIResources() {
        mySettingsComponent = null
    }
}
