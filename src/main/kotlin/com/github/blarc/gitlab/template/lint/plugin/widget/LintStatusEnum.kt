package com.github.blarc.gitlab.template.lint.plugin.widget

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.intellij.icons.AllIcons
import javax.swing.Icon

enum class LintStatusEnum(val text: String?, val tooltip: String?, val icon: Icon?) {
    INVALID(message("lint.status.invalid"), message("lint.status.invalid.tooltip"), AllIcons.General.ExclMark),
    INVALID_ID(message("lint.status.invalid-id"), message("lint.status.invalid-id.tooltip"), AllIcons.General.ExclMark),
    VALID(message("lint.status.valid"), message("lint.status.valid.tooltip"), AllIcons.General.InspectionsOK),
    RUNNING(message("lint.status.running"), message("lint.status.running.tooltip"), AllIcons.General.InlineRefreshHover),
    WAITING(message("lint.status.waiting"), message("lint.status.waiting.tooltip"), AllIcons.General.InspectionsPause),
    HIDDEN(null, null, null)
}
