package com.github.blarc.gitlab.template.lint.plugin.widgets

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message

enum class LintStatusEnum(val text: String, val tooltip: String) {
    INVALID(message("lint.status.invalid"), message("lint.status.invalid.tooltip")),
    VALID(message("lint.status.valid"), message("lint.status.valid.tooltip")),
    RUNNING(message("lint.status.running"), message("lint.status.running.tooltip")),
    WAITING(message("lint.status.waiting"), message("lint.status.waiting.tooltip")),
    INVALID_ID(message("lint.status.invalid-id"), message("lint.status.invalid-id.tooltip"))
}
