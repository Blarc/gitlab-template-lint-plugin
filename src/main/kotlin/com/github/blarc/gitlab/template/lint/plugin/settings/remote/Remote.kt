package com.github.blarc.gitlab.template.lint.plugin.settings.remote

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class Remote(
    var remoteUrl: String = "",
    var gitlabUrl: String? = null,
    var remoteId: Long? = null
)