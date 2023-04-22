package com.github.blarc.gitlab.template.lint.plugin.gitlab

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
data class GitlabFile(
    @SerialName("file_name")
    val fileName: String,
    @SerialName("file_path")
    val filePath: String,
    val encoding: String,
    var content: String,
    @SerialName("content_sha256")
    val sha: String,
    val ref: String,
    @SerialName("blob_id")
    val blobId: String,
    @SerialName("commit_id")
    val commitId: String,
    var properties: Map<String, String>? = null
) : GitlabObject