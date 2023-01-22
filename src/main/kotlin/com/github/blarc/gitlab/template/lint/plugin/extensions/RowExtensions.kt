package com.github.blarc.gitlab.template.lint.plugin.extensions

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle
import com.github.blarc.gitlab.template.lint.plugin.GitlabLintBundle.message
import com.intellij.ui.components.BrowserLink
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row


fun Row.reportBugLink(): Cell<BrowserLink> {
    return browserLink(message("settings.report-bug"), GitlabLintBundle.URL_BUG_REPORT.toString())
}