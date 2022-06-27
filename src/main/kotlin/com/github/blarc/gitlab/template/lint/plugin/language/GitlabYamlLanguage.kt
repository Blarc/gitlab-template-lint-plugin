package com.github.blarc.gitlab.template.lint.plugin.language

import com.intellij.lang.Language

class GitlabYamlLanguage : Language("GitlabYaml") {

    companion object {
        val INSTANCE: GitlabYamlLanguage = GitlabYamlLanguage()
    }
}
