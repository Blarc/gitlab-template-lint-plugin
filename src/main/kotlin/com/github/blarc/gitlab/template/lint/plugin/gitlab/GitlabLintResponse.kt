package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class GitlabLintResponse(
    var valid: Boolean,
    var errors: MutableList<String>,
    val warnings: List<String>,
    @SerialName("merged_yaml")
    val mergedYaml: String?
)
