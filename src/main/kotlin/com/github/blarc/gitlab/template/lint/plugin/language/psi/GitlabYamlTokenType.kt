package com.github.blarc.gitlab.template.lint.plugin.language.psi

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlLanguage
import com.intellij.psi.tree.IElementType

class GitlabYamlTokenType(debugName: String) : IElementType(debugName, GitlabYamlLanguage.INSTANCE) {
    override fun toString(): String {
        return "GitlabYamlTokenType." + super.toString()
    }
}
