package com.github.blarc.gitlab.template.lint.plugin.settings

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
        val instance: AppSettings?
            get() = ApplicationManager.getApplication().getService(AppSettings::class.java)
    }

    var gitlabLintRegexString: String? = ".*gitlab-ci\\.(yaml|yml)$"
    var lastVersion: String? = null
    var hits = 0
    var requestSupport = true;

    var gitlabToken: String?
        set(value) {
            PasswordSafe.instance.setPassword(getCredentialAttributes(), value.toString())
        }
        get() {
            val credentialAttributes = getCredentialAttributes()
            val credentials: Credentials = PasswordSafe.instance.get(credentialAttributes) ?: return null
            return credentials.getPasswordAsString()
        }

    var remotesMap: MutableMap<String, Long?> = mutableMapOf()

    private fun getCredentialAttributes(): CredentialAttributes {
        return CredentialAttributes(
            SERVICE_NAME,
            "gitlabToken",
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