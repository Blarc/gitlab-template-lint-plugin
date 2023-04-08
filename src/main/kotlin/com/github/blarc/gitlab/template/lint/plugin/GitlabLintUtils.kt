package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusWidget
import com.intellij.openapi.components.service
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.WindowManager
import java.nio.file.FileSystems

object GitlabLintUtils {

    fun matchesGlobs(text: String): Boolean {
        return matchesInclusionGlobs(text) && !matchesExclusionGlobs(text)
    }

    fun matchesInclusionGlobs(text: String): Boolean {
        val inclusionGlobs = AppSettings.instance.gitlabLintGlobStrings
        return matchesGlobs(text, inclusionGlobs)
    }

    fun matchesExclusionGlobs(text: String): Boolean {
        val exclusionGlobs = AppSettings.instance.exclusionGlobs
        return matchesGlobs(text, exclusionGlobs)
    }

    fun matchesGlobs(text: String, globs: List<String>): Boolean {
        val fileSystem = FileSystems.getDefault()
        for (globString in globs) {
            val glob = fileSystem.getPathMatcher("glob:$globString")
            if (glob.matches(fileSystem.getPath(text))) {
                return true
            }
        }
        return false
    }

    fun updateStatusWidget(project: Project, status: LintStatusEnum) {
        project.service<Pipeline>().lintStatus = status
        WindowManager.getInstance().getStatusBar(project)?.updateWidget(LintStatusWidget.ID)
    }
}
