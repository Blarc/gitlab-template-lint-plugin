package com.github.blarc.gitlab.template.lint.plugin.ui.settings.remote

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Remote(
    val remoteUrl: String,
    var gitlabUrl: String?,
    var remoteId: Long?
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
