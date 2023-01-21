package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import java.nio.file.FileSystems

object GitlabLintUtils {
    fun matchesGitlabLintGlob(text: String): Boolean {
        val globStrings = AppSettings.instance.gitlabLintGlobStrings
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
