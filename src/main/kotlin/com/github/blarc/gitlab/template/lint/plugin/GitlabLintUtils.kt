package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import java.nio.file.FileSystems

object GitlabLintUtils {
    private fun getGitlabLintGlobStrings(): List<String> {
        val appSettings = AppSettings.instance
        if (appSettings?.gitlabLintGlobStrings != null) {
            return appSettings.gitlabLintGlobStrings!!
        }
        return listOf("*.gitlab-ci.yml", "*.gitlab-ci.yaml")
    }
    fun matchesGitlabLintGlob(text: String): Boolean {
        val globStrings = getGitlabLintGlobStrings()
        val fileSystem = FileSystems.getDefault()
        for (globString in globStrings) {
            val glob = fileSystem.getPathMatcher("glob:$globString")
            if (glob.matches(fileSystem.getPath(text))) {
                return true
            }
        }
        return false
    }
}
