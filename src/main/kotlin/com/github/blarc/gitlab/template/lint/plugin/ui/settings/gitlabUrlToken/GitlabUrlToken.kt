package com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class GitlabUrlToken(
  var gitlabUrl: String = "",
  var gitlabToken: String? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as GitlabUrlToken
        return gitlabUrl == other.gitlabUrl
    }

    override fun hashCode(): Int {
        return gitlabUrl.hashCode()
    }
}
