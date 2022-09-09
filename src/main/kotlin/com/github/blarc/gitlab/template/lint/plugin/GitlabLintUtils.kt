package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings

object GitlabLintUtils {
    private fun getGitlabLintRegex(): Regex {
        val appSettings = AppSettings.instance
        if (appSettings?.gitlabLintRegexString != null) {
            return Regex(appSettings.gitlabLintRegexString!!)
        }
        return Regex(".*gitlab-ci\\.(yaml|yml)$")
    }

    fun matchesGitlabLintRegex(text: String): Boolean {
        return getGitlabLintRegex().matches(text)
    }
}
