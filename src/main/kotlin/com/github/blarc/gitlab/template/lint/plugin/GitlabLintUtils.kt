package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlFileType
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusEnum
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusWidget
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.nio.file.FileSystems
import java.nio.file.InvalidPathException

object GitlabLintUtils {
    fun isGitlabYaml(file: VirtualFile): Boolean {
        return !matchesExclusionGlobs(file.path) && (matchesInclusionGlobs(file.path) || matchesFileType(file))
    }

    private fun matchesFileType(file: VirtualFile): Boolean {
        FileTypeManager.getInstance().getAssociations(GitlabYamlFileType()).forEach {
            if (it.acceptsCharSequence(file.nameSequence)) {
                return true
            }
        }
        return false
    }

    private fun matchesInclusionGlobs(text: String): Boolean {
        val inclusionGlobs = AppSettings.instance.gitlabLintGlobStrings
        return matchesGlobs(text, inclusionGlobs)
    }

    private fun matchesExclusionGlobs(text: String): Boolean {
        val exclusionGlobs = AppSettings.instance.exclusionGlobs
        return matchesGlobs(text, exclusionGlobs)
    }

    private fun matchesGlobs(text: String, globs: List<String>): Boolean {
        val fileSystem = FileSystems.getDefault()
        for (globString in globs) {
            val glob = fileSystem.getPathMatcher("glob:$globString")
            try {
                if (glob.matches(fileSystem.getPath(text))) {
                    return true
                }
            }
            catch (e: InvalidPathException) {
                // Ignore invalid paths
            }
        }
        return false
    }

    fun updateStatusWidget(project: Project, status: LintStatusEnum) {
        project.service<Pipeline>().lintStatus = status
        WindowManager.getInstance().getStatusBar(project)?.updateWidget(LintStatusWidget.ID)
    }
}
