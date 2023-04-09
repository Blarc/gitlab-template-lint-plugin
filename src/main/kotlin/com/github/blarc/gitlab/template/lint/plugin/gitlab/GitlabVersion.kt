package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class GitlabVersion (
    val version: String,
    val revision: String
): GitlabObject
