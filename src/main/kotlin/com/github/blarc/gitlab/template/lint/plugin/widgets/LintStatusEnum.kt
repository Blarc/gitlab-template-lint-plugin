package com.github.blarc.gitlab.template.lint.plugin.widgets

enum class LintStatusEnum(val text: String, val tooltip: String) {
    INVALID("Invalid", "Gitlab CI yaml is invalid!"),
    VALID("Valid", "Gitlab CI yaml is valid."),
    RUNNING("Running", "Gitlab CI linter is running."),
    WAITING("Waiting", "Gitlab CI linter is waiting.")
}
