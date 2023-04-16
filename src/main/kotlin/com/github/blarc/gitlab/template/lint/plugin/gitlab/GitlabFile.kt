package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class GitlabFile(
    @SerialName("file_name")
    val fileName: String,
    @SerialName("content")
    var content: String
) : GitlabObject