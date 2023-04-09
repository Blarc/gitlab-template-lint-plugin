package com.github.blarc.gitlab.template.lint.plugin

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlFileType
import com.github.blarc.gitlab.template.lint.plugin.pipeline.Pipeline
import com.github.blarc.gitlab.template.lint.plugin.settings.AppSettings
import com.github.blarc.gitlab.template.lint.plugin.widget.LintStatusWidget
import com.github.blarc.gitlab.template.lint.plugin.widget.PipelineStatusEnum
import com.intellij.openapi.components.service
import com.intellij.openapi.fileTypes.FileTypeManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.WindowManager
import java.nio.file.FileSystems

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
            if (glob.matches(fileSystem.getPath(text))) {
                return true
            }
        }
        return false
    }

    fun updateStatusWidget(project: Project, status: PipelineStatusEnum) {
        project.service<Pipeline>().pipelineStatus = status
        WindowManager.getInstance().getStatusBar(project)?.updateWidget(LintStatusWidget.ID)
    }
}
