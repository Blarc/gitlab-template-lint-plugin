package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class GitlabLintResponse(
    val valid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)
