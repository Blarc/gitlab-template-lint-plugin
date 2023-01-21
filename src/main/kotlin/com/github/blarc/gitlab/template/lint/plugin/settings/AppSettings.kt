package com.github.blarc.gitlab.template.lint.plugin.settings

import com.github.blarc.gitlab.template.lint.plugin.ui.settings.LintFrequencyEnum
import com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote.Remote
import com.intellij.credentialStore.CredentialAttributes
import com.intellij.credentialStore.Credentials
import com.intellij.ide.passwordSafe.PasswordSafe
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable


/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = AppSettings.SERVICE_NAME,
    storages = [Storage("GitlabLint.xml")]
)
class AppSettings : PersistentStateComponent<AppSettings> {
    companion object {
        const val SERVICE_NAME = "com.github.blarc.gitlab.template.lint.plugin.settings.AppSettingsState"
        val instance: AppSettings
            get() = ApplicationManager.getApplication().getService(AppSettings::class.java)
    }

    var gitlabLintGlobStrings: List<String> = listOf("**/*.gitlab-ci.yml", "**/*.gitlab-ci.yaml")
    var lastVersion: String? = null
    var hits = 0
    var requestSupport = true
    var remotes: MutableMap<String, Remote> = mutableMapOf()
    var lintFrequency: LintFrequencyEnum = LintFrequencyEnum.ON_SAVE
    var showMergedPreview = true
    var runLintOnFileChange = false

    fun saveGitlabToken(token: String, gitlabUrl: String) {
        PasswordSafe.instance.setPassword(getCredentialAttributes(gitlabUrl), token)
    }

    fun getGitlabToken(gitlabUrl: String): String? {
        val credentialAttributes = getCredentialAttributes(gitlabUrl)
        val credentials: Credentials = PasswordSafe.instance.get(credentialAttributes) ?: return null
        return credentials.getPasswordAsString()
    }

    private fun getCredentialAttributes(title: String): CredentialAttributes {
        return CredentialAttributes(
            title,
            null,
            this.javaClass,
            false
        )
    }

    @Nullable
    override fun getState() = this


    override fun loadState(@NotNull state: AppSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    fun recordHit() {
        hits++
    }
}
