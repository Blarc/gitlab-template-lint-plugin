package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.Serializable

@Serializable
data class GitlabVersion (
    val version: String,
    val revision: String
)
