package com.github.blarc.gitlab.template.lint.plugin.gitlab

enum class GitlabLintErrorsEnum(val message: String) {
    REFERENCE_NOT_FOUND("Reference not found"),
    PIPELINE_FILTERED_OUT("Pipeline filtered out by workflow rules.")
}