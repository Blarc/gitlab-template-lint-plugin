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

    fun updateStatusWidget(project: Project, status: LintStatusEnum) {
        project.service<Pipeline>().lintStatus = status
        WindowManager.getInstance().getStatusBar(project)?.updateWidget(LintStatusWidget.ID)
    }
}
