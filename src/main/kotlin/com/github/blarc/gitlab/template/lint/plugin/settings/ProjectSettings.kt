package com.github.blarc.gitlab.template.lint.plugin.settings

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil
import org.jetbrains.annotations.NotNull

/**
 * Supports storing the application settings in a persistent way.
 * The [State] and [Storage] annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
    name = ProjectSettings.SERVICE_NAME,
    storages = [Storage("GitlabLint.xml")]
)
class ProjectSettings : PersistentStateComponent<ProjectSettings?> {

    companion object {
        const val SERVICE_NAME = "com.github.blarc.gitlab.template.lint.plugin.settings.ProjectSettings"
    }

    var gitlabUrl: String? = null
    var gitlabUrls: Set<String> = setOf()
    var fallbackBranch = "main"
    var remote = "origin"
    var forceHttps = true

    override fun getState() = this

    override fun loadState(@NotNull state: ProjectSettings) {
        XmlSerializerUtil.copyBean(state, this)
    }
}
