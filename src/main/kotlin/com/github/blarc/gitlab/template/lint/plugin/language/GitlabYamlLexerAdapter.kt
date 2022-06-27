package com.github.blarc.gitlab.template.lint.plugin.language

import com.github.blarc.gitlab.template.lint.plugin.language.GitlabYamlLexer
import com.intellij.lexer.FlexAdapter

class GitlabYamlLexerAdapter : FlexAdapter(GitlabYamlLexer(null)) {
}
