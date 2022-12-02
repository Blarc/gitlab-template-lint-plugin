package com.github.blarc.gitlab.template.lint.plugin.ui.settings

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message

enum class LintFrequencyEnum(val message: String) {
    ON_CHANGE(message("settings.frequency.on-change")),
    ON_SAVE(message("settings.frequency.on-save")),
    MANUAL(message("settings.frequency.manual"))
}