package com.github.blarc.gitlab.template.lint.plugin.ui.settings.gitlabUrlToken

import com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote.Remote
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class GitlabUrlToken(
  val gitlabUrl: String,
  var gitlabToken: String?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Remote
        return gitlabUrl == other.gitlabUrl
    }

    override fun hashCode(): Int {
        return gitlabUrl.hashCode()
    }
}
