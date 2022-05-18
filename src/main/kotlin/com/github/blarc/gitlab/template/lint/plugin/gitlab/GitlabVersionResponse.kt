package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.Serializable

@Serializable
data class GitlabVersionResponse (
    val version: String,
    val revision: String
)
