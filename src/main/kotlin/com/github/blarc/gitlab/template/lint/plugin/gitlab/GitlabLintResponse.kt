package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.Serializable

@Serializable
data class GitlabLintResponse(
    val valid: Boolean,
    val errors: List<String>,
    val warnings: List<String>
)
