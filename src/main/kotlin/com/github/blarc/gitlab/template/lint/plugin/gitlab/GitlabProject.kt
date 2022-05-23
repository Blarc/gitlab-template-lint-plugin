package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitlabProject(
    val id: Long,
    @SerialName("web_url")
    val webUrl: String
)
