package com.github.blarc.gitlab.template.lint.plugin.language.psi

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlLanguage
import com.intellij.psi.tree.IElementType

class GitlabYamlElementType(debugName: String): IElementType(debugName, GitlabYamlLanguage.INSTANCE) {
}
