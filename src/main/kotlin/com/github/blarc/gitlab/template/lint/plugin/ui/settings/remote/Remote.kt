package com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Remote(
    var remoteUrl: String = "",
    var gitlabUrl: String? = null,
    var remoteId: Long? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Remote
        return remoteUrl == other.remoteUrl
    }

    override fun hashCode(): Int {
        return remoteUrl.hashCode()
    }
}
