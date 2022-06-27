package com.github.blarc.gitlab.template.lint.plugin.language.psi

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlFileType
import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlLanguage
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider

class GitlabYamlFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, GitlabYamlLanguage.INSTANCE) {
    override fun getFileType(): FileType {
        return GitlabYamlFileType.INSTANCE
    }
}
