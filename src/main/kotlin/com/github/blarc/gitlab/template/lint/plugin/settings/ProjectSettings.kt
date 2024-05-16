package com.github.blarc.gitlab.template.lint.plugin.settings

import com.github.blarc.gitlab.template.lint.plugin.settings.gitlabUrlToken.GitlabUrlToken
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import com.intellij.util.xmlb.annotations.Transient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.annotations.NotNull

/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@Service(Service.Level.PROJECT)
@State(
    name = ProjectSettings.SERVICE_NAME,
    storages = [Storage("GitlabLint.xml")]
)
class ProjectSettings : PersistentStateComponent<ProjectSettings?> {

    companion object {
        const val SERVICE_NAME = "com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings"
    }

    var newProject: Boolean = true
    var gitlabUrls: Set<String> = setOf()
    var fallbackBranch: String = ""
    var remote = "origin"
    var forceHttps = true
    var ignoredErrors: MutableMap<String, MutableSet<String>> = mutableMapOf()

    @Transient
    @get:Transient
    var gitlabUrlTokens: List<GitlabUrlToken> = listOf()

    override fun getState() = this

    override fun loadState(@NotNull state: ProjectSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }

    override fun initializeComponent() {
        CoroutineScope(Dispatchers.IO).launch {
            gitlabUrlTokens =
                gitlabUrls.map { GitlabUrlToken(it, AppSettings.instance.getGitlabToken(it)) }
        }
    }
}
