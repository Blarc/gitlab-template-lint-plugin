package com.github.blarc.gitlab.template.lint.plugin.settings.ignoredErrors

import kotlinx.serialization.Serializable

@Suppress("PROVIDED_RUNTIME_TOO_LOW")
@Serializable
class IgnoredError(
    var filePath: String,
    var errorMessage: String
)