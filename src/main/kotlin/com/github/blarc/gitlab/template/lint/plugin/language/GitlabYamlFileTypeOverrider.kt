package com.github.blarc.gitlab.template.lint.plugin.language

import com.github.blarc.gitlab.template.lint.plugin.GitlabLintUtils
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.fileTypes.impl.FileTypeOverrider
import com.intellij.openapi.vfs.VirtualFile

class GitlabYamlFileTypeOverrider : FileTypeOverrider {
    override fun getOverriddenFileType(file: VirtualFile): FileType? {
        if (GitlabLintUtils.isGitlabYaml(file)) {
            return GitlabYamlFileType()
        }
        return null
    }
}
